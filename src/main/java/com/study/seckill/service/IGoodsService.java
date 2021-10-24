package com.study.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.seckill.pojo.Goods;
import com.study.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 阿杰
 * @since 2021-10-09
 */
public interface IGoodsService extends IService<Goods> {

    /**
     * 获取所有商品信息
     * @return
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 根据Id 查询商品
     * @param goodsId
     * @return
     */
    GoodsVo findGoodsVoById(long goodsId);
}
