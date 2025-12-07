package ar.edu.itba.paw.model.exceptions;

public class InvalidFilterException extends InvalidOperationException{
    public InvalidFilterException() {
        super("Invalid filter");
    }

    public InvalidFilterException(String message) {
        super("Invalid filter: " + message);
    }
}
