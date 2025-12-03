package ar.edu.itba.paw.webapp.dto.error;

import lombok.Data;

@Data
public class ValidationErrorDto {

    private String message;
    private String path;

    public static ValidationErrorDto fromError(String message,String path){
        ValidationErrorDto error=new ValidationErrorDto();
        error.setMessage(message);
        error.setPath(path);
        return error;
    }
    private ValidationErrorDto(){}
}
