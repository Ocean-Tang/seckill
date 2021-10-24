package com.study.seckill.exception;

import com.study.seckill.vo.RespBean;
import com.study.seckill.vo.RespBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理类
 * @author 黄灿杰
 * @date 2021/10/7
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public RespBean exceptionHandler(Exception e) {
        if(e instanceof GlobalException) {
            GlobalException ex = (GlobalException) e;
            return RespBean.error(ex.getRespBeanEnum());
        }else if (e instanceof BindException) {

            BindException ex = (BindException) e;
            RespBean respBean = RespBean.error(RespBeanEnum.BIND_ERROR);
            // 设置异常返回结果，使得前端能够接收正常处理
            respBean.setMessage(respBean.getMessage() + ":"
                    + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return respBean;
        }
        //默认返回的错误类型
        return RespBean.error(RespBeanEnum.ERROR);
    }

}
