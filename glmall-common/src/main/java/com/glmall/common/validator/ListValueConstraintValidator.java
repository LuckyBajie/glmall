package com.glmall.common.validator;

import com.glmall.common.validator.annotations.ListValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Number> {

    private Set<Number> set = null;

    /**
     * 初始化方法
     *
     * @param constraintAnnotation
     */
    @Override
    public void initialize(ListValue constraintAnnotation) {
        set = new HashSet<>();
        int[] values = constraintAnnotation.value();
        for (int value : values) {
            set.add(Integer.valueOf(value));
        }
    }

    /**
     * 校验规则
     *
     * @param number
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(Number number, ConstraintValidatorContext constraintValidatorContext) {
        return set.contains(number);
    }
}
