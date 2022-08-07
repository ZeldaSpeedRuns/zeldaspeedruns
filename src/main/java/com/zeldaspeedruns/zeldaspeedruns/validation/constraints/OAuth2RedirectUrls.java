package com.zeldaspeedruns.zeldaspeedruns.validation.constraints;

import com.zeldaspeedruns.zeldaspeedruns.validation.OAuth2RedirectUrlsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = OAuth2RedirectUrlsValidator.class)
public @interface OAuth2RedirectUrls {
    String message() default "{api-client.error.redirect-urls.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
