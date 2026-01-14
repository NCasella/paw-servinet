package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Appointment;
import ar.edu.itba.paw.model.Pair;
import ar.edu.itba.paw.model.Service;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.exceptions.AppointmentNonExistentException;
import ar.edu.itba.paw.persistance.config.TestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Transactional
@Rollback
@Sql("classpath:sql/schema.sql")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class AppointmentDaoJpaTest {

    private static final long APPOINTMENT_ID = 1;
    private static final long ANOTHER_APPOINTMENT_ID = 2;
    private static final long BUSINESS_ID = 1;
    private static final String BUSINESS_NAME = "business name";
    private static final long SERVICE_ID = 1;
    private static final long ANOTHER_SERVICE_ID = 2;
    private static final long USER_ID = 1;
    private static final long ANOTHER_USER_ID = 2;
    private static final String LOCATION = "calle falsa 123";
    private static final String DESCRIPTION = "generic description";

    private final LocalDateTime START_DATE = LocalDateTime.now().plusDays(1);
    private final LocalDateTime END_DATE = START_DATE.plusHours(2);

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private AppointmentDaoJpa appointmentDao;

    private User USER;
    private Service SERVICE;

    @Before
    public void setup() {
        insertUser(USER_ID, "solro", "sol", "rodri", "solrodriguezgiana@gmail.com", true);
        insertUser(ANOTHER_USER_ID, "user2", "name2", "surname2", "user2@gmail.com", false);

        insertBusiness(BUSINESS_ID, USER_ID, BUSINESS_NAME, LOCATION);

        insertService(SERVICE_ID, BUSINESS_ID, "Peluqueria Ramon", "Veni, peinate y divertite!",
                "calle falsa 123", 60, "Por hora", "5000", true);

        insertService(ANOTHER_SERVICE_ID, BUSINESS_ID, "Service2", "Description2", "calle falsa 456",
                30, "Total", "3000", false);

        USER = em.find(User.class, USER_ID);
        SERVICE = em.find(Service.class, SERVICE_ID);
    }

    private void insertUser(long id, String username, String name, String surname, String email, boolean isProvider) {
        String sql = """
            INSERT INTO users (userid, username, name, surname, email, telephone, password, isprovider, isverified, profilepic)
            VALUES (:id, :username, :name, :surname, :email, '113452343', 'password', :isProvider, true, 3)
            """;
        em.createNativeQuery(sql)
            .setParameter("id", id)
            .setParameter("username", username)
            .setParameter("name", name)
            .setParameter("surname", surname)
            .setParameter("email", email)
            .setParameter("isProvider", isProvider)
            .executeUpdate();
    }

    private void insertBusiness(long id, long userId, String name, String location) {
        String sql = """
            INSERT INTO business (businessid, businessname, userid, businessTelephone, businessEmail, businessLocation)
            VALUES (:id, :name, :userId, '113452343', 'email@test.com', :location)
            """;
        em.createNativeQuery(sql)
            .setParameter("id", id)
            .setParameter("name", name)
            .setParameter("userId", userId)
            .setParameter("location", location)
            .executeUpdate();
    }

    private void insertService(long id, long businessId, String name, String description, String location, int duration, String pricingType, String price, boolean additionalCharges) {
        String sql = """
            INSERT INTO services (id, businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges, imageid)
            VALUES (:id, :businessId, :name, :description, false, :location, 'Belleza', :duration, :pricingType, :price, :additionalCharges, null)
            """;
        em.createNativeQuery(sql)
            .setParameter("id", id)
            .setParameter("businessId", businessId)
            .setParameter("name", name)
            .setParameter("description", description)
            .setParameter("location", location)
            .setParameter("duration", duration)
            .setParameter("pricingType", pricingType)
            .setParameter("price", price)
            .setParameter("additionalCharges", additionalCharges)
            .executeUpdate();
    }

    private void insertAppointment(long id, long serviceId, long userId, LocalDateTime startDate, LocalDateTime endDate, boolean confirmed, boolean cancelled) {
        String sql = """
            INSERT INTO appointments (appointmentid, serviceid, userid, startdate, enddate, location, confirmed, cancelled)
            VALUES (:id, :serviceId, :userId, :startDate, :endDate, :location, :confirmed, :cancelled)
            """;
        em.createNativeQuery(sql)
            .setParameter("id", id)
            .setParameter("serviceId", serviceId)
            .setParameter("userId", userId)
            .setParameter("startDate", Timestamp.valueOf(startDate))
            .setParameter("endDate", Timestamp.valueOf(endDate))
            .setParameter("location", LOCATION)
            .setParameter("confirmed", confirmed)
            .setParameter("cancelled", cancelled)
            .executeUpdate();
    }

    @Test
    public void testCreate() {
        Appointment appointment = appointmentDao.create(SERVICE, USER, START_DATE, END_DATE, LOCATION, DESCRIPTION);
        em.flush();
        em.clear();

        Assert.assertNotNull("The appointment object SHOULD be returned by the DAO.", appointment);

        Appointment persisted = em.find(Appointment.class, appointment.getId());
        Assert.assertNotNull( "The appointment SHOULD be retrievable from the DB by its ID.", persisted);
        Assert.assertFalse("The appointment SHOULD NOT be confirmed by default.", persisted.getConfirmed());

        long count = em.createQuery("SELECT count(a) FROM Appointment a", Long.class).getSingleResult();
        Assert.assertEquals("The DB SHOULD have the appointment created with no duplicates.",1L, count);
    }


    @Test
    public void testCreateWithCorrectService() {
        Appointment a1 = appointmentDao.create(SERVICE, USER, START_DATE, END_DATE, LOCATION, DESCRIPTION);
        em.flush();
        em.clear();

        Appointment persistedAppointment = em.createQuery("from Appointment a where a.serviceAppointed.id=:id", Appointment.class)
                .setParameter("id", SERVICE_ID)
                .getSingleResult();


        Assert.assertNotNull("The appointment SHOULD exist in the DB after being created.", persistedAppointment);
        Assert.assertEquals("The appointment SHOULD match the service ID specified when created.",
                SERVICE_ID, persistedAppointment.getServiceAppointed().getId());
    }

    @Test
    public void testFindById() {
        insertAppointment(APPOINTMENT_ID, SERVICE_ID, USER_ID, START_DATE, END_DATE, false, false);
        em.flush();
        em.clear();

        Optional<Appointment> appointment = appointmentDao.findById(APPOINTMENT_ID);

        Assert.assertTrue("An existing appointment SHOULD be retrievable by its ID.", appointment.isPresent());
        Assert.assertEquals("The appointment SHOULD match the serviceId associated with it.",
                SERVICE_ID, appointment.get().getServiceid());
        Assert.assertTrue("The appointment's start date SHOULD be before the end date registered.",
                END_DATE.isAfter(appointment.get().getStartDate()));
    }

    @Test
    public void testFindByIdNotFound() {
        Optional<Appointment> appointment = appointmentDao.findById(999);

        Assert.assertFalse("A non-existent appointment SHOULD NOT be retrievable.", appointment.isPresent());
    }

    @Test
    public void testConfirmAppointment() {
        insertAppointment(APPOINTMENT_ID, SERVICE_ID, USER_ID, START_DATE, END_DATE, false, false);
        insertAppointment(ANOTHER_APPOINTMENT_ID, SERVICE_ID, USER_ID, START_DATE.plusHours(1), END_DATE.plusHours(1), false, false);

        appointmentDao.confirmAppointment(APPOINTMENT_ID);
        em.flush();

        Appointment firstAppointment = em.find(Appointment.class, APPOINTMENT_ID);
        Assert.assertNotNull("The first appointment SHOULD be found after being confirmed.", firstAppointment);
        Assert.assertTrue("The first appointment SHOULD change its status after being confirmed.",
                firstAppointment.getConfirmed());

        Appointment secondAppointment = em.find(Appointment.class, ANOTHER_APPOINTMENT_ID);
        Assert.assertNotNull("The second appointment SHOULD be found after no changes.", secondAppointment);
        Assert.assertFalse( "The second appointment SHOULD NOT change its confirmation status without being confirmed.",
                secondAppointment.getConfirmed());

    }

    @Test
    public void testConfirmNonExistentAppointment() {
        long nonExistentId = 999L;

        Assert.assertThrows(AppointmentNonExistentException.class, () -> {
            appointmentDao.confirmAppointment(nonExistentId);
        });
    }

    @Test
    public void testCancelAppointment() {
        insertAppointment(APPOINTMENT_ID, SERVICE_ID, USER_ID, START_DATE, END_DATE, false, false);
        insertAppointment(ANOTHER_APPOINTMENT_ID, SERVICE_ID, USER_ID, START_DATE.plusHours(1), END_DATE.plusHours(1), false, false);
        em.flush();

        appointmentDao.cancelAppointment(APPOINTMENT_ID);
        em.flush();

        boolean firstAppointmentStatus = em.find(Appointment.class, APPOINTMENT_ID).isCancelled();
        boolean secondAppointmentStatus = em.find(Appointment.class, ANOTHER_APPOINTMENT_ID).isCancelled();

        Assert.assertTrue("The first appointment which was canceled SHOULD update its status.", firstAppointmentStatus);
        Assert.assertFalse("The second appointment SHOULD NOT change its status to CANCELED after no changes.", secondAppointmentStatus);
    }

    @Test
    public void testCancelNonExistentAppointment() {
        long nonExistentId = 999L;

        Assert.assertThrows(AppointmentNonExistentException.class, () -> {
            appointmentDao.cancelAppointment(nonExistentId);
        });
    }

    @Test
    public void testGetAllUpcomingServiceAppointments() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(5);
        insertAppointment(1, SERVICE_ID, USER_ID, futureDate, futureDate.plusHours(2), false, false);
        insertAppointment(2, SERVICE_ID, USER_ID, futureDate.plusDays(1), futureDate.plusDays(1).plusHours(2), false, false);
        insertAppointment(3, SERVICE_ID, USER_ID, futureDate.plusDays(2), futureDate.plusDays(2).plusHours(2), false, true);

        List<Appointment> appointments = appointmentDao.getAllUpcomingServiceAppointments(SERVICE_ID);

        Assert.assertNotNull("The upcoming appointments for a service SHOULD be returned as a list.", appointments);
        Assert.assertEquals("The upcoming appointments list size SHOULD match the amount of them in the DB for that service.",
                2, appointments.size());
    }

    @Test
    public void testGetAllUpcomingServiceAppointmentsEmpty() {
        List<Appointment> appointments = appointmentDao.getAllUpcomingServiceAppointments(SERVICE_ID);

        Assert.assertNotNull("The upcoming appointments for a service SHOULD be returned as a list even if there are none.",
                appointments);
        Assert.assertTrue("The upcoming appointments list SHOULD be empty when there are none in the DB.",
                appointments.isEmpty());
    }

    @Test
    public void testGetAllUpcomingServicesAppointments() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(5);
        insertAppointment(1, SERVICE_ID, USER_ID, futureDate, futureDate.plusHours(2), true, false);
        insertAppointment(2, ANOTHER_SERVICE_ID, USER_ID, futureDate.plusDays(1), futureDate.plusDays(1).plusHours(2), true, false);

        List<Long> services = List.of(SERVICE_ID, ANOTHER_SERVICE_ID);
        List<Appointment> appointments = appointmentDao.getAllUpcomingServicesAppointments(services, true, 1, 10);

        Assert.assertNotNull("The upcoming appointments for various services SHOULD be returned as a list even if there are none.",
                appointments);
        Assert.assertEquals("The upcoming appointments list size SHOULD match the amount of them in the DB for all services listed.",
                2, appointments.size());
    }

    @Test
    public void testGetAllUpcomingServicesAppointmentsFiltered() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(5);
        insertAppointment(1, SERVICE_ID, USER_ID, futureDate, futureDate.plusHours(2), true, false);
        insertAppointment(2, SERVICE_ID, USER_ID, futureDate.plusDays(1), futureDate.plusDays(1).plusHours(2), false, false);

        List<Appointment> confirmedAppointments = appointmentDao.getAllUpcomingServicesAppointments(List.of(SERVICE_ID), true, 1, 10);
        List<Appointment> pendingAppointments = appointmentDao.getAllUpcomingServicesAppointments(List.of(SERVICE_ID), false, 1, 10);

        Assert.assertEquals("The upcoming confirmed appointments list size SHOULD match the amount of them in the DB for all services listed.",
                1, confirmedAppointments.size());
        Assert.assertEquals("The upcoming pending appointments list size SHOULD match the amount of them in the DB for all services listed.",
                1, pendingAppointments.size());
    }

    @Test
    public void testGetServicesAppointmentCount() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(5);
        insertAppointment(1, SERVICE_ID, USER_ID, futureDate, futureDate.plusHours(2), true, false);
        insertAppointment(2, SERVICE_ID, ANOTHER_USER_ID, futureDate.plusDays(1), futureDate.plusDays(1).plusHours(2), true, false);
        insertAppointment(3, SERVICE_ID, USER_ID, futureDate.plusDays(2), futureDate.plusDays(2).plusHours(2), false, false);

        long confirmedCount = appointmentDao.getServicesAppointmentCount(List.of(SERVICE_ID), true);
        long pendingCount = appointmentDao.getServicesAppointmentCount(List.of(SERVICE_ID), false);

        Assert.assertEquals("The upcoming appointments count SHOULD match the amount of them that are confirmed in the DB for all services listed.",
                2, confirmedCount);
        Assert.assertEquals("The upcoming appointments count SHOULD match the amount of them that are pending for confirmation in the DB for all services listed.",
                1, pendingCount);
    }

    @Test
    public void testGetAllUpcomingUserAppointments() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(5);
        insertAppointment(1, SERVICE_ID, USER_ID, futureDate, futureDate.plusHours(2), true, false);
        insertAppointment(2, SERVICE_ID, USER_ID, futureDate.plusDays(1), futureDate.plusDays(1).plusHours(2), true, false);
        insertAppointment(3, SERVICE_ID, ANOTHER_USER_ID, futureDate.plusDays(2), futureDate.plusDays(2).plusHours(2), false, false);

        List<Appointment> confirmedAppointments = appointmentDao.getAllUpcomingUserAppointments(USER_ID, true, 1, 10);
        List<Appointment> pendingAppointments = appointmentDao.getAllUpcomingUserAppointments(ANOTHER_USER_ID, false, 1, 10);

        Assert.assertNotNull("The upcoming confirmed appointments for an user SHOULD be returned as a list.",
                confirmedAppointments);
        Assert.assertNotNull("The upcoming pending appointments for an user SHOULD be returned as a list.",
                pendingAppointments);
        Assert.assertEquals("The upcoming confirmed appointments list size SHOULD match the amount of them in the DB for an user." ,
                2, confirmedAppointments.size());
        Assert.assertEquals("The upcoming pending appointments list size SHOULD match the amount of them in the DB for an user." ,
                1, pendingAppointments.size());
    }

    //TODO: test pagination

    @Test
    public void testGetAllUpcomingUserAppointmentsEmpty() {
        List<Appointment> appointments = appointmentDao.getAllUpcomingUserAppointments(USER_ID, true, 1, 10);

        Assert.assertNotNull("The upcoming confirmed appointments for an user SHOULD be returned as a list even if there are none.",
                appointments);
        Assert.assertTrue("The upcoming appointments list SHOULD be empty when there are none in the DB.", appointments.isEmpty());
    }

    @Test
    public void testGetUserAppointmentCount() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(5);
        insertAppointment(1, SERVICE_ID, USER_ID, futureDate, futureDate.plusHours(2), true, false);
        insertAppointment(2, SERVICE_ID, USER_ID, futureDate.plusDays(1), futureDate.plusDays(1).plusHours(2), false, false);

        long confirmedCount = appointmentDao.getUserAppointmentCount(USER_ID, true);
        long pendingCount = appointmentDao.getUserAppointmentCount(USER_ID, false);

        Assert.assertEquals("The upcoming confirmed appointments count SHOULD match the amount of them in the DB for an user.",
                1, confirmedCount);
        Assert.assertEquals("The upcoming pending appointments count SHOULD match the amount of them in the DB for an user.",
                1, pendingCount);
    }

    @Test
    public void testGetPreviousUserAppointments() {
        LocalDateTime pastDate = LocalDateTime.now().minusDays(5);
        insertAppointment(1, SERVICE_ID, USER_ID, pastDate, pastDate.plusHours(2), true, false);
        insertAppointment(2, SERVICE_ID, USER_ID, pastDate.minusDays(1), pastDate.minusDays(1).plusHours(2), true, false);
        insertAppointment(3, SERVICE_ID, USER_ID, pastDate.minusDays(2), pastDate.minusDays(2).plusHours(2), false, false);

        List<Appointment> appointments = appointmentDao.getPreviousUserAppointments(USER_ID, 1, 10);

        Assert.assertNotNull("The previous appointments for an user SHOULD be returned as a list.",
                appointments);
        Assert.assertEquals("The previous appointments list size SHOULD match the amount of them in the DB for an user.",
                2, appointments.size());
    }

    @Test
    public void testGetPreviousUserAppointmentsEmpty() {
        List<Appointment> appointments = appointmentDao.getPreviousUserAppointments(USER_ID, 1, 10);

        Assert.assertNotNull("The previous appointments for an user SHOULD be returned as a list even if there are none.",
                appointments);
        Assert.assertTrue("The previous appointments list SHOULD be empty when there are none in the DB.",
                appointments.isEmpty());
    }

    @Test
    public void testGetPreviousUserAppointmentCount() {
        LocalDateTime pastDate = LocalDateTime.now().minusDays(5);
        insertAppointment(1, SERVICE_ID, USER_ID, pastDate, pastDate.plusHours(2), true, false);
        insertAppointment(2, SERVICE_ID, USER_ID, pastDate.minusDays(1), pastDate.minusDays(1).plusHours(2), true, false);

        long count = appointmentDao.getPreviousUserAppointmentCount(USER_ID);

        Assert.assertEquals("The previous appointments count SHOULD match the amount of them in the DB for an user.",
                2, count);
    }

    @Test
    public void testGetServicesFinishedAppointmentCount() {
        LocalDateTime pastDate = LocalDateTime.now().minusDays(5);
        LocalDateTime startRange = pastDate.minusDays(10);
        LocalDateTime endRange = LocalDateTime.now();

        insertAppointment(1, SERVICE_ID, USER_ID, pastDate, pastDate.plusHours(2), true, false);
        insertAppointment(2, SERVICE_ID, ANOTHER_USER_ID, pastDate.minusDays(1), pastDate.minusDays(1).plusHours(2), true, false);
        insertAppointment(3, ANOTHER_SERVICE_ID, USER_ID, pastDate.minusDays(2), pastDate.minusDays(2).plusHours(2), true, false);

        List<Long> services = List.of(SERVICE_ID, ANOTHER_SERVICE_ID);
        List<Pair<Long, Long>> result = appointmentDao.getServicesFinishedAppointmentCount(services, startRange, endRange);
        Pair<Long, Long> firstResult = result.getFirst();
        Pair<Long, Long> secondResult = result.getLast();

        Assert.assertNotNull("The finished appointments for various services SHOULD be returned as a list.",
                result);
        Assert.assertEquals("The finished appointments list size SHOULD match the amount of the requested services. ",
                2, result.size());
    }

    @Test
    public void testGetServicesFinishedAppointmentCountEmpty() {
        LocalDateTime endRange = LocalDateTime.now();
        LocalDateTime startRange = endRange.minusDays(3);
        insertAppointment(2, SERVICE_ID, ANOTHER_USER_ID, startRange.minusDays(1), startRange.minusDays(1).plusHours(2), true, false);

        List<Pair<Long, Long>> result= appointmentDao.getServicesFinishedAppointmentCount(List.of(SERVICE_ID), startRange, endRange);

        Assert.assertTrue("The finished appointments list SHOULD be empty when there are none in the DB for that period.",
                result.isEmpty());
    }

    @Test
    public void testGetServicesRequestedAppointmentCount() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(5);
        LocalDateTime startFrom = LocalDateTime.now();

        insertAppointment(1, SERVICE_ID, USER_ID, futureDate, futureDate.plusHours(2), false, false);
        insertAppointment(2, SERVICE_ID, ANOTHER_USER_ID, futureDate.plusDays(1), futureDate.plusDays(1).plusHours(2), false, false);
        insertAppointment(3, SERVICE_ID, USER_ID, futureDate.plusDays(2), futureDate.plusDays(2).plusHours(2), true, false);

        Long count = appointmentDao.getServicesRequestedAppointmentCount(List.of(SERVICE_ID), startFrom);

        Assert.assertEquals("The requested appointments count SHOULD match the amount of the requested services.", Long.valueOf(2), count);
    }

    @Test
    public void testGetServicesRequestedAppointmentCountEmpty() {
        Long count = appointmentDao.getServicesRequestedAppointmentCount(List.of(SERVICE_ID), LocalDateTime.now());

        Assert.assertEquals("The requested appointments count SHOULD be zero when no appointments are requested.", Long.valueOf(0), count);
    }
}
