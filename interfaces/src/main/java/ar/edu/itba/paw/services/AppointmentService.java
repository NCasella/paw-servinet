package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {

    Optional<Appointment> findById(long id);

    Optional<List<Appointment>> getAllUpcomingServiceAppointments(long serviceid);

    Appointment create(long serviceid, String name, String surname, String email, String location, String telephone, String date);
    List<AppointmentInfo> getAppointmentsByUser(long userid);

    long confirmAppointment(long appointmentid);

    long cancelAppointment(long appointmentid);

    long denyAppointment(long appointmentid);

}
