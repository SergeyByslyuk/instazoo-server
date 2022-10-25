package com.sergeytechnologies.instazoo.annotations;


import com.sergeytechnologies.instazoo.service.validations.PasswordMatchesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Documented
public @interface PasswordMatches {
    String message() default "Password do not matches";

    Class<?>[] groups() default{};

    Class<? extends Payload>[] payload() default {};
}
