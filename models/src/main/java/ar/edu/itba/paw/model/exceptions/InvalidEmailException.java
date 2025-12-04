package ar.edu.itba.paw.model.exceptions;

public class InvalidEmailException extends InvalidOperationException{
    private static final String message = "The email provided does not belong to any account registered.";

    public InvalidEmailException() {
        super(message);
    }

    @Override
    public String getMessage() {
        return message;
    }

}

