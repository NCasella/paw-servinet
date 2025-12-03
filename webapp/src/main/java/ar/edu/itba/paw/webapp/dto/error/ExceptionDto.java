package ar.edu.itba.paw.webapp.dto.error;

public class ExceptionDto {

    private String messageException;


    public static ExceptionDto fromException(Exception e){
        ExceptionDto exceptionDto=new ExceptionDto();
        exceptionDto.setMessageException(e.getMessage());
        return exceptionDto;
    }
    private ExceptionDto(){}
    public String getMessageException(){return this.messageException;}

    public void setMessageException(String message){this.messageException=message;}
}
