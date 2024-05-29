package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {

    Optional<Appointment> findById(long id);

    List<Appointment> getAllUpcomingServiceAppointments(long serviceid);

    List<Appointment> getAllUpcomingServicesAppointments(Collection<Long> serviceIds, boolean confirmed);

    List<Appointment> getAllUpcomingUserAppointments(long userid, boolean confirmed);

    Appointment create(long serviceid, String name, String surname, String email, String location, String telephone, String date);

    long confirmAppointment(long appointmentid);

    long cancelAppointment(long appointmentid);

    long denyAppointment(long appointmentid);

}
