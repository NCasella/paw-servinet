package ar.edu.itba.paw.webapp.validation;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;


import javax.imageio.ImageIO;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ValidImageFileValidator implements ConstraintValidator<ValidImageFile, byte[]> {
    private static final int MAX_SIZE=5*1024*1024;
   @Override
   public boolean isValid(byte[] file, ConstraintValidatorContext context) {
    if(file==null || file.length==0){
        return false;
    }
    if(file.length>MAX_SIZE){
        return false;
    }
    try(InputStream in=new ByteArrayInputStream(file)){
        return ImageIO.read(in)!=null;
    }catch (IOException e){
        return false;
    }

   }

}
