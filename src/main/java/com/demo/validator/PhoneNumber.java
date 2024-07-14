package com.demo.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented // định nghĩa annotation
@Constraint(validatedBy = PhoneValidator.class) // ràng buộc là gọi sang PhoneValidator
@Target({ElementType.METHOD, ElementType.FIELD}) // áp dụng đối với method và field
@Retention(RetentionPolicy.RUNTIME) // xử lý annotation trong lúc runtime
public @interface PhoneNumber {

    String message() default "Invalid phone number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
