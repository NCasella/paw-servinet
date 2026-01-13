package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Appointment;

import ar.edu.itba.paw.model.Pair;
import ar.edu.itba.paw.model.Service;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.exceptions.AppointmentNonExistentException;
import ar.edu.itba.paw.services.AppointmentDao;
import org.springframework.stereotype.Repository;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class AppointmentDaoJpa implements AppointmentDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Appointment> findById(long id) {
        return Optional.ofNullable(em.find(Appointment.class, id));
    }


    @Override
    public List<Appointment> getAllUpcomingServiceAppointments(long serviceid) {
        TypedQuery<Appointment> query = em.createQuery("from Appointment where serviceAppointed.id = :serviceid and  cancelled = FALSE and startDate > :currentDate ", Appointment.class);
        query.setParameter("serviceid",serviceid);
        query.setParameter("currentDate", LocalDateTime.now());
        return query.getResultList();
    }

    @Override
    public List<Appointment> getAllUpcomingServicesAppointments(Collection<Long> servicesIds, boolean confirmed, int page, int pageSize) {
        Query query = em.createNativeQuery("SELECT appointmentid from appointments where serviceid in :serviceids and confirmed = :confirmed and cancelled = FALSE and startDate > :currentDate order by startDate, appointmentid");
        query.setParameter("serviceids",List.copyOf(servicesIds));
        query.setParameter("currentDate", LocalDateTime.now());
        query.setParameter("confirmed", confirmed);
        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);

        final List<Long> idList = (List<Long>) query.getResultList()
                .stream().map(n -> ((Number)n).longValue()).collect(Collectors.toList());
        TypedQuery<Appointment> queryPage = em.createQuery("from Appointment where id in :idList order by startDate, id", Appointment.class);
        queryPage.setParameter("idList", idList);

        return queryPage.getResultList();
    }

    @Override
    public long getServicesAppointmentCount(Collection<Long> servicesIds, boolean confirmed ){
        TypedQuery<Long> query = em.createQuery("SELECT count(id) FROM Appointment as a where serviceAppointed.id in :serviceids and confirmed = :confirmed and cancelled = FALSE and startDate > :currentDate ", Long.class);
        query.setParameter("currentDate", LocalDateTime.now());
        query.setParameter("serviceids", servicesIds);
        query.setParameter("confirmed", confirmed);
        return query.getResultList().getFirst();
    }

    @Override
    public List<Pair<Long,Long>> getServicesFinishedAppointmentCount(Collection<Long> servicesIds, LocalDateTime startDate, LocalDateTime endDate){
        TypedQuery<Tuple> query = em.createQuery("SELECT serviceAppointed.id as serviceId,count(id) as countAppointments FROM Appointment where serviceid in :serviceids and confirmed = TRUE and cancelled = FALSE and startDate between :startDate and :endDate " +
                "group by serviceid order by countAppointments desc", Tuple.class);
        query.setParameter("serviceids", servicesIds);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        List<Pair<Long,Long>> result = query.getResultList().stream()
                .map(tuple -> new Pair<>(
                 ((Number) tuple.get("serviceId")).longValue(),
                 ((Number) tuple.get("countAppointments")).longValue()
        )).collect(Collectors.toList());
        return result;
    }

    @Override
    public Long getServicesRequestedAppointmentCount(Collection<Long> servicesIds, LocalDateTime startDate){
        TypedQuery<Long> query = em.createQuery("SELECT count(id) FROM Appointment where serviceAppointed.id in :serviceids and confirmed = FALSE and cancelled = FALSE and startDate > :startDate",Long.class);
        query.setParameter("serviceids", servicesIds);
        query.setParameter("startDate", startDate);
        return query.getSingleResult();
    }

    @Override
    public List<Appointment> getAllUpcomingUserAppointments(long userid, boolean confirmed, int page, int pageSize) {
        Query query = em.createNativeQuery("SELECT appointmentid FROM appointments as a where a.userId = :userid and confirmed = :confirmed and cancelled = FALSE and startDate > :currentDate order by startDate,appointmentid");
        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);
        query.setParameter("currentDate", LocalDateTime.now());
        query.setParameter("userid", userid);
        query.setParameter("confirmed", confirmed);

        final List<Long> idList = (List<Long>) query.getResultList()
                .stream().map(n -> ((Number)n).longValue()).collect(Collectors.toList());
        TypedQuery<Appointment> queryPage = em.createQuery("from Appointment where id in :idList order by startDate,id", Appointment.class);
        queryPage.setParameter("idList",idList);

        return queryPage.getResultList();
    }

    @Override
    public long getUserAppointmentCount(long userid, boolean confirmed ){
        TypedQuery<Long> query = em.createQuery("SELECT count(id) FROM Appointment as a where appointedBy.userId = :userid and confirmed = :confirmed and cancelled = FALSE and startDate > :currentDate ", Long.class);
        query.setParameter("currentDate", LocalDateTime.now());
        query.setParameter("userid", userid);
        query.setParameter("confirmed", confirmed);
        return query.getResultList().getFirst();
    }

    @Override
    public List<Appointment> getPreviousUserAppointments(long userid, int page, int pageSize) {

        Query nativeQuery = em.createNativeQuery("SELECT appointmentid FROM appointments as a where a.userid = :userid and confirmed = TRUE and cancelled = FALSE and startDate < :currentDate order by startDate desc, appointmentid desc");
        nativeQuery.setFirstResult((page - 1) * pageSize);
        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setParameter("currentDate", LocalDateTime.now());
        nativeQuery.setParameter("userid", userid);

        final List<Long> idList = (List<Long>) nativeQuery.getResultList()
                .stream().map(n -> ((Number)n).longValue()).collect(Collectors.toList());

        TypedQuery<Appointment> query = em.createQuery("from Appointment where id in :idList order by startDate desc, id desc", Appointment.class);
        query.setParameter("idList",idList);

        return query.getResultList();
    }

    @Override
    public long getPreviousUserAppointmentCount(long userid){
        TypedQuery<Long> query = em.createQuery("SELECT count(*) FROM Appointment as a where appointedBy.userId = :userid and confirmed = TRUE and cancelled = FALSE and startDate < :currentDate", Long.class);
        query.setParameter("currentDate", LocalDateTime.now());
        query.setParameter("userid", userid);
        return query.getSingleResult();
    }

    @Override
    public Appointment create(Service service, User user, LocalDateTime startDate, LocalDateTime endDate, String location, String description) {
        Appointment appointment = new Appointment( service,user,startDate,endDate,location,false, description);
        em.persist(appointment);

        return appointment;
    }

    @Override
    public void confirmAppointment(long appointmentid) {
        Appointment appointment = findById(appointmentid).orElseThrow(AppointmentNonExistentException::new);
        appointment.setConfirmed();
    }

    @Override
    public void cancelAppointment(long appointmentid) {
        Appointment appointment = findById(appointmentid).orElseThrow(AppointmentNonExistentException::new);
        appointment.setCancelled(true);
    }
}
