package com.study.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.study.seckill.config.AccessLimit;
import com.study.seckill.exception.GlobalException;
import com.study.seckill.pojo.*;
import com.study.seckill.rabbitmq.MQSender;
import com.study.seckill.service.IGoodsService;
import com.study.seckill.service.IOrderService;
import com.study.seckill.service.ISeckillOrderService;
import com.study.seckill.utils.JsonUtil;
import com.study.seckill.vo.GoodsVo;
import com.study.seckill.vo.RespBean;
import com.study.seckill.vo.RespBeanEnum;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀功能
 * @author 黄灿杰
 * @date 2021/10/9
 */
@Controller
@Slf4j
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisScript<Long> redisScript;
    /**
     * 内存标记 商品库存
      */
    private Map<Long, Boolean> emptyStockMap = new HashMap<>();

    /**
     * 秒杀商品
     * 10000 个线程
     * windows 优化前：125/s
     * Linux 优化前：212/s
     * @return
     */
    @RequestMapping("/doSeckill2")
    public String doSeckill(User user, Model model, long goodsId) {

        // 如果用户没有登录
        if(null == user) {
            return "login";
        }

        model.addAttribute("user", user);
        GoodsVo goods = goodsService.findGoodsVoById(goodsId);

        // 如果没有库存了
        if(goods.getStockCount() < 1) {
            model.addAttribute("errorMsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "seckillFail";
        }

        // 用户是否重复购买
        // 或者使用sql 语句，写到mapper文件里
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId())
                .eq("goods_id", goodsId));
        if(seckillOrder != null) {
            model.addAttribute("errorMsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "seckillFail";
        }

        // 处理秒杀
        Order order = orderService.seckill(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods",goods);
        return "orderDetail";
    }

    /**
     * 秒杀按钮静态化接口
     *
     * 优化前 ： 212s
     * 缓存后： 224s
     * 优化接口后： 604s
     * @param user
     * @param model
     * @param goodsId
     * @return
     */
    @ResponseBody
    @PostMapping("/{path}/doSeckill")
    public RespBean doSeckill2(@PathVariable String path,
                               User user, Model model, long goodsId) {
        // 如果用户没有登录
        if(null == user) {
            throw new GlobalException(RespBeanEnum.SESSION_ERROR);
        }

        // 判断秒杀接口是否有效
        boolean check = orderService.checkPath(user, goodsId, path);
        if (!check) {
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }

        // 如果商品已经卖完了,直接返回，减少Redis访问
        if ( emptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 用户是否重复购买
        SeckillOrder seckillOrder =
                ((SeckillOrder) valueOperations.get("order:" + user.getId() + ":" + goodsId));
        System.out.println(seckillOrder);
        if(seckillOrder != null) {
            model.addAttribute("errorMsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            throw new GlobalException(RespBeanEnum.REPEATE_ERROR);
        }

        // 预减库存 -> 使用LUA 脚本完成，加锁，实现类似事务的操作，LUA脚本可以放在服务器，提高服务器吞吐量
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
       /* Long stock = (Long)redisTemplate.execute(redisScript,
                Collections.singletonList("seckillGoods:" + goodsId),
                Collections.EMPTY_LIST);*/
        if(stock < 0) {
            // 如果商品已经卖完
            emptyStockMap.put(goodsId, true);
            valueOperations.increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        // 处理秒杀，发送秒杀消息到Rabbitmq
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));

        return RespBean.success(0);
    }

    /**
     * 获取秒杀结果
     * @param goodsId
     * @return orderId -1 表示秒杀失败，0 表示排队中
     */
    @GetMapping("/result")
    @ResponseBody
    public RespBean getResult(User user, long goodsId) {

        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);

    }

    /**
     * 获取秒杀地址
     * 不同用户，不同商品，秒杀地址不同
     * @param user
     * @param goodsId
     * @return
     */
    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
    @RequestMapping("/path")
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request) {

        if (null == user) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        // 接口限流，5次
        /*ValueOperations valueOperations = redisTemplate.opsForValue();

        String uri = request.getRequestURI();
        System.out.println(uri);
        Integer count = (Integer) valueOperations.get(uri + ":" + user.getId());
        if (null == count) {
            valueOperations.set(uri + ":" + user.getId(), 1, 5, TimeUnit.SECONDS);
        }else if( count < 5) {
            valueOperations.increment(uri + ":" + user.getId());
        }else {
            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REACHED);
        }*/

        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check) {
            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        }

        String path = orderService.createPath(user, goodsId);
        return RespBean.success(path);
    }

    /**
     * 图形验证码接口
     * @param goodsId
     * @param user
     * @param response
     */
    @RequestMapping("/captcha")
    public void verifyCode(Long goodsId, User user, HttpServletResponse response) {

        // 设置返回类型， gif 图片
        response.setContentType("image/gif");
        // 设置 不缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        // 设置过期时间，永不过期
        response.setDateHeader("Expires", 0);

        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId,
                captcha.text(), 300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            //e.printStackTrace();
            log.error("验证码生成失败！ " + e.getMessage());
        }
    }

    /**
     * 系统初始化完毕后，将商品库存信息存入 Redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        // 没有商品信息
        if(list.isEmpty()) {
            return;
        }
        // 加载库存到 Redis 中
        list.forEach(goods -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goods.getId(), goods.getStockCount());
            emptyStockMap.put(goods.getId(), false);
        });
    }
}
