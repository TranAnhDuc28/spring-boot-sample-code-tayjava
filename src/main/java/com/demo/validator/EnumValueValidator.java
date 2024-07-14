package com.demo.validator;

import com.demo.validator.EnumValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.stream.Stream;

public class EnumValueValidator implements ConstraintValidator<EnumValue, CharSequence> {
    private List acceptedValues;

    // convert enum to list
    @Override
    public void initialize(EnumValue enumValue) {
        acceptedValues = Stream.of(enumValue.enumClass().getEnumConstants())
                .map(Enum::name)
                .toList();
    }

    // so sánh giá trị có nằm trong list đó hay không
    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if(value == null) {
            return true;
        }
        return acceptedValues.contains(value.toString().toUpperCase());
    }
}
