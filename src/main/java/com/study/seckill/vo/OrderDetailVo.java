package com.study.seckill.vo;

import com.study.seckill.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 黄灿杰
 * @date 2021/10/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailVo {

    private Order order;
    private GoodsVo goodsVo;

}
