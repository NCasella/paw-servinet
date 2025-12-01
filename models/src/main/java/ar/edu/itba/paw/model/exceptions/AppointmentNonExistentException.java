package ar.edu.itba.paw.model.exceptions;

public class AppointmentNonExistentException extends NotFoundException{

    private static final String message = "The appointment doesn't exist";

    public AppointmentNonExistentException() {
        super(message);
    }


}
