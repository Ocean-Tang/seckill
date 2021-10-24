package com.study.seckill.vo;

import com.study.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 页面详情返回对象
 * @author 黄灿杰
 * @date 2021/10/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailVo {

    private GoodsVo goodsVo;
    private User user;
    private long remainSeconds;
    private int secKillStatus;

}
