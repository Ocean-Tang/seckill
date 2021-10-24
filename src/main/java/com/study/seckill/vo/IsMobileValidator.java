package com.study.seckill.vo;

import com.study.seckill.utils.ValidatorUtil;
import com.study.seckill.validator.IsMobile;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author 黄灿杰
 * @date 2021/10/7
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 如果value 必定有值
        if(required) {
            return ValidatorUtil.isMobile(value);
        } else {
            if(!StringUtils.hasText(value)) {
                return true;
            }else {
                return ValidatorUtil.isMobile(value);
            }
        }

    }
}
