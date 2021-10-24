package com.study.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.seckill.pojo.Goods;
import com.study.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 阿杰
 * @since 2021-10-09
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVo> findGoodsVo();

    /**
     * 根据商品Id 查询商品
     * @param goodsId
     * @return
     */
    GoodsVo findGoodsVoById(long goodsId);
}
