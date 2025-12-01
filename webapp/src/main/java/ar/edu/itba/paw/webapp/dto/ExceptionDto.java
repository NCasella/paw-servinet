package ar.edu.itba.paw.webapp.dto;

import lombok.Data;

@Data
public class ExceptionDto {

    private String messageException;

    public static ExceptionDto fromException(Exception e){
        ExceptionDto exceptionDto=new ExceptionDto();
        exceptionDto.setMessageException(e.getMessage());
        return exceptionDto;
    }
    private ExceptionDto(){}
}
