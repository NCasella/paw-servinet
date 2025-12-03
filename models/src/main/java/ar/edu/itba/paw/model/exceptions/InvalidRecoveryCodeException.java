package ar.edu.itba.paw.model.exceptions;

public class InvalidRecoveryCodeException extends InvalidOperationException{
    private static final String message = "The code provided is invalid.";

    public InvalidRecoveryCodeException() {
        super(message);
    }

    @Override
    public String getMessage() {
        return message;
    }

}
