package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Appointment;
import ar.edu.itba.paw.model.exceptions.AppointmentNonExistentException;
import ar.edu.itba.paw.services.AppointmentDao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
        TypedQuery<Appointment> query = em.createQuery("from Appointment where serviceid = :serviceid and startDate > :currentDate ", Appointment.class);
        query.setParameter("serviceid",serviceid);
        query.setParameter("currentDate", LocalDateTime.now());
        return query.getResultList();

    }

    @Override
    public List<Appointment> getAllUpcomingServicesAppointments(Collection<Long> servicesIds, boolean confirmed) {
        TypedQuery<Appointment> query = em.createQuery("from Appointment where serviceid in :serviceids and startDate > :currentDate and confirmed = :confirmed ", Appointment.class);
        query.setParameter("serviceids",List.copyOf(servicesIds));
        query.setParameter("currentDate", LocalDateTime.now());
        query.setParameter("confirmed", confirmed);
        return query.getResultList();
    }

    @Override
    public List<Appointment> getAllUpcomingUserAppointments(long userid, boolean confirmed, int page, int pageSize) {
        TypedQuery<Long> query = em.createQuery("SELECT id FROM Appointment as a where userid = :userid and confirmed = :confirmed and startDate > :currentDate", Long.class);
        query.setFirstResult(page * pageSize); // (page - 1) si arrancan en 1 las pags
        query.setMaxResults(pageSize);
        query.setParameter("currentDate", LocalDateTime.now());
        query.setParameter("userid", userid);
        query.setParameter("confirmed", confirmed);

        final List<Long> idList = query.getResultList();
        TypedQuery<Appointment> queryPage = em.createQuery("from Appointment where id in :idList ", Appointment.class);
        queryPage.setParameter("idList",idList);

        return queryPage.getResultList();
    }

    @Override
    public List<Appointment> getPreviousUserAppointments(long userid, int page, int pageSize) {

        TypedQuery<Long> nativeQuery = em.createQuery("SELECT id FROM Appointment as a where userid = :userid and confirmed = TRUE and startDate < :currentDate ", Long.class);
        nativeQuery.setFirstResult(page * pageSize); // (page - 1) si arrancan en 1 las pags
        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setParameter("currentDate", LocalDateTime.now());
        nativeQuery.setParameter("userid", userid);

        final List<Long> idList = nativeQuery.getResultList();

        TypedQuery<Appointment> query = em.createQuery("from Appointment where id in :idList order by startDate desc ", Appointment.class);
        query.setParameter("idList",idList);

        return query.getResultList();
    }

    @Override
    public long getUserAppointmentCount(long userid, boolean confirmed ){
        TypedQuery<Long> query = em.createQuery("SELECT count(id) FROM Appointment as a where userid = :userid and confirmed = :confirmed and startDate > :currentDate ", Long.class);
        query.setParameter("currentDate", LocalDateTime.now());
        query.setParameter("userid", userid);
        query.setParameter("confirmed", confirmed);
        return query.getResultList().getFirst();
    }
    @Override
    public long getPreviousUserAppointmentCount(long userid){
        TypedQuery<Long> query = em.createQuery("SELECT count(*) FROM Appointment as a where userid = :userid and confirmed = TRUE and startDate < :currentDate ", Long.class);
        query.setParameter("currentDate", LocalDateTime.now());
        query.setParameter("userid", userid);
        return query.getSingleResult();
    }

    @Override
    public Appointment create(long serviceid, long userid, LocalDateTime startDate, LocalDateTime endDate, String location, String description) {
        Appointment appointment = new Appointment( serviceid,userid,startDate,endDate,location,false, description);
        em.persist(appointment);

        return appointment;
    }

    @Override
    public void confirmAppointment(long appointmentid) {
        Appointment appointment = findById(appointmentid).orElseThrow(AppointmentNonExistentException::new);
        appointment.setConfirmed();
        em.merge(appointment);
    }

    @Override
    public void cancelAppointment(long appointmentid) {
        Appointment appointment = findById(appointmentid).orElseThrow(AppointmentNonExistentException::new);
        em.remove(appointment);
    }
}
