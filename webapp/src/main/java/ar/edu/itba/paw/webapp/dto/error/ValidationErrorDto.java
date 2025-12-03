package ar.edu.itba.paw.webapp.dto.error;

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
    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
