package ar.edu.itba.paw.model.exceptions;

public class ImageNonExistentException extends InvalidOperationException{
    private static final String MESSAGE="Service image non existent ";
    public ImageNonExistentException(){super(MESSAGE);}
}
