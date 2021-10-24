package com.study.seckill.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.study.seckill.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * User表的Mapper接口
 * @author 黄灿杰
 * @date 2021/10/7
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
