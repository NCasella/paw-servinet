package ar.edu.itba.paw.services;


import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.BusinessNotFoundException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDateTime;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ServiceServiceImplTest {
    private static final LocalDateTime STARTDATE = LocalDateTime.now();
    private static final LocalDateTime ENDDATE = STARTDATE.plusHours(2);
    private static final String EMAIL = "andresdominguez555@gmail.com";
    private static final String PASSWORD ="password";
    private static final String SERVICENAME = "Service name";
    private static final String SERVICEDESCRIPTION = "Service description";
    private static final long SERVICEID = 1;
    private static final long USERID = 1;
    private static final long APPOINTMENTID = 1;

    private static final long ID =1;
    private static final String BUSINESS_NAME = "Business";
    private static final long PROVIDERID = 1000;
    private static final String TELEPHONE = "123456789";
    private static final String LOCALE = "en";
    private static final String USERNAME = "username";
    private static final long BUSINESSID = 1;
    private static final String DESCRIPTION = "description";
    private static final Boolean HOMESERVICE = false;
    private static final String SURNAME = "surname";
    private static final String LOCATION = "calle 123";
    private static final Neighbourhoods[] NEIGHBOURHOODS = {Neighbourhoods.PALERMO};
    private static final Categories CATEGORY = Categories.BELLEZA;
    private static final int DURATION = 30;
    private static final String PRICE = "ARS 1000";
    private static final Boolean ADDITIONALCHARGES = false;
    private static final PricingTypes PRICING = PricingTypes.PER_TOTAL;
    private static final User mockUser=Mockito.mock(User.class);
    private static final Business mockBusiness=new Business(BUSINESS_NAME,mockUser,TELEPHONE,EMAIL,LOCATION);
    private static final Service service=new Service(mockBusiness,SERVICENAME,SERVICEDESCRIPTION,HOMESERVICE,LOCATION, CATEGORY,DURATION,PRICING,PRICE,ADDITIONALCHARGES,(long)1);
    @InjectMocks
    private ServiceServiceImpl serviceService;

    @Mock
    private static ServiceDao serviceDao;
    @Mock
    private static UserServiceImpl userService;
    @Mock
    private static AppointmentService appointmentService;
    @Mock
    private static EmailServiceImpl emailService;
    @Mock
    private static BusinessDao businessDao;
    @Mock
    private static ImageService imageService;


    @Test
    public void testCreate() {

        Mockito.when(businessDao.findById(BUSINESSID)).thenReturn(Optional.of(mockBusiness));
       Mockito.when(serviceDao.create(mockBusiness,SERVICENAME,SERVICEDESCRIPTION,HOMESERVICE,LOCATION,NEIGHBOURHOODS,CATEGORY,DURATION,PRICING,PRICE,ADDITIONALCHARGES,(long)-1)).thenReturn(service);
        Mockito.when(imageService.getImageById(-1L)).thenReturn(Optional.of(new ImageModel(-1,new byte[2])));
       Service serv=serviceService.create(BUSINESSID,SERVICENAME,SERVICEDESCRIPTION,HOMESERVICE,NEIGHBOURHOODS,LOCATION,CATEGORY,DURATION,PRICING,PRICE,ADDITIONALCHARGES,-1L);

        Assert.assertNotNull(serv);
        Assert.assertEquals(service,serv);
        Assert.assertEquals(mockBusiness,serv.getBusiness());
        Assert.assertEquals(SERVICENAME,serv.getName());
        Assert.assertEquals(SERVICEDESCRIPTION,serv.getDescription());
        Assert.assertEquals(HOMESERVICE,serv.getHomeService());
        Assert.assertEquals(LOCATION,serv.getLocation());
        Assert.assertEquals(Long.valueOf(1),serv.getImageId());
    }
    @Test
    public void testCreateWithoutImage(){
        final Service noImageService=new Service(mockBusiness,SERVICENAME,SERVICEDESCRIPTION,HOMESERVICE,LOCATION, CATEGORY,DURATION,PRICING,PRICE,ADDITIONALCHARGES,-1L);
        Mockito.when(businessDao.findById(BUSINESSID)).thenReturn(Optional.of(mockBusiness));
        Mockito.when(serviceDao.create(mockBusiness,SERVICENAME,SERVICEDESCRIPTION,HOMESERVICE,LOCATION,NEIGHBOURHOODS,CATEGORY,DURATION,PRICING,PRICE,ADDITIONALCHARGES,-1L)).thenReturn(noImageService);
        Mockito.when(imageService.getImageById(-1L)).thenReturn(Optional.of(new ImageModel(-1,new byte[2])));
        Service serv=serviceService.create(BUSINESSID,SERVICENAME,SERVICEDESCRIPTION,HOMESERVICE,NEIGHBOURHOODS,LOCATION,CATEGORY,DURATION,PRICING,PRICE,ADDITIONALCHARGES,-1L);

        Assert.assertNotNull(serv);
        Assert.assertEquals(noImageService,serv);
        Assert.assertEquals(mockBusiness,serv.getBusiness());
        Assert.assertEquals(SERVICENAME,serv.getName());
        Assert.assertEquals(SERVICEDESCRIPTION,serv.getDescription());
        Assert.assertEquals(HOMESERVICE,serv.getHomeService());
        Assert.assertEquals(LOCATION,serv.getLocation());
        Assert.assertEquals(Long.valueOf(-1),serv.getImageId());
    }
    @Test(expected = BusinessNotFoundException.class)
    public void testCreateBusIdNotFound() {

        Mockito.when(businessDao.findById(BUSINESSID)).thenReturn(Optional.empty());

        Service serv=serviceService.create(BUSINESSID,SERVICENAME,SERVICEDESCRIPTION,HOMESERVICE,NEIGHBOURHOODS,LOCATION,CATEGORY,DURATION,PRICING,PRICE,ADDITIONALCHARGES,-1L);

        Assert.fail();
    }



}
