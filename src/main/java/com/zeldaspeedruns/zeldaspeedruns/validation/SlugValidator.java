package com.zeldaspeedruns.zeldaspeedruns.validation;

import com.zeldaspeedruns.zeldaspeedruns.validation.constraints.Slug;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class SlugValidator implements ConstraintValidator<Slug, String> {
    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-z\\d]+(?:-[a-z\\d]+)*$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
            return SLUG_PATTERN.matcher(value).matches();
        }
        return true;
    }
}
