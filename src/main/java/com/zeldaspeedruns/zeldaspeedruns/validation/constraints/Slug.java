package com.zeldaspeedruns.zeldaspeedruns.validation.constraints;

import com.zeldaspeedruns.zeldaspeedruns.validation.SlugValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Constraint(validatedBy = SlugValidator.class)
public @interface Slug {
    String message() default "must be a valid slug";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
