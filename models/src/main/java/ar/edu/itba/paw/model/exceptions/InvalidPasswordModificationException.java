package ar.edu.itba.paw.model.exceptions;

public class InvalidPasswordModificationException extends InvalidOperationException{
    private static final String message = "The password provided does not belong to the user. The old password must match in order to allow the password modification.";

    public InvalidPasswordModificationException() {
        super(message);
    }

    @Override
    public String getMessage() {
        return message;
    }

}
