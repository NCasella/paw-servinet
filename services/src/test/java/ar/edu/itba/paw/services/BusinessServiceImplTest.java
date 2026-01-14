package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Business;
import ar.edu.itba.paw.model.Categories;
import ar.edu.itba.paw.model.PagedList;
import ar.edu.itba.paw.model.PricingTypes;
import ar.edu.itba.paw.model.Service;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.exceptions.BusinessNotFoundException;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class BusinessServiceImplTest {

    private static final long BUSINESS_ID = 1L;
    private static final String BUSINESS_NAME = "Test Business";
    private static final long USER_ID = 1L;
    private static final String USER_LOCALE = "en";
    private static final String TELEPHONE = "123456789";
    private static final String EMAIL = "business@test.com";
    private static final String LOCATION = "Test Location";
    private static final int PAGE_SIZE = 10;

    private User user;
    private Business business;

    @InjectMocks
    private BusinessServiceImpl businessService;

    @Mock
    private BusinessDao businessDao;
    @Mock
    private ServiceService serviceService;
    @Mock
    private EmailService emailService;
    @Mock
    private UserService userService;

    @Before
    public void setup() throws Exception {
        user = createUserWithId(USER_ID, "testuser", "password", "Test", "User",
                "test@test.com", "123456789", true, USER_LOCALE);
        business = createBusinessWithId(BUSINESS_ID, BUSINESS_NAME, user, TELEPHONE, EMAIL, LOCATION);
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

    private Service createServiceWithId(long id, Business business, String name) throws Exception {
        Service service = new Service(business, name, "Description", false, "Location",
                Categories.BELLEZA, 60, PricingTypes.PER_TOTAL, "1000", false, null);
        service.setId(id);
        return service;
    }

    private void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    @Test
    public void testCreateBusiness() {
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(businessDao.createBusiness(BUSINESS_NAME, USER_ID, TELEPHONE, EMAIL, LOCATION))
                .thenReturn(business);

        Business result = businessService.createBusiness(BUSINESS_NAME, USER_ID, TELEPHONE, EMAIL, LOCATION);

        Assert.assertNotNull(result);
        Assert.assertEquals(BUSINESS_NAME, result.getBusinessName());
        Assert.assertEquals(EMAIL, result.getEmail());
        Assert.assertEquals(LOCATION, result.getLocation());
        Assert.assertEquals(TELEPHONE, result.getTelephone());

        verify(businessDao).createBusiness(BUSINESS_NAME, USER_ID, TELEPHONE, EMAIL, LOCATION);
        verify(userService).makeProvider(user);
        verify(emailService).createdBusiness(business, USER_LOCALE);
    }

    @Test
    public void testCreateBusinessWithInvalidUser() {
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(UserNotFoundException.class,
                () -> businessService.createBusiness(BUSINESS_NAME, USER_ID, TELEPHONE, EMAIL, LOCATION));

        verify(businessDao, never()).createBusiness(Mockito.anyString(), Mockito.anyLong(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        verify(userService, never()).makeProvider(Mockito.any());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testFindById() {
        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.of(business));

        Optional<Business> result = businessService.findById(BUSINESS_ID);

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(BUSINESS_ID, result.get().getBusinessid());
        Assert.assertEquals(BUSINESS_NAME, result.get().getBusinessName());
    }

    @Test
    public void testFindByIdNotFound() {
        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.empty());

        Optional<Business> result = businessService.findById(BUSINESS_ID);

        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void testFindByBusinessName() {
        Mockito.when(businessDao.findByBusinessName(BUSINESS_NAME)).thenReturn(Optional.of(business));

        Optional<Business> result = businessService.findByBusinessName(BUSINESS_NAME);

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(BUSINESS_NAME, result.get().getBusinessName());
    }

    @Test
    public void testFindByBusinessNameNotFound() {
        Mockito.when(businessDao.findByBusinessName(BUSINESS_NAME)).thenReturn(Optional.empty());

        Optional<Business> result = businessService.findByBusinessName(BUSINESS_NAME);

        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void testGetAllBusinessesExactPages() {
        List<Business> businesses = List.of(business);
        Mockito.when(businessDao.getAllBusinesses(1, PAGE_SIZE)).thenReturn(businesses);
        Mockito.when(businessDao.getBusinessesCount()).thenReturn(PAGE_SIZE);

        PagedList<Business> result = businessService.getAllBusinesses(1);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getList().size());
        Assert.assertEquals(PAGE_SIZE, result.getTotalElements());
    }

    @Test
    public void testGetAllBusinessesRemainderPage() {
        List<Business> businesses = List.of(business);
        Mockito.when(businessDao.getAllBusinesses(1, PAGE_SIZE)).thenReturn(businesses);
        Mockito.when(businessDao.getBusinessesCount()).thenReturn(PAGE_SIZE + 3);

        PagedList<Business> result = businessService.getAllBusinesses(1);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getList().size());
        Assert.assertEquals(PAGE_SIZE + 3, result.getTotalElements());
    }

    @Test
    public void testGetAllBusinessesEmpty() {
        Mockito.when(businessDao.getAllBusinesses(1, PAGE_SIZE)).thenReturn(Collections.emptyList());
        Mockito.when(businessDao.getBusinessesCount()).thenReturn(0);

        PagedList<Business> result = businessService.getAllBusinesses(1);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.getList().isEmpty());
        Assert.assertEquals(0, result.getTotalElements());
    }

    @Test
    public void testGetBusinessesByUser() {
        List<Business> businesses = List.of(business);
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(businessDao.getBusinessesByUser(USER_ID, 1, PAGE_SIZE)).thenReturn(businesses);
        Mockito.when(businessDao.getBusinessesCountByUser(USER_ID)).thenReturn(1);

        PagedList<Business> result = businessService.getBusinessesByUser(USER_ID, 1);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getList().size());
        Assert.assertEquals(1, result.getTotalElements());
    }

    @Test
    public void testGetBusinessesByUserNotFound() {
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(UserNotFoundException.class,
                () -> businessService.getBusinessesByUser(USER_ID, 1));

        verify(businessDao, never()).getBusinessesByUser(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void testGetBusinessesCount() {
        Mockito.when(businessDao.getBusinessesCount()).thenReturn(5);

        int count = businessService.getBusinessesCount();

        Assert.assertEquals(5, count);
    }

    @Test
    public void testGetBusinessesCountByUser() {
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(businessDao.getBusinessesCountByUser(USER_ID)).thenReturn(3);

        int count = businessService.getBusinessesCountByUser(USER_ID);

        Assert.assertEquals(3, count);
    }

    @Test
    public void testGetBusinessesCountByUserNotFound() {
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(UserNotFoundException.class,
                () -> businessService.getBusinessesCountByUser(USER_ID));

        verify(businessDao, never()).getBusinessesCountByUser(Mockito.anyLong());
    }

    @Test
    public void testGetBusinessEmail() {
        Mockito.when(businessDao.getBusinessEmail(BUSINESS_ID)).thenReturn(Optional.of(EMAIL));

        Optional<String> result = businessService.getBusinessEmail(BUSINESS_ID);

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(EMAIL, result.get());
    }

    @Test
    public void testGetBusinessEmailNotFound() {
        Mockito.when(businessDao.getBusinessEmail(BUSINESS_ID)).thenReturn(Optional.empty());

        Optional<String> result = businessService.getBusinessEmail(BUSINESS_ID);

        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void testChangeBusinessEmail() {
        String newEmail = "newemail@test.com";
        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.of(business));

        businessService.changeBusinessEmail(BUSINESS_ID, newEmail);

        verify(businessDao).changeBusinessEmail(BUSINESS_ID, newEmail);
    }

    @Test
    public void testChangeBusinessEmailNotFound() {
        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(BusinessNotFoundException.class,
                () -> businessService.changeBusinessEmail(BUSINESS_ID, "newemail@test.com"));

        verify(businessDao, never()).changeBusinessEmail(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    public void testChangeBusinessLocation() {
        String newLocation = "New Location";
        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.of(business));

        businessService.changeBusinessLocation(BUSINESS_ID, newLocation);

        verify(businessDao).changeBusinessLocation(BUSINESS_ID, newLocation);
    }

    @Test
    public void testChangeBusinessLocationNotFound() {
        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(BusinessNotFoundException.class,
                () -> businessService.changeBusinessLocation(BUSINESS_ID, "New Location"));

        verify(businessDao, never()).changeBusinessLocation(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    public void testChangeBusinessTelephone() {
        String newTelephone = "999888777";
        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.of(business));

        businessService.changeBusinessTelephone(BUSINESS_ID, newTelephone);

        verify(businessDao).changeBusinessTelephone(BUSINESS_ID, newTelephone);
    }

    @Test
    public void testChangeBusinessTelephoneNotFound() {
        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(BusinessNotFoundException.class,
                () -> businessService.changeBusinessTelephone(BUSINESS_ID, "999888777"));

        verify(businessDao, never()).changeBusinessTelephone(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    public void testEditBusiness() {
        String newEmail = "newemail@test.com";
        String newLocation = "New Location";
        String newTelephone = "999888777";
        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.of(business));

        businessService.editBusiness(BUSINESS_ID, newEmail, newLocation, newTelephone);

        verify(businessDao).changeBusinessEmail(BUSINESS_ID, newEmail);
        verify(businessDao).changeBusinessLocation(BUSINESS_ID, newLocation);
        verify(businessDao).changeBusinessTelephone(BUSINESS_ID, newTelephone);
    }

    @Test
    public void testDeleteBusinessNotFound() {
        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(BusinessNotFoundException.class,
                () -> businessService.deleteBusiness(BUSINESS_ID));

        verify(businessDao, never()).deleteBusiness(Mockito.anyLong());
        verify(serviceService, never()).delete(Mockito.any(), Mockito.any(), Mockito.anyBoolean());
        verify(userService, never()).revokeProviderRole(Mockito.any());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testDeleteBusinessUserStillHasOthers() {
        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.of(business));
        Mockito.when(serviceService.getAllBusinessServices(BUSINESS_ID)).thenReturn(Collections.emptyList());
        Mockito.when(businessDao.getBusinessesCountByUser(USER_ID)).thenReturn(1);
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(user));

        businessService.deleteBusiness(BUSINESS_ID);

        verify(businessDao).deleteBusiness(BUSINESS_ID);
        verify(emailService).deletedBusiness(business, USER_LOCALE);
        verify(userService, never()).revokeProviderRole(Mockito.any());
    }

    @Test
    public void testDeleteBusinessLastOne() {
        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.of(business));
        Mockito.when(serviceService.getAllBusinessServices(BUSINESS_ID)).thenReturn(Collections.emptyList());
        Mockito.when(businessDao.getBusinessesCountByUser(USER_ID)).thenReturn(0);
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(user));

        businessService.deleteBusiness(BUSINESS_ID);

        verify(businessDao).deleteBusiness(BUSINESS_ID);
        verify(userService).revokeProviderRole(user);
        verify(emailService).deletedBusiness(business, USER_LOCALE);
    }

    @Test
    public void testDeleteBusinessWithServices() throws Exception {
        Service service1 = createServiceWithId(1L, business, "Service 1");
        Service service2 = createServiceWithId(2L, business, "Service 2");
        Service service3 = createServiceWithId(3L, business, "Service 3");
        List<Service> services = List.of(service1, service2, service3);

        Mockito.when(businessDao.findById(BUSINESS_ID)).thenReturn(Optional.of(business));
        Mockito.when(serviceService.getAllBusinessServices(BUSINESS_ID)).thenReturn(services);
        Mockito.when(businessDao.getBusinessesCountByUser(USER_ID)).thenReturn(1);
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(user));

        businessService.deleteBusiness(BUSINESS_ID);

        verify(serviceService, times(services.size())).delete(Mockito.any(Service.class), Mockito.eq(business), Mockito.eq(false));
        verify(serviceService).delete(service1, business, false);
        verify(serviceService).delete(service2, business, false);
        verify(serviceService).delete(service3, business, false);
        verify(businessDao).deleteBusiness(BUSINESS_ID);
    }
}
