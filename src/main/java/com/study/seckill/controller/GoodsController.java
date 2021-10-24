package com.study.seckill.controller;

import com.study.seckill.pojo.User;
import com.study.seckill.service.IGoodsService;
import com.study.seckill.vo.DetailVo;
import com.study.seckill.vo.GoodsVo;
import com.study.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.ISpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author 黄灿杰
 * @date 2021/10/8
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /**
     * 商品列表页
     * windows 优化前： 吞吐量：214.3/s
     * Linux 优化前：   吞吐量：154/s
     * 使用页面缓存
     * 吞吐量： 162.6
     * @param user
     * @param model
     * @return
     */
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(User user, Model model, HttpServletRequest request,
                         HttpServletResponse response) {

        // 获取 goodsList.html 页面缓存
        ValueOperations opsForValue = redisTemplate.opsForValue();
        String html = (String) opsForValue.get("goodsList");
        if (StringUtils.hasText(html)) {
            return html;
        }
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());

        WebContext context = new WebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", context);
        if (StringUtils.hasText(html)) {
           opsForValue.set("goodsList", html, 1, TimeUnit.MINUTES);
        }

        return html;
    }


    /**
     * 跳转到 静态化 商品详情页
     * @return
     */
    @RequestMapping(value = "/toDetail/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail(Model model, User user, @PathVariable long goodsId,
                           HttpServletRequest request, HttpServletResponse response){
        ValueOperations opsForValue = redisTemplate.opsForValue();
        String goodsDetail = (String) opsForValue.get("goodsDetail:" + goodsId);
        if(StringUtils.hasText(goodsDetail)) {
            return goodsDetail;
        }

        GoodsVo goods = goodsService.findGoodsVoById(goodsId);
        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        Date nowDate = new Date();
        long remainSeconds = -1;
        // 秒杀状态
        int secKillStatus = 0;
        // 秒杀未开始
        if(nowDate.before(startDate)) {
            secKillStatus = 0;
            remainSeconds = (startDate.getTime() - nowDate.getTime())/1000;
        }else if(nowDate.after(endDate)) {
            // 秒杀结束
            secKillStatus = 2;
            remainSeconds = -1;
        }else {
            // 秒杀进行中
            secKillStatus = 1;
            remainSeconds = 0;
        }

        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("user", user);
        model.addAttribute("goods",goods);

        WebContext webContext = new WebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap());
        String html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);

        if (StringUtils.hasText(html)) {
            opsForValue.set("goodsDetail:" + goodsId, html, 60, TimeUnit.SECONDS);
        }
        return html;

    }

    @RequestMapping( "/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail2(Model model, User user,@PathVariable long goodsId){

        GoodsVo goods = goodsService.findGoodsVoById(goodsId);
        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        Date nowDate = new Date();
        long remainSeconds = -1;
        // 秒杀状态
        int secKillStatus = 0;
        // 秒杀未开始
        if(nowDate.before(startDate)) {
            secKillStatus = 0;
            remainSeconds = (startDate.getTime() - nowDate.getTime())/1000;
        }else if(nowDate.after(endDate)) {
            // 秒杀结束
            secKillStatus = 2;
            remainSeconds = -1;
        }else {
            // 秒杀进行中
            secKillStatus = 1;
            remainSeconds = 0;
        }

        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("user", user);
        model.addAttribute("goods",goods);

        DetailVo detail = new DetailVo(goods, user, remainSeconds, secKillStatus);

        return RespBean.success(detail);

    }

}
