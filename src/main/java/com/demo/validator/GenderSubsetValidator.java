package com.demo.validator;

import com.demo.util.Gender;
import com.demo.validator.GenderSubset;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class GenderSubsetValidator implements ConstraintValidator<GenderSubset, Gender> {
    private Gender[] genders;

    @Override
    public void initialize(GenderSubset constraint) {
        // gán tất cả các giá trị enum muốn validate được truyền vào
        this.genders = constraint.anyOf();
    }

    @Override
    public boolean isValid(Gender value, ConstraintValidatorContext constraintValidatorContext) {
        return value == null || Arrays.asList(genders).contains(value);
    }
}
