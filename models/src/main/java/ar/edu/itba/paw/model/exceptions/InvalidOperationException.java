package ar.edu.itba.paw.model.exceptions;

public class InvalidOperationException extends RuntimeException {

    public InvalidOperationException(String argument){
        super(argument);
    }

}
