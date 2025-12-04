package ar.edu.itba.paw.model.exceptions;

public class InvalidUsernameException extends InvalidOperationException{
    private static final String message = "The username provided is already in use.";

    public InvalidUsernameException() {
        super(message);
    }

    @Override
    public String getMessage() {
        return message;
    }

}
