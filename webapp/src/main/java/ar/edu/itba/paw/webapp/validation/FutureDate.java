package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
@Constraint(validatedBy = FutureDateValidator.class)
@Target({ ElementType.TYPE ,FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureDate {
    String message() default "FutureDate.appointmentForm.date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        EmailNotUsed[] emails();
    }
}
