package ar.edu.itba.paw.webapp.validation;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;



import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class ValidImageFileValidator implements ConstraintValidator<ValidImageFile, FormDataBodyPart> {
    @Override
    public boolean isValid(FormDataBodyPart file, ConstraintValidatorContext context) {
        return file!=null && file.getMediaType()!=null && file.getEntityAs(byte[].class).length < 5*1024*1024 && file.getMediaType().getType().toLowerCase().contains("image");
    }
}
