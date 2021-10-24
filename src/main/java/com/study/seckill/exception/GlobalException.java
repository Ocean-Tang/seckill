package com.study.seckill.exception;

import com.study.seckill.vo.RespBean;
import com.study.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常
 * @author 黄灿杰
 * @date 2021/10/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalException extends RuntimeException{

    private RespBeanEnum respBeanEnum;

}
