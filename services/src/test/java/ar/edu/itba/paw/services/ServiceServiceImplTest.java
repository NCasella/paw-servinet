package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.BusinessNotFoundException;
import ar.edu.itba.paw.model.exceptions.ServiceNotFoundException;
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
import java.util.*;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class ServiceServiceImplTest {

    private static final String SERVICE_NAME = "Service name";
    private static final String SERVICE_DESCRIPTION = "Service description";
    private static final long SERVICE_ID = 1L;
    private static final long USER_ID = 1L;
    private static final long OWNER_USER_ID = 2L;
    private static final String LOCALE = "en";
    private static final long BUSINESS_ID = 1L;
    private static final Boolean HOME_SERVICE = false;
    private static final String LOCATION = "calle 123";
    private static final Neighbourhoods[] NEIGHBOURHOODS = {Neighbourhoods.PALERMO};
    private static final Categories CATEGORY = Categories.BELLEZA;
    private static final int DURATION = 30;
    private static final String PRICE = "ARS 1000";
    private static final Boolean ADDITIONAL_CHARGES = false;
    private static final PricingTypes PRICING = PricingTypes.PER_TOTAL;
    private static final int PAGE_SIZE = 10;

    private User businessOwner;
    private User clientUser;
    private Business business;
    private Service service;

    @InjectMocks
    private ServiceServiceImpl serviceService;

    @Mock
    private ServiceDao serviceDao;
    @Mock
    private UserService userService;
    @Mock
    private AppointmentService appointmentService;
    @Mock
    private EmailService emailService;
    @Mock
    private BusinessDao businessDao;
    @Mock
    private ImageService imageService;

    @Before
    public void setup() throws Exception {
        businessOwner = createUserWithId(OWNER_USER_ID, "owner", "password", "Owner", "User",
                "owner@test.com", "111111111", true, LOCALE);
        clientUser = createUserWithId(USER_ID, "client", "password", "Client", "User",
                "client@test.com", "222222222", false, LOCALE);

        business = createBusinessWithId(BUSINESS_ID, "Test Business", businessOwner, "333333333",
                "business@test.com", "Business Location");

        service = createServiceWithId(SERVICE_ID, business, SERVICE_NAME, SERVICE_DESCRIPTION,
                HOME_SERVICE, LOCATION, CATEGORY, DURATION, PRICING, PRICE, ADDITIONAL_CHARGES, null);
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
    public void testCreate() {
        ImageModel imageModel = new ImageModel(-1L, new byte[2]);
        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.of(business));
        Mockito.when(serviceDao.create(business, SERVICE_NAME, SERVICE_DESCRIPTION, HOME_SERVICE, LOCATION, NEIGHBOURHOODS, CATEGORY, DURATION, PRICING, PRICE, ADDITIONAL_CHARGES, -1L))
                .thenReturn(service);
        Mockito.when(imageService.getImageById(-1L)).thenReturn(Optional.of(imageModel));

        Service result = serviceService.create(BUSINESS_ID, SERVICE_NAME, SERVICE_DESCRIPTION, HOME_SERVICE, NEIGHBOURHOODS, LOCATION, CATEGORY, DURATION, PRICING, PRICE, ADDITIONAL_CHARGES, -1L);

        Assert.assertNotNull(result);
        Assert.assertEquals(SERVICE_ID, result.getId());
        Assert.assertEquals(SERVICE_NAME, result.getName());
        verify(emailService).createdService(result, business, LOCALE);
    }

    @Test
    public void testCreateWithoutImage() {
        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.of(business));
        Mockito.when(serviceDao.create(business, SERVICE_NAME, SERVICE_DESCRIPTION, HOME_SERVICE, LOCATION, NEIGHBOURHOODS, CATEGORY, DURATION, PRICING, PRICE, ADDITIONAL_CHARGES, null))
                .thenReturn(service);
        Mockito.when(imageService.getImageById(-1L)).thenReturn(Optional.empty());

        Service result = serviceService.create(BUSINESS_ID, SERVICE_NAME, SERVICE_DESCRIPTION, HOME_SERVICE, NEIGHBOURHOODS, LOCATION, CATEGORY, DURATION, PRICING, PRICE, ADDITIONAL_CHARGES, -1L);

        Assert.assertNotNull(result);
    }

    @Test
    public void testCreateBusinessNotFound() {
        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(BusinessNotFoundException.class,
                () -> serviceService.create(BUSINESS_ID, SERVICE_NAME, SERVICE_DESCRIPTION, HOME_SERVICE, NEIGHBOURHOODS, LOCATION, CATEGORY, DURATION, PRICING, PRICE, ADDITIONAL_CHARGES, -1L));

        verify(serviceDao, never()).create(Mockito.any(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyBoolean(), Mockito.anyString(), Mockito.any(), Mockito.any(),
                Mockito.anyInt(), Mockito.any(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testFindById() {
        Mockito.when(serviceDao.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        Optional<Service> result = serviceService.findById(SERVICE_ID);

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(SERVICE_ID, result.get().getId());
        Assert.assertEquals(SERVICE_NAME, result.get().getName());
    }

    @Test
    public void testFindByIdNotFound() {
        Mockito.when(serviceDao.findById(SERVICE_ID)).thenReturn(Optional.empty());

        Optional<Service> result = serviceService.findById(SERVICE_ID);

        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void testFindBasicServiceById() {
        Mockito.when(serviceDao.findBasicServiceById(SERVICE_ID)).thenReturn(Optional.of(service));

        Optional<BasicService> result = serviceService.findBasicServiceById(SERVICE_ID);

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(SERVICE_ID, result.get().getId());
    }

    @Test
    public void testFindBasicServiceByIdNotFound() {
        Mockito.when(serviceDao.findBasicServiceById(SERVICE_ID)).thenReturn(Optional.empty());

        Optional<BasicService> result = serviceService.findBasicServiceById(SERVICE_ID);

        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void testEditServiceName() {
        Mockito.when(serviceDao.findById(SERVICE_ID)).thenReturn(Optional.of(service));
        Mockito.when(serviceDao.editServiceName(SERVICE_ID, "New Name")).thenReturn(service);

        Service result = serviceService.editServiceName(SERVICE_ID, "New Name");

        Assert.assertNotNull(result);
        verify(serviceDao).editServiceName(SERVICE_ID, "New Name");
    }

    @Test
    public void testEditServiceNameNotFound() {
        Mockito.when(serviceDao.findById(SERVICE_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(ServiceNotFoundException.class,
                () -> serviceService.editServiceName(SERVICE_ID, "New Name"));

        verify(serviceDao, never()).editServiceName(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    public void testEditService() {
        Mockito.when(serviceDao.findById(SERVICE_ID)).thenReturn(Optional.of(service));

        serviceService.editService(SERVICE_ID, "New description", 60, PricingTypes.PER_HOUR, "2000", true);

        verify(serviceDao).editService(SERVICE_ID, "New description", 60, PricingTypes.PER_HOUR, "2000", true);
    }

    @Test
    public void testEditServiceNotFound() {
        Mockito.when(serviceDao.findById(SERVICE_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(ServiceNotFoundException.class,
                () -> serviceService.editService(SERVICE_ID, "New description", 60, PricingTypes.PER_HOUR, "2000", true));

        verify(serviceDao, never()).editService(Mockito.anyLong(), Mockito.anyString(),
                Mockito.anyInt(), Mockito.any(), Mockito.anyString(), Mockito.anyBoolean());
    }

    @Test
    public void testGetServicesContactInfo() {
        Collection<Long> serviceIds = List.of(1L, 2L);
        ServiceContactInfo info1 = new ServiceContactInfo(1L, "Service 1", "email1@test.com", "111111111");
        ServiceContactInfo info2 = new ServiceContactInfo(2L, "Service 2", "email2@test.com", "222222222");
        Mockito.when(serviceDao.getServicesContactInfo(serviceIds)).thenReturn(List.of(info1, info2));

        Map<Long, ServiceContactInfo> result = serviceService.getServicesContactInfo(serviceIds);

        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.containsKey(1L));
        Assert.assertTrue(result.containsKey(2L));
        Assert.assertEquals("Service 1", result.get(1L).getServiceName());
        Assert.assertEquals("Service 2", result.get(2L).getServiceName());
    }

    @Test
    public void testGetServicesContactInfoEmpty() {
        Collection<Long> serviceIds = Collections.emptyList();
        Mockito.when(serviceDao.getServicesContactInfo(serviceIds)).thenReturn(Collections.emptyList());

        Map<Long, ServiceContactInfo> result = serviceService.getServicesContactInfo(serviceIds);

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAllServices() {
        List<Service> services = List.of(service);
        Mockito.when(serviceDao.getAllServices()).thenReturn(services);

        List<Service> result = serviceService.getAllServices();

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(SERVICE_ID, result.get(0).getId());
    }

    @Test
    public void testGetAllServicesEmpty() {
        Mockito.when(serviceDao.getAllServices()).thenReturn(Collections.emptyList());

        List<Service> result = serviceService.getAllServices();

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testDelete() {
        Mockito.when(serviceDao.findById(SERVICE_ID)).thenReturn(Optional.of(service));
        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.of(business));
        Mockito.when(appointmentService.getAllUpcomingServiceAppointments(SERVICE_ID))
                .thenReturn(Collections.emptyList());

        serviceService.delete(SERVICE_ID);

        verify(serviceDao).delete(SERVICE_ID);
        verify(emailService).deletedService(service, business, LOCALE);
    }

    @Test
    public void testDeleteServiceNotFound() {
        Mockito.when(serviceDao.findById(SERVICE_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(ServiceNotFoundException.class,
                () -> serviceService.delete(SERVICE_ID));

        verify(serviceDao, never()).delete(Mockito.anyLong());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testDeleteBusinessNotFound() {
        Mockito.when(serviceDao.findById(SERVICE_ID)).thenReturn(Optional.of(service));
        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(BusinessNotFoundException.class,
                () -> serviceService.delete(SERVICE_ID));

        verify(serviceDao, never()).delete(Mockito.anyLong());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testDeleteWithAppointments() throws Exception {
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = startDate.plusMinutes(30);

        Appointment confirmedAppointment = createAppointmentWithId(1L, service, clientUser,
                startDate, endDate, LOCATION, true, "Confirmed appointment");
        Appointment pendingAppointment = createAppointmentWithId(2L, service, clientUser,
                startDate, endDate, LOCATION, false, "Pending appointment");
        List<Appointment> appointments = List.of(confirmedAppointment, pendingAppointment);

        Mockito.when(serviceDao.findById(SERVICE_ID)).thenReturn(Optional.of(service));
        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.of(business));
        Mockito.when(appointmentService.getAllUpcomingServiceAppointments(SERVICE_ID))
                .thenReturn(appointments);
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(clientUser));

        serviceService.delete(SERVICE_ID);

        verify(userService, times(appointments.size())).findById(USER_ID);
        verify(emailService).cancelledAppointment(confirmedAppointment, service, business, clientUser, true, LOCALE);
        verify(emailService).deniedAppointment(pendingAppointment, service, business, clientUser, true, LOCALE);
        verify(serviceDao).delete(SERVICE_ID);
    }

    @Test
    public void testGetServices() {
        List<Service> services = List.of(service);
        Mockito.when(serviceDao.getServicesFilteredBy(1, CATEGORY, NEIGHBOURHOODS, 4, "query", null, false, null))
                .thenReturn(services);
        Mockito.when(serviceDao.getServiceCount(CATEGORY, NEIGHBOURHOODS, 4, "query", false, null))
                .thenReturn(1);

        PagedList<Service> result = serviceService.getServices(1, CATEGORY, NEIGHBOURHOODS, 4, "query", null, false, null);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getList().size());
        Assert.assertEquals(1, result.getTotalElements());
    }

    @Test
    public void testGetServicesEmpty() {
        Mockito.when(serviceDao.getServicesFilteredBy(1, null, null, null, null, null, null, null))
                .thenReturn(Collections.emptyList());
        Mockito.when(serviceDao.getServiceCount(null, null, null, null, null, null))
                .thenReturn(0);

        PagedList<Service> result = serviceService.getServices(1, null, null, null, null, null, null, null);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.getList().isEmpty());
        Assert.assertEquals(0, result.getTotalElements());
    }

    @Test
    public void testGetServicesWithBusinessId() {
        List<Service> services = List.of(service);
        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.of(business));
        Mockito.when(serviceDao.getServicesFilteredBy(1, null, null, null, null, null, null, BUSINESS_ID))
                .thenReturn(services);
        Mockito.when(serviceDao.getServiceCount(null, null, null, null, null, BUSINESS_ID))
                .thenReturn(1);

        PagedList<Service> result = serviceService.getServices(1, null, null, null, null, null, null, BUSINESS_ID);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getList().size());
    }

    @Test
    public void testGetServicesWithBusinessIdNotFound() {
        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(BusinessNotFoundException.class,
                () -> serviceService.getServices(1, null, null, null, null, null, null, BUSINESS_ID));

        verify(serviceDao, never()).getServicesFilteredBy(Mockito.anyInt(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void testGetServiceCount() {
        Mockito.when(serviceDao.getServiceCount(CATEGORY, NEIGHBOURHOODS, 4, "query", false, null))
                .thenReturn(15);

        int count = serviceService.getServiceCount(CATEGORY, NEIGHBOURHOODS, 4, "query", false, null);

        Assert.assertEquals(15, count);
    }

    @Test
    public void testGetPageCountRemainder() {
        Mockito.when(serviceDao.getServiceCount(CATEGORY, NEIGHBOURHOODS, 4, "query", false, null))
                .thenReturn(25);

        int pageCount = serviceService.getPageCount(CATEGORY, NEIGHBOURHOODS, 4, "query", false, null);

        Assert.assertEquals(3, pageCount);
    }

    @Test
    public void testGetPageCountExact() {
        Mockito.when(serviceDao.getServiceCount(null, null, null, null, null, null))
                .thenReturn(20);

        int pageCount = serviceService.getPageCount(null, null, null, null, null, null);

        Assert.assertEquals(2, pageCount);
    }

    @Test
    public void testGetPageCountZero() {
        Mockito.when(serviceDao.getServiceCount(null, null, null, null, null, null))
                .thenReturn(0);

        int pageCount = serviceService.getPageCount(null, null, null, null, null, null);

        Assert.assertEquals(0, pageCount);
    }

    @Test
    public void testGetAllBusinessBasicServices() {
        List<BasicService> services = List.of(service);
        Mockito.when(serviceDao.getAllBusinessBasicServices(BUSINESS_ID)).thenReturn(services);

        List<BasicService> result = serviceService.getAllBusinessBasicServices(BUSINESS_ID);

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(SERVICE_ID, result.get(0).getId());
    }

    @Test
    public void testGetAllUserBasicServices() throws Exception {
        Business business1 = createBusinessWithId(1L, "Business 1", businessOwner, "111", "email1@test.com", "Location 1");
        Business business2 = createBusinessWithId(2L, "Business 2", businessOwner, "222", "email2@test.com", "Location 2");

        setFieldValue(businessOwner, "businessOwned", List.of(business1, business2));

        Service service1 = createServiceWithId(1L, business1, "Service 1", "Desc 1", false, LOCATION,
                CATEGORY, DURATION, PRICING, PRICE, false, null);
        Service service2 = createServiceWithId(2L, business2, "Service 2", "Desc 2", false, LOCATION,
                CATEGORY, DURATION, PRICING, PRICE, false, null);

        Mockito.when(serviceDao.getAllBusinessBasicServices(1L)).thenReturn(List.of(service1));
        Mockito.when(serviceDao.getAllBusinessBasicServices(2L)).thenReturn(List.of(service2));

        List<BasicService> result = serviceService.getAllUserBasicServices(businessOwner);

        Assert.assertEquals(2, result.size());
        verify(serviceDao).getAllBusinessBasicServices(1L);
        verify(serviceDao).getAllBusinessBasicServices(2L);
    }

    @Test
    public void testGetAllBusinessServices() {
        List<Service> services = List.of(service);
        Mockito.when(serviceDao.getAllBusinessServices(BUSINESS_ID)).thenReturn(services);

        List<Service> result = serviceService.getAllBusinessServices(BUSINESS_ID);

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(SERVICE_ID, result.get(0).getId());
    }

    @Test
    public void testGetRecommendedServices() {
        List<Service> services = List.of(service);
        Mockito.when(serviceDao.getRecommendedServices()).thenReturn(services);

        List<Service> result = serviceService.getRecommendedServices();

        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testGetRecommendedServicesEmpty() {
        Mockito.when(serviceDao.getRecommendedServices()).thenReturn(Collections.emptyList());

        List<Service> result = serviceService.getRecommendedServices();

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAvailableNeighbourhoodsNoCategory() {
        List<String> neighbourhoods = List.of("Palermo", "Belgrano");
        Mockito.when(serviceDao.getAvailableNeighbourhoods()).thenReturn(neighbourhoods);

        List<String> result = serviceService.getAvailableNeighbourhoods(null);

        Assert.assertEquals(2, result.size());
        verify(serviceDao).getAvailableNeighbourhoods();
        verify(serviceDao, never()).getAvailableNeighbourhoodsByCategory(Mockito.any());
    }

    @Test
    public void testGetAvailableNeighbourhoodsWithCategory() {
        List<String> neighbourhoods = List.of("Palermo");
        Mockito.when(serviceDao.getAvailableNeighbourhoodsByCategory(CATEGORY)).thenReturn(neighbourhoods);

        List<String> result = serviceService.getAvailableNeighbourhoods(CATEGORY);

        Assert.assertEquals(1, result.size());
        verify(serviceDao).getAvailableNeighbourhoodsByCategory(CATEGORY);
        verify(serviceDao, never()).getAvailableNeighbourhoods();
    }
}
