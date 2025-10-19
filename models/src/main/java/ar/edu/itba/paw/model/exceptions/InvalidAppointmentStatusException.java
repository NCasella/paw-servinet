package ar.edu.itba.paw.model.exceptions;

public class InvalidAppointmentStatusException extends InvalidOperationException{
    public InvalidAppointmentStatusException() {
        super("Invalid appointment status");
    }

}
