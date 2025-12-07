package ar.edu.itba.paw.model.exceptions;

public class AppointmentWasCancelled extends InvalidOperationException {
  static String message = "The appointment was already cancelled";
  public AppointmentWasCancelled() {
    super(message);
  }
}
