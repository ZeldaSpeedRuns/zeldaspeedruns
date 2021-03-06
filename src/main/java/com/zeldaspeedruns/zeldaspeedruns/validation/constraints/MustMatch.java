package com.zeldaspeedruns.zeldaspeedruns.validation.constraints;

import com.zeldaspeedruns.zeldaspeedruns.validation.FieldMatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(MustMatch.List.class)
@Constraint(validatedBy = FieldMatchValidator.class)
public @interface MustMatch {
    String message() default "{com.zeldaspeedruns.zeldaspeedruns.validation.constraints.MustMatch.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String field();

    String matchedField();


    @Documented
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @interface List {
        MustMatch[] value();
    }
}
