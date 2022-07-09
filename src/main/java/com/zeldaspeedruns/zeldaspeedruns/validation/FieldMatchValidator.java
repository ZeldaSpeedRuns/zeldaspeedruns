package com.zeldaspeedruns.zeldaspeedruns.validation;


import com.zeldaspeedruns.zeldaspeedruns.validation.constraints.MustMatch;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class FieldMatchValidator implements ConstraintValidator<MustMatch, Object> {
    static final String MESSAGE_PATH = "{com.zeldaspeedruns.zeldaspeedruns.validation.constraints.MustMatch.message}";

    private String fieldName;
    private String matchedFieldName;
    private String message;

    @Override
    public void initialize(MustMatch constraintAnnotation) {
        this.fieldName = constraintAnnotation.field();
        this.matchedFieldName = constraintAnnotation.matchedField();
        this.message = constraintAnnotation.message();

        if (this.message.equals(MESSAGE_PATH)) {
            this.message = "must match " + matchedFieldName;
        }
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext context) {
        BeanWrapper wrapper = new BeanWrapperImpl(o);
        final Object field = wrapper.getPropertyValue(fieldName);
        final Object matchedField = wrapper.getPropertyValue(matchedFieldName);
        boolean valid = Objects.equals(field, matchedField);

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(fieldName)
                    .addBeanNode()
                    .addConstraintViolation();
        }

        return valid;
    }
}
