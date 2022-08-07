package com.zeldaspeedruns.zeldaspeedruns.validation;

import com.zeldaspeedruns.zeldaspeedruns.validation.constraints.OAuth2RedirectUrls;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

public class OAuth2RedirectUrlsValidator implements ConstraintValidator<OAuth2RedirectUrls, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        var values = value.lines()
                .filter(s -> !s.isEmpty())
                .map(String::trim)
                .collect(Collectors.toSet());

        if (values.isEmpty()) {
            return false;
        }

        for (var v : values) {
            if (!v.startsWith("http://localhost") && !v.startsWith("https://")) {
                return false;
            }

            try {
                new URL(v);
            } catch (MalformedURLException e) {
                return false;
            }
        }

        return true;
    }
}
