package com.demo.validator;

import com.demo.validator.EnumPatternValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;

/**
 *  Ưu điểm của phương pháp này là có thể áp dụng chung cho tất cả các enum khác nhau
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, METHOD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Constraint(validatedBy = EnumPatternValidator.class)
public @interface EnumPattern {
    String name();
    String regexp();
    String message() default "{name} must match {regexp}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
