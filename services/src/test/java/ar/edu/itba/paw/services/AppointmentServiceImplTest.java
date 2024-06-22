package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.AppointmentAlreadyConfirmed;
import ar.edu.itba.paw.model.exceptions.AppointmentNonExistentException;
import ar.edu.itba.paw.model.exceptions.ServiceNotFoundException;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
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
    private static final String APPDESCRIPTION="Appointment description";

    private static final User yserOwner =new User("biz owner","biz owner password","biz owner name","biz owner surname","biz owner email","11578411",true,LOCALE);
    private static final User userAppointment =new User(USERNAME,PASSWORD,NAME,SURNAME,EMAIL,TELEPHONE,true,LOCALE);
    private static final Business business =new Business(BUSINESSNAME, yserOwner,TELEPHONE,EMAIL,LOCATION);
    private static final Service service =new Service(business,NAME,DESCRIPTION,HOMESERVICE,LOCATION, CATEGORY,DURATION,PRICING,PRICE,ADDITIONALCHARGES,(long)0);
    private static final Appointment appointment = new Appointment(service, userAppointment,STARTDATE,ENDDATE,LOCATION,false);
    private static final Appointment confirmedAppointment=new Appointment(service, userAppointment,STARTDATE,ENDDATE,LOCATION,true);
    private static final Service mockService=Mockito.mock(Service.class);

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

        Mockito.when(userService.findByEmail(Mockito.eq(EMAIL))).thenReturn(Optional.of(userAppointment));
        Mockito.when(appointmentDao.create(Mockito.eq(service),Mockito.eq(userAppointment),Mockito.eq(STARTDATE),Mockito.any(),Mockito.eq(LOCATION),Mockito.eq(APPDESCRIPTION))).thenReturn(appointment);
        Mockito.when(serviceDao.findById(Mockito.eq(SERVICEID))).thenReturn(Optional.of(service));
        // 2. Ejecuta la class under test (una sola)
        Appointment appointment = appointmentService.create(SERVICEID,NAME,SURNAME,EMAIL,LOCATION,TELEPHONE,STARTDATE.toString(),APPDESCRIPTION);

        // 3. Postcondiciones - assertions (todas las que sean necesarias)
        Assert.assertNotNull(appointment);
        Assert.assertEquals(userAppointment, appointment.getAppointedBy());
        Assert.assertEquals(ENDDATE, appointment.getEndDate());
    }

    @Test
    public void findByIdNonExistent(){
        Mockito.when(appointmentService.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Optional<Appointment> appointment= appointmentService.findById(APPOINTMENTID);
        Assert.assertTrue(appointment.isEmpty());

    }

    @Test(expected = ServiceNotFoundException.class)
    public void createAppointmentForNonExistentService(){
        Appointment appointment1=appointmentService.create(SERVICEID,NAME,SURNAME,EMAIL,LOCATION,TELEPHONE,STARTDATE.toString(),APPDESCRIPTION);
        Assert.fail();
    }

    @Test(expected = UserNotFoundException.class)
    public void createAppointmentForNonExistentUser(){
        Mockito.when(serviceDao.findById(Mockito.eq(SERVICEID))).thenReturn(Optional.of(service));
        Appointment appointment1=appointmentService.create(SERVICEID,NAME,SURNAME,EMAIL,LOCATION,TELEPHONE,STARTDATE.toString(),APPDESCRIPTION);
        Assert.fail();
    }

    @Test(expected = AppointmentAlreadyConfirmed.class)
    public void testDenyAlreadyCondirmed(){
        Mockito.when(appointmentService.findById(Mockito.eq(APPOINTMENTID))).thenReturn(Optional.of(confirmedAppointment));
        appointmentService.denyAppointment(APPOINTMENTID);
        Assert.fail();
    }

    @Test(expected= AppointmentAlreadyConfirmed.class)
    public void testConfirmAlready(){
        Mockito.when(appointmentService.findById(Mockito.eq(APPOINTMENTID))).thenReturn(Optional.of(confirmedAppointment));
        appointmentService.confirmAppointment(APPOINTMENTID);
        Assert.fail();
    }
}

