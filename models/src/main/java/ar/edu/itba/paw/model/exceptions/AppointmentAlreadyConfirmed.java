package ar.edu.itba.paw.model.exceptions;

public class AppointmentAlreadyConfirmed extends InvalidOperationException{

    private static final String message = "The appointment had already been confirmed";

    public AppointmentAlreadyConfirmed() {
        super(message);
    }

    @Override
    public String getMessage() {
        return String.format(message);
    }


}

