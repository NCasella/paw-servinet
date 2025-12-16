package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailNotUsedValidator implements ConstraintValidator<EmailNotUsed, String> {
    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return userService.findByEmail(value).isEmpty();
    }
}
