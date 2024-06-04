package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AppointmentService {

    Optional<Appointment> findById(long id);

    List<Appointment> getAllUpcomingServiceAppointments(long serviceid);

    List<Appointment> getAllUpcomingServicesAppointments(Collection<Long> serviceIds, boolean confirmed, int page);

    long getServicesAppointmentCount(Collection<Long> serviceIds, boolean confirmed);

    List<Pair<Long,Long>> getServicesFinishedAppointmentCount(Collection<Long> serviceIds, DateIntervalFilter filter);

    List<Appointment> getAllUpcomingUserAppointments(long userid, boolean confirmed, int page);

    long getUserAppointmentPageCount(long userid, boolean confirmed);

    List<Appointment> getPreviousUserAppointments(long userid, int page);

    long getPreviousUserAppointmentPageCount(long userid);

    Appointment create(long serviceid, String name, String surname, String email, String location, String telephone, String date, String description);

    long confirmAppointment(long appointmentid);

    long cancelAppointment(long appointmentid);

    long denyAppointment(long appointmentid);

}
