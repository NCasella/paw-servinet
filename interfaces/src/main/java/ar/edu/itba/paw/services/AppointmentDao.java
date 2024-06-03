package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Appointment;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AppointmentDao {

    Optional<Appointment> findById(long id);

    List<Appointment> getAllUpcomingServiceAppointments(long serviceid);

    List<Appointment> getAllUpcomingServicesAppointments(Collection<Long> servicesIds, boolean confirmed);

    List<Appointment> getAllUpcomingUserAppointments(long userid, boolean confirmed, int page, int pageSize);

    List<Appointment> getPreviousUserAppointments(long userid, int page, int pageSize);

    long getUserAppointmentCount(long userid, boolean confirmed);

    long getPreviousUserAppointmentCount(long userid);

    Appointment create(long serviceid, long userid, LocalDateTime startDate, LocalDateTime endDate, String location, String description);

    void confirmAppointment(long appointmentid);

    void cancelAppointment(long appointmentid);
}
