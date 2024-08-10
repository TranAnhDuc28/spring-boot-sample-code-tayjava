package com.demo.validator;

import com.demo.enums.Gender;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Ưu điểm của phương pháp này là có thể chỉ định cụ thể một vài giá trị cần validate trong enum thay vì tất cả.
 */

@Documented
@Retention(RUNTIME)
@Target({FIELD, METHOD})
@Constraint(validatedBy = GenderSubsetValidator.class)
public @interface GenderSubset {

    Gender[] anyOf(); // truyền vào các enum gender
    String message() default "must be any of {anyOf}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
