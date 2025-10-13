package ar.edu.itba.paw.model.exceptions;

public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(String message) {
        super(message);
    }
    public ForbiddenOperationException() {
        super("Forbidden operation");
    }
}
