package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class AppointmentServiceImplTest {

    private static final LocalDateTime START_DATE = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime END_DATE = START_DATE.plusMinutes(30);
    private static final String LOCATION = "calle 123";
    private static final long SERVICE_ID = 1L;
    private static final String LOCALE = "en";
    private static final String DESCRIPTION = "description";
    private static final int DURATION = 30;
    private static final long BUSINESS_ID = 1L;
    private static final long USER_ID = 1L;
    private static final long OWNER_USER_ID = 2L;
    private static final long APPOINTMENT_ID = 1L;
    private static final int PAGE_SIZE = 10;

    private User userOwner;
    private User userAppointment;
    private Business business;
    private Service service;
    private Appointment appointment;
    private Appointment confirmedAppointment;
    private Appointment cancelledAppointment;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;
    @Mock
    private UserService userService;
    @Mock
    private AppointmentDao appointmentDao;
    @Mock
    private ServiceDao serviceDao;
    @Mock
    private EmailService emailService;

    @Before
    public void setup() throws Exception {
        userOwner = createUserWithId(OWNER_USER_ID, "owner", "password", "Owner", "User",
                "owner@test.com", "111111111", true, LOCALE);
        userAppointment = createUserWithId(USER_ID, "client", "password", "Client", "User",
                "client@test.com", "222222222", false, LOCALE);

        business = createBusinessWithId(BUSINESS_ID, "Test Business", userOwner, "333333333",
                "business@test.com", "Business Location");

        service = createServiceWithId(SERVICE_ID, business, "Test Service", "Service Description",
                false, LOCATION, Categories.BELLEZA, DURATION, PricingTypes.PER_TOTAL, "1000", false, null);

        appointment = createAppointmentWithId(APPOINTMENT_ID, service, userAppointment, START_DATE,
                END_DATE, LOCATION, false, DESCRIPTION);

        confirmedAppointment = createAppointmentWithId(APPOINTMENT_ID, service, userAppointment, START_DATE,
                END_DATE, LOCATION, true, DESCRIPTION);

        cancelledAppointment = createAppointmentWithId(APPOINTMENT_ID, service, userAppointment, START_DATE,
                END_DATE, LOCATION, false, DESCRIPTION);
        cancelledAppointment.setCancelled(true);
    }

    private User createUserWithId(long id, String username, String password, String name,
                                   String surname, String email, String telephone,
                                   boolean isProvider, String locale) throws Exception {
        User user = new User(username, password, name, surname, email, telephone, isProvider, locale);
        setFieldValue(user, "userId", id);
        return user;
    }

    private Business createBusinessWithId(long id, String name, User owner, String telephone,
                                           String email, String location) throws Exception {
        Business business = new Business(name, owner, telephone, email, location);
        setFieldValue(business, "businessid", id);
        return business;
    }

    private Service createServiceWithId(long id, Business business, String name, String description,
                                         boolean homeService, String location, Categories category,
                                         int duration, PricingTypes pricingType, String price,
                                         boolean additionalCharges, Long imageId) {
        Service service = new Service(business, name, description, homeService, location, category,
                duration, pricingType, price, additionalCharges, imageId);
        service.setId(id);
        return service;
    }

    private Appointment createAppointmentWithId(long id, Service service, User appointedBy,
                                                 LocalDateTime startDate, LocalDateTime endDate,
                                                 String location, boolean confirmed, String description) throws Exception {
        Appointment appointment = new Appointment(service, appointedBy, startDate, endDate, location, confirmed, description);
        setFieldValue(appointment, "id", id);
        return appointment;
    }

    private void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Class<?> clazz = obj.getClass();
        Field field = null;
        while (clazz != null && field == null) {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        if (field == null) {
            throw new NoSuchFieldException("Field " + fieldName + " not found in class hierarchy");
        }
        field.setAccessible(true);
        field.set(obj, value);
    }

    @Test
    public void testFindById() {
        Mockito.when(appointmentDao.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));

        Optional<Appointment> result = appointmentService.findById(APPOINTMENT_ID);

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(APPOINTMENT_ID, result.get().getId());
    }

    @Test
    public void testFindByIdNotFound() {
        Mockito.when(appointmentDao.findById(APPOINTMENT_ID)).thenReturn(Optional.empty());

        Optional<Appointment> result = appointmentService.findById(APPOINTMENT_ID);

        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void testGetAllUpcomingServiceAppointments() {
        List<Appointment> appointments = List.of(appointment);
        Mockito.when(appointmentDao.getAllUpcomingServiceAppointments(SERVICE_ID)).thenReturn(appointments);

        List<Appointment> result = appointmentService.getAllUpcomingServiceAppointments(SERVICE_ID);

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(APPOINTMENT_ID, result.get(0).getId());
    }

    @Test
    public void testGetAllUpcomingServiceAppointmentsEmpty() {
        Mockito.when(appointmentDao.getAllUpcomingServiceAppointments(SERVICE_ID)).thenReturn(Collections.emptyList());

        List<Appointment> result = appointmentService.getAllUpcomingServiceAppointments(SERVICE_ID);

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAllUpcomingServicesAppointments() {
        List<Appointment> appointments = List.of(appointment);
        Mockito.when(appointmentDao.getAllUpcomingServicesAppointments(List.of(SERVICE_ID), true, 1, PAGE_SIZE))
                .thenReturn(appointments);

        List<Appointment> result = appointmentService.getAllUpcomingServicesAppointments(List.of(SERVICE_ID), true, 1);

        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testGetServicesAppointmentCount() {
        Mockito.when(appointmentDao.getServicesAppointmentCount(List.of(SERVICE_ID), true))
                .thenReturn(5L);

        long count = appointmentService.getServicesAppointmentCount(List.of(SERVICE_ID), true);

        Assert.assertEquals(5L, count);
    }

    @Test
    public void testGetAllUpcomingUserAppointments() {
        List<Appointment> appointments = List.of(appointment);
        Mockito.when(appointmentDao.getAllUpcomingUserAppointments(USER_ID, true, 1, PAGE_SIZE))
                .thenReturn(appointments);

        List<Appointment> result = appointmentService.getAllUpcomingUserAppointments(USER_ID, true, 1);

        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testGetUserAppointmentCount() {
        Mockito.when(appointmentDao.getUserAppointmentCount(USER_ID, true)).thenReturn(3L);

        long count = appointmentService.getUserAppointmentCount(USER_ID, true);

        Assert.assertEquals(3L, count);
    }

    @Test
    public void testGetPreviousUserAppointments() {
        List<Appointment> appointments = List.of(appointment);
        Mockito.when(appointmentDao.getPreviousUserAppointments(USER_ID, 1, PAGE_SIZE)).thenReturn(appointments);

        List<Appointment> result = appointmentService.getPreviousUserAppointments(USER_ID, 1);

        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testGetPreviousUserAppointmentCount() {
        Mockito.when(appointmentDao.getPreviousUserAppointmentCount(USER_ID)).thenReturn(8L);

        long count = appointmentService.getPreviousUserAppointmentCount(USER_ID);

        Assert.assertEquals(8L, count);
    }

    @Test
    public void testGetPageCountExact() {
        Assert.assertEquals(2, appointmentService.getPageCount(20));
    }

    @Test
    public void testGetPageCountRemainder() {
        Assert.assertEquals(3, appointmentService.getPageCount(25));
    }

    @Test
    public void testGetPageCountEmpty() {
        Assert.assertEquals(0, appointmentService.getPageCount(0));
    }

    @Test
    public void testGetPageCountPartialPage() {
        Assert.assertEquals(1, appointmentService.getPageCount(5));
    }

    @Test
    public void testConfirmAppointment() {
        Mockito.when(appointmentDao.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));

        long result = appointmentService.confirmAppointment(APPOINTMENT_ID);

        Assert.assertEquals(SERVICE_ID, result);
        verify(appointmentDao).confirmAppointment(APPOINTMENT_ID);
        verify(emailService).confirmedAppointment(appointment, service, business, userAppointment, LOCALE);
    }

    @Test
    public void testConfirmAppointmentNotFound() {
        Mockito.when(appointmentDao.findById(APPOINTMENT_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(AppointmentNonExistentException.class,
                () -> appointmentService.confirmAppointment(APPOINTMENT_ID));

        verify(appointmentDao, never()).confirmAppointment(Mockito.anyLong());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testConfirmAlreadyConfirmed() {
        Mockito.when(appointmentDao.findById(APPOINTMENT_ID)).thenReturn(Optional.of(confirmedAppointment));

        Assert.assertThrows(AppointmentAlreadyConfirmed.class,
                () -> appointmentService.confirmAppointment(APPOINTMENT_ID));

        verify(appointmentDao, never()).confirmAppointment(Mockito.anyLong());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testDenyAppointment() {
        Mockito.when(appointmentDao.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));

        long result = appointmentService.denyAppointment(APPOINTMENT_ID);

        Assert.assertEquals(SERVICE_ID, result);
        verify(appointmentDao).cancelAppointment(APPOINTMENT_ID);
        verify(emailService).deniedAppointment(appointment, service, business, userAppointment, false, LOCALE);
    }

    @Test
    public void testDenyAppointmentNotFound() {
        Mockito.when(appointmentDao.findById(APPOINTMENT_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(AppointmentNonExistentException.class,
                () -> appointmentService.denyAppointment(APPOINTMENT_ID));

        verify(appointmentDao, never()).cancelAppointment(Mockito.anyLong());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testDenyAlreadyConfirmed() {
        Mockito.when(appointmentDao.findById(APPOINTMENT_ID)).thenReturn(Optional.of(confirmedAppointment));

        Assert.assertThrows(AppointmentAlreadyConfirmed.class,
                () -> appointmentService.denyAppointment(APPOINTMENT_ID));

        verify(appointmentDao, never()).cancelAppointment(Mockito.anyLong());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testCancelAppointment() {
        Mockito.when(appointmentDao.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));

        long result = appointmentService.cancelAppointment(APPOINTMENT_ID);

        Assert.assertEquals(SERVICE_ID, result);
        verify(appointmentDao).cancelAppointment(APPOINTMENT_ID);
        verify(emailService).cancelledAppointment(appointment, service, business, userAppointment, false, LOCALE);
    }

    @Test
    public void testCancelAppointmentNotFound() {
        Mockito.when(appointmentDao.findById(APPOINTMENT_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(AppointmentNonExistentException.class,
                () -> appointmentService.cancelAppointment(APPOINTMENT_ID));

        verify(appointmentDao, never()).cancelAppointment(Mockito.anyLong());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testChangePendingAppointmentStatusConfirmed() {
        Mockito.when(appointmentDao.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));

        appointmentService.changePendingAppointmentStatus(APPOINTMENT_ID, AppointmentStatus.CONFIRMED);

        verify(appointmentDao).confirmAppointment(APPOINTMENT_ID);
        verify(emailService).confirmedAppointment(appointment, service, business, userAppointment, LOCALE);
    }

    @Test
    public void testChangePendingAppointmentStatusDenied() {
        Mockito.when(appointmentDao.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));

        appointmentService.changePendingAppointmentStatus(APPOINTMENT_ID, AppointmentStatus.DENIED);

        verify(appointmentDao).cancelAppointment(APPOINTMENT_ID);
        verify(emailService).deniedAppointment(appointment, service, business, userAppointment, false, LOCALE);
    }

    @Test
    public void testChangePendingAppointmentStatusCancelled() {
        Mockito.when(appointmentDao.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));

        appointmentService.changePendingAppointmentStatus(APPOINTMENT_ID, AppointmentStatus.CANCELLED);

        verify(appointmentDao).cancelAppointment(APPOINTMENT_ID);
    }

    @Test
    public void testChangePendingAppointmentStatusAlreadyCancelled() {
        Mockito.when(appointmentDao.findById(APPOINTMENT_ID)).thenReturn(Optional.of(cancelledAppointment));

        Assert.assertThrows(AppointmentWasCancelled.class,
                () -> appointmentService.changePendingAppointmentStatus(APPOINTMENT_ID, AppointmentStatus.CONFIRMED));

        verify(appointmentDao, never()).confirmAppointment(Mockito.anyLong());
        verify(appointmentDao, never()).cancelAppointment(Mockito.anyLong());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testChangePendingAppointmentStatusAlreadyConfirmed() {
        Mockito.when(appointmentDao.findById(APPOINTMENT_ID)).thenReturn(Optional.of(confirmedAppointment));

        Assert.assertThrows(AppointmentAlreadyConfirmed.class,
                () -> appointmentService.changePendingAppointmentStatus(APPOINTMENT_ID, AppointmentStatus.CONFIRMED));

        verify(appointmentDao, never()).confirmAppointment(Mockito.anyLong());
        verify(appointmentDao, never()).cancelAppointment(Mockito.anyLong());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testCreate() {
        Mockito.when(serviceDao.findById(SERVICE_ID)).thenReturn(Optional.of(service));
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(userAppointment));
        Mockito.when(serviceDao.isServiceOwner(SERVICE_ID, USER_ID)).thenReturn(false);
        Mockito.when(appointmentDao.create(Mockito.eq(service), Mockito.eq(userAppointment),
                Mockito.any(), Mockito.any(), Mockito.eq(LOCATION), Mockito.eq(DESCRIPTION)))
                .thenReturn(appointment);

        Appointment result = appointmentService.create(SERVICE_ID, USER_ID, LOCATION, START_DATE, DESCRIPTION, Optional.empty());

        Assert.assertNotNull(result);
        Assert.assertEquals(APPOINTMENT_ID, result.getId());

        verify(appointmentDao).create(Mockito.eq(service), Mockito.eq(userAppointment),
                Mockito.any(), Mockito.any(), Mockito.eq(LOCATION), Mockito.eq(DESCRIPTION));
        verify(emailService).requestAppointment(appointment, service, business, userAppointment, LOCALE);
    }

    @Test
    public void testCreateServiceNotFound() {
        Mockito.when(serviceDao.findById(SERVICE_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(InvalidOperationException.class,
                () -> appointmentService.create(SERVICE_ID, USER_ID, LOCATION, START_DATE, DESCRIPTION, Optional.empty()));

        verify(appointmentDao, never()).create(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testCreateUserNotFound() {
        Mockito.when(serviceDao.findById(SERVICE_ID)).thenReturn(Optional.of(service));
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(InvalidOperationException.class,
                () -> appointmentService.create(SERVICE_ID, USER_ID, LOCATION, START_DATE, DESCRIPTION, Optional.empty()));

        verify(appointmentDao, never()).create(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testCreateOwnerCantAppointOwnService() {
        Mockito.when(serviceDao.findById(SERVICE_ID)).thenReturn(Optional.of(service));
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(userAppointment));
        Mockito.when(serviceDao.isServiceOwner(SERVICE_ID, USER_ID)).thenReturn(true);

        Assert.assertThrows(InvalidOperationException.class,
                () -> appointmentService.create(SERVICE_ID, USER_ID, LOCATION, START_DATE, DESCRIPTION, Optional.empty()));

        verify(appointmentDao, never()).create(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testGetUserAppointmentsConfirmed() {
        List<Appointment> appointments = List.of(appointment);
        Mockito.when(appointmentDao.getAllUpcomingUserAppointments(USER_ID, true, 1, PAGE_SIZE))
                .thenReturn(appointments);
        Mockito.when(appointmentDao.getUserAppointmentCount(USER_ID, true)).thenReturn(1L);

        PagedList<Appointment> result = appointmentService.getUserAppointments(USER_ID, AppointmentStatus.CONFIRMED, 1);

        Assert.assertEquals(1, result.getList().size());
        Assert.assertEquals(1, result.getTotalElements());
    }

    @Test
    public void testGetUserAppointmentsPending() {
        List<Appointment> appointments = List.of(appointment);
        Mockito.when(appointmentDao.getAllUpcomingUserAppointments(USER_ID, false, 1, PAGE_SIZE))
                .thenReturn(appointments);
        Mockito.when(appointmentDao.getUserAppointmentCount(USER_ID, false)).thenReturn(1L);

        PagedList<Appointment> result = appointmentService.getUserAppointments(USER_ID, AppointmentStatus.PENDING, 1);

        Assert.assertEquals(1, result.getList().size());
    }

    @Test
    public void testGetUserAppointmentsFinished() {
        List<Appointment> appointments = List.of(appointment);
        Mockito.when(appointmentDao.getPreviousUserAppointments(USER_ID, 1, PAGE_SIZE))
                .thenReturn(appointments);
        Mockito.when(appointmentDao.getPreviousUserAppointmentCount(USER_ID)).thenReturn(1L);

        PagedList<Appointment> result = appointmentService.getUserAppointments(USER_ID, AppointmentStatus.FINISHED, 1);

        Assert.assertEquals(1, result.getList().size());
    }

    @Test
    public void testGetUserAppointmentsEmpty() {
        Mockito.when(appointmentDao.getAllUpcomingUserAppointments(USER_ID, true, 1, PAGE_SIZE))
                .thenReturn(Collections.emptyList());
        Mockito.when(appointmentDao.getUserAppointmentCount(USER_ID, true)).thenReturn(0L);

        PagedList<Appointment> result = appointmentService.getUserAppointments(USER_ID, AppointmentStatus.CONFIRMED, 1);

        Assert.assertTrue(result.getList().isEmpty());
        Assert.assertEquals(0, result.getTotalElements());
    }

    @Test
    public void testGetBusinessAppointments() throws Exception {
        BasicService basicService = createBasicServiceWithId(SERVICE_ID, business, "Test Service", LOCATION, null);
        List<BasicService> services = List.of(basicService);
        List<Appointment> appointments = List.of(appointment);

        Mockito.when(serviceDao.getAllBusinessBasicServices(BUSINESS_ID)).thenReturn(services);
        Mockito.when(appointmentDao.getAllUpcomingServicesAppointments(Mockito.anyCollection(), Mockito.eq(true), Mockito.eq(1), Mockito.eq(PAGE_SIZE)))
                .thenReturn(appointments);
        Mockito.when(appointmentDao.getServicesAppointmentCount(Mockito.anyCollection(), Mockito.eq(true)))
                .thenReturn(1L);

        PagedList<Appointment> result = appointmentService.getBusinessAppointments(BUSINESS_ID, AppointmentStatus.CONFIRMED, 1);

        Assert.assertEquals(1, result.getList().size());
        Assert.assertEquals(1, result.getTotalElements());
    }

    @Test
    public void testGetBusinessAppointmentsEmpty() {
        Mockito.when(serviceDao.getAllBusinessBasicServices(BUSINESS_ID)).thenReturn(Collections.emptyList());

        PagedList<Appointment> result = appointmentService.getBusinessAppointments(BUSINESS_ID, AppointmentStatus.CONFIRMED, 1);

        Assert.assertTrue(result.getList().isEmpty());
        Assert.assertEquals(0, result.getTotalElements());
    }

    private BasicService createBasicServiceWithId(long id, Business business, String name,
                                                   String location, Long imageId) throws Exception {
        // Using concrete Service class since BasicService is abstract
        Service service = new Service(business, name, "description", false, location,
                Categories.BELLEZA, 30, PricingTypes.PER_TOTAL, "1000", false, imageId);
        service.setId(id);
        return service;
    }
}
