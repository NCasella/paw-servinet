package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;

import java.time.LocalDateTime;
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

    Long getServicesRequestedAppointmentCount(Collection<Long> serviceIds, DateIntervalFilter filter);

    List<Appointment> getAllUpcomingUserAppointments(long userid, boolean confirmed, int page);

    long getUserAppointmentCount(long userid, boolean confirmed);

    List<Appointment> getPreviousUserAppointments(long userid, int page);

    long getPreviousUserAppointmentCount(long userid);

    long getPageCount(long count);

    Appointment create(long serviceid, String name, String surname, String email, String location, String telephone, String date, String description);

    long confirmAppointment(long appointmentid);

    long cancelAppointment(long appointmentid);

    long denyAppointment(long appointmentid);

    Appointment create(long serviceid, long userid, String location, LocalDateTime startDate, String description);

    void changePendingAppointmentStatus(long appointmentid, AppointmentStatus status);

    PagedList<Appointment> getUserAppointments(long userId,AppointmentStatus status,int page);

    PagedList<Appointment> getBusinessAppointments(long businessId,AppointmentStatus status,int page);
}

