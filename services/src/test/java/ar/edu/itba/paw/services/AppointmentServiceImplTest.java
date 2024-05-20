package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.AppointmentAlreadyConfirmed;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class AppointmentServiceImplTest {

    private static final String BUSINESSNAME="busname";
    private static final String USERNAME="username";
    private static final String PASSWORD="password";
    private static final LocalDateTime STARTDATE = LocalDateTime.now();
    private static final LocalDateTime ENDDATE = STARTDATE.plusHours(2);
    private static final String LOCATION = "calle 123";
    private static final long SERVICEID = 1;
    private static final String NAME= "name";
    private static final String SURNAME= "surname";
    private static final String TELEPHONE= "123456789";
    private static final String LOCALE= "en";
    private static final String EMAIL= "email";
    private static final String DESCRIPTION = "description";
    private static final Boolean HOMESERVICE = true;
    private static final int DURATION = 30;
    private static final String PRICE = "ARS 1000";
    private static final Boolean ADDITIONALCHARGES = false;
    private static final PricingTypes PRICING = PricingTypes.PER_TOTAL;
    private static final long BUSINESSID = 1;
    private static final Neighbourhoods[] NEIGHBOURHOODS = {Neighbourhoods.PALERMO};
    private static final Categories CATEGORY = Categories.BELLEZA;
    private static final long USERID = 1;
    private static final long APPOINTMENTID = 1;

    // Los Mocks hacen el constructor automaticamente (no necesito hacer un Before setup)
    @InjectMocks
    private AppointmentServiceImpl appointmentService;
    @Mock
    private BusinessDao businessDao;
    @Mock
    private UserService userService;
    @Mock
    private AppointmentDao appointmentDao;
    @Mock
    private ServiceDao serviceDao;
    @Mock
    private EmailService emailService;

    @Test
    public void testCreate() {
        // 1. Precondiciones

        Mockito.when(userService.findByEmail(Mockito.eq(EMAIL))).thenReturn(Optional.of(new User(USERNAME,PASSWORD,NAME,SURNAME,EMAIL,TELEPHONE,true,LOCALE)));
        Mockito.when(appointmentDao.create(Mockito.eq(SERVICEID),Mockito.eq(USERID),Mockito.eq(STARTDATE),Mockito.any(),Mockito.eq(LOCATION))).thenReturn(createAppointment());
        Mockito.when(businessDao.findById(Mockito.eq(BUSINESSID))).thenReturn(Optional.of(createBusiness()));

        Mockito.when(serviceDao.findById(Mockito.eq(SERVICEID))).thenReturn(Optional.of(createService()));
        // 2. Ejecuta la class under test (una sola)
        Appointment appointment = appointmentService.create(SERVICEID,NAME,SURNAME,EMAIL,LOCATION,TELEPHONE,STARTDATE.toString());

        // 3. Postcondiciones - assertions (todas las que sean necesarias)
        Assert.assertNotNull(appointment);
        Assert.assertEquals(APPOINTMENTID, appointment.getId());
        Assert.assertEquals(ENDDATE, appointment.getEndDate());
    }
    @Test(expected = AppointmentAlreadyConfirmed.class)
    public void testDenyAlreadyCondirmed(){
        Mockito.when(appointmentService.findById(Mockito.eq(APPOINTMENTID))).thenReturn(Optional.of(new Appointment(APPOINTMENTID,SERVICEID,USERID,STARTDATE,ENDDATE,LOCATION,true)));
        Mockito.when(serviceDao.findById(Mockito.eq(SERVICEID))).thenReturn(Optional.of(createService()));
        Mockito.when(userService.findById(Mockito.anyLong())).thenReturn(Optional.of(new User(USERNAME,PASSWORD,NAME,SURNAME,EMAIL,TELEPHONE,true,LOCALE)));

        appointmentService.denyAppointment(APPOINTMENTID);
        Assert.fail();
    }

    @Test(expected= AppointmentAlreadyConfirmed.class)
    public void testCreateExisting(){
        Mockito.when(appointmentService.findById(Mockito.eq(APPOINTMENTID))).thenReturn(Optional.of(new Appointment(APPOINTMENTID,SERVICEID,USERID,STARTDATE,ENDDATE,LOCATION,true)));
        Mockito.when(serviceDao.findById(Mockito.eq(SERVICEID))).thenReturn(Optional.of(createService()));
        Mockito.when(userService.findById(Mockito.anyLong())).thenReturn(Optional.of(new User(USERNAME,PASSWORD,NAME,SURNAME,EMAIL,TELEPHONE,true,LOCALE)));

        appointmentService.confirmAppointment(APPOINTMENTID);
        Assert.fail();
    }
    private static Service createService(){
        return new Service(SERVICEID,BUSINESSID,NAME,DESCRIPTION,HOMESERVICE,LOCATION, Arrays.stream(NEIGHBOURHOODS).map(Enum::name).toArray(String[]::new),CATEGORY,DURATION,PRICING,PRICE,ADDITIONALCHARGES,0);
    }

    private static Business createBusiness(){
        return new Business(BUSINESSID,BUSINESSNAME,USERID,TELEPHONE,EMAIL,LOCATION);
    }
    private static Appointment createAppointment(){
        return new Appointment(APPOINTMENTID,SERVICEID,USERID,STARTDATE,ENDDATE,LOCATION,false);
    }
}

