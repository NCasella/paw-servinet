package ar.edu.itba.paw.model.exceptions;

public class AppointmentNonExistentException extends NotFoundException{

    private static final String message = "The appointment doesn't exist";
    private static final String APPOINTMENT_NON_EXISTENT = "turnonoexiste";

    public AppointmentNonExistentException() {
        super(APPOINTMENT_NON_EXISTENT);
    }

    @Override
    public String getMessage() {
        return String.format(message);
    }


}
