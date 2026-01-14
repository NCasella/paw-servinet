package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.RatingNotFoundException;
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
import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class RatingServiceImplTest {

    private static final String COMMENT = "This is a comment";
    private static final int HIGH_RATING = 5;
    private static final int MID_RATING = 4;
    private static final long USER_ID = 1L;
    private static final long SERVICE_ID = 1L;
    private static final long BUSINESS_ID = 1L;
    private static final long RATING_ID = 1L;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";
    private static final String SURNAME = "surname";
    private static final String TELEPHONE = "123456789";
    private static final String LOCALE = "en";
    private static final String EMAIL = "mail@mail.com";
    private static final int PAGE_SIZE = 10;

    private User user;
    private User businessOwner;
    private Service service;
    private Business business;
    private Rating rating;

    @InjectMocks
    private RatingServiceImpl ratingService;

    @Mock
    private RatingDao ratingDao;

    @Mock
    private ServiceService serviceService;

    @Mock
    private BusinessService businessService;

    @Before
    public void setup() throws Exception {
        businessOwner = createUserWithId(2L, "owner", "password", "Owner", "User",
                "owner@test.com", "111111111", true, LOCALE);
        user = createUserWithId(USER_ID, USERNAME, PASSWORD, NAME, SURNAME, EMAIL, TELEPHONE, false, LOCALE);

        business = createBusinessWithId(BUSINESS_ID, "Test Business", businessOwner, "333333333",
                "business@test.com", "Business Location");

        service = createServiceWithId(SERVICE_ID, business, "Test Service", "Service Description",
                false, "Location", Categories.BELLEZA, 60, PricingTypes.PER_TOTAL, "1000", false, null);

        rating = createRatingWithId(RATING_ID, service, user, HIGH_RATING, COMMENT, LocalDate.now());
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

    private Rating createRatingWithId(long id, Service service, User user, int rating,
                                       String comment, LocalDate date) throws Exception {
        Rating ratingObj = new Rating(service, user, rating, comment, date);
        setFieldValue(ratingObj, "ratingid", id);
        return ratingObj;
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
    public void testHasAlreadyRated() {
        Mockito.when(ratingDao.hasAlreadyRated(USER_ID, SERVICE_ID)).thenReturn(Optional.of(rating));

        Rating result = ratingService.hasAlreadyRated(USER_ID, SERVICE_ID);

        Assert.assertNotNull(result);
        Assert.assertEquals(HIGH_RATING, result.getRating());
        Assert.assertEquals(COMMENT, result.getComment());
        Assert.assertEquals(user, result.getUser());
        Assert.assertEquals(service, result.getService());
    }

    @Test
    public void testHasNotAlreadyRated() {
        Mockito.when(ratingDao.hasAlreadyRated(USER_ID, SERVICE_ID)).thenReturn(Optional.empty());

        Rating result = ratingService.hasAlreadyRated(USER_ID, SERVICE_ID);

        Assert.assertNull(result);
    }

    @Test
    public void testFindById() {
        Mockito.when(ratingDao.findById(RATING_ID)).thenReturn(Optional.of(rating));

        Optional<Rating> result = ratingService.findById(RATING_ID);

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(HIGH_RATING, result.get().getRating());
        Assert.assertEquals(RATING_ID, result.get().getId());
    }

    @Test
    public void testFindByIdNotFound() {
        Mockito.when(ratingDao.findById(RATING_ID)).thenReturn(Optional.empty());

        Optional<Rating> result = ratingService.findById(RATING_ID);

        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void testCreate() {
        Mockito.when(serviceService.findById(SERVICE_ID)).thenReturn(Optional.of(service));
        Mockito.when(ratingDao.create(SERVICE_ID, USER_ID, HIGH_RATING, COMMENT)).thenReturn(rating);

        Rating result = ratingService.create(SERVICE_ID, USER_ID, HIGH_RATING, COMMENT);

        Assert.assertNotNull(result);
        Assert.assertEquals(HIGH_RATING, result.getRating());
        Assert.assertEquals(COMMENT, result.getComment());
        verify(ratingDao).create(SERVICE_ID, USER_ID, HIGH_RATING, COMMENT);
    }

    @Test
    public void testCreateServiceNotFound() {
        Mockito.when(serviceService.findById(SERVICE_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(ServiceNotFoundException.class,
                () -> ratingService.create(SERVICE_ID, USER_ID, HIGH_RATING, COMMENT));

        verify(ratingDao, never()).create(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyString());
    }

    @Test
    public void testEdit() {
        Mockito.when(ratingDao.findById(RATING_ID)).thenReturn(Optional.of(rating));

        ratingService.edit(RATING_ID, MID_RATING, "Updated comment");

        verify(ratingDao).edit(RATING_ID, MID_RATING, "Updated comment");
    }

    @Test
    public void testEditRatingNotFound() {
        Mockito.when(ratingDao.findById(RATING_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(RatingNotFoundException.class,
                () -> ratingService.edit(RATING_ID, MID_RATING, "Updated comment"));

        verify(ratingDao, never()).edit(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyString());
    }

    @Test
    public void testGetAllRatings() {
        List<Rating> ratings = List.of(rating);
        Mockito.when(ratingDao.getAllRatings(1, PAGE_SIZE, null)).thenReturn(ratings);
        Mockito.when(ratingDao.getAllRatingsCount(null)).thenReturn(1);

        PagedList<Rating> result = ratingService.getAllRatings(1, null);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getList().size());
        Assert.assertEquals(1, result.getTotalElements());
    }

    @Test
    public void testGetAllRatingsEmpty() {
        Mockito.when(ratingDao.getAllRatings(1, PAGE_SIZE, null)).thenReturn(Collections.emptyList());
        Mockito.when(ratingDao.getAllRatingsCount(null)).thenReturn(0);

        PagedList<Rating> result = ratingService.getAllRatings(1, null);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.getList().isEmpty());
        Assert.assertEquals(0, result.getTotalElements());
    }

    @Test
    public void testGetRatingsByService() {
        List<Rating> ratings = List.of(rating);
        Mockito.when(serviceService.findById(SERVICE_ID)).thenReturn(Optional.of(service));
        Mockito.when(ratingDao.getRatingsByService(SERVICE_ID, 1, PAGE_SIZE, null))
                .thenReturn(ratings);
        Mockito.when(ratingDao.getRatingsCountByService(SERVICE_ID, null)).thenReturn(1);

        PagedList<Rating> result = ratingService.getRatingsByService(SERVICE_ID, 1, null);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getList().size());
    }

    @Test
    public void testGetRatingsByServiceNotFound() {
        Mockito.when(serviceService.findById(SERVICE_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(ServiceNotFoundException.class,
                () -> ratingService.getRatingsByService(SERVICE_ID, 1, null));

        verify(ratingDao, never()).getRatingsByService(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any());
    }

    @Test
    public void testGetAllRatingsCount() {
        Mockito.when(ratingDao.getAllRatingsCount(null)).thenReturn(5);

        int count = ratingService.getAllRatingsCount(null);

        Assert.assertEquals(5, count);
    }

    @Test
    public void testGetRatingsCountByService() {
        Mockito.when(serviceService.findById(SERVICE_ID)).thenReturn(Optional.of(service));
        Mockito.when(ratingDao.getRatingsCountByService(SERVICE_ID, null)).thenReturn(3);

        int count = ratingService.getRatingsCountByService(SERVICE_ID, null);

        Assert.assertEquals(3, count);
    }

    @Test
    public void testGetRatingsCountByServiceNotFound() {
        Mockito.when(serviceService.findById(SERVICE_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(ServiceNotFoundException.class,
                () -> ratingService.getRatingsCountByService(SERVICE_ID, null));

        verify(ratingDao, never()).getRatingsCountByService(Mockito.anyLong(), Mockito.any());
    }

    @Test
    public void testGetRatingsAvg() throws Exception {
        setFieldValue(service, "ratingAvg", 4.56);

        double avg = ratingService.getRatingsAvg(service);

        Assert.assertEquals(4.6, avg, 0.01);
    }

    @Test
    public void testGetRatingsAvgRoundsCorrectly() throws Exception {
        setFieldValue(service, "ratingAvg", 3.44);

        double avg = ratingService.getRatingsAvg(service);

        Assert.assertEquals(3.4, avg, 0.01);
    }

    @Test
    public void testGetAllBusinessRatings() {
        List<Rating> ratings = List.of(rating);
        Mockito.when(businessService.findById(BUSINESS_ID)).thenReturn(Optional.of(business));
        Mockito.when(ratingDao.getAllBusinessRatings(BUSINESS_ID, 1, PAGE_SIZE)).thenReturn(ratings);

        List<Rating> result = ratingService.getAllBusinessRatings(BUSINESS_ID, 1);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testGetAllBusinessRatingsNotFound() {
        Mockito.when(businessService.findById(BUSINESS_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(ServiceNotFoundException.class,
                () -> ratingService.getAllBusinessRatings(BUSINESS_ID, 1));

        verify(ratingDao, never()).getAllBusinessRatings(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void testGetAllBusinessRatingsFilteredWithNullFilter() {
        List<Rating> ratings = List.of(rating);
        Mockito.when(businessService.findById(BUSINESS_ID)).thenReturn(Optional.of(business));
        Mockito.when(ratingDao.getAllBusinessRatings(BUSINESS_ID, 1, PAGE_SIZE)).thenReturn(ratings);

        List<Rating> result = ratingService.getAllBusinessRatingsFiltered(BUSINESS_ID, 1, null);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testGetAllBusinessRatingsFilteredWithFilter() {
        List<Rating> ratings = List.of(rating);
        RatingsFilters filter = RatingsFilters.RATING_DESC;
        Mockito.when(businessService.findById(BUSINESS_ID)).thenReturn(Optional.of(business));
        Mockito.when(ratingDao.getAllBusinessRatingsFiltered(BUSINESS_ID, 1, PAGE_SIZE, filter))
                .thenReturn(ratings);

        List<Rating> result = ratingService.getAllBusinessRatingsFiltered(BUSINESS_ID, 1, filter);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testGetRatingsAvgByRate() {
        List<Object[]> daoResult = new ArrayList<>();
        daoResult.add(new Object[]{5, 10L});
        daoResult.add(new Object[]{4, 5L});
        daoResult.add(new Object[]{3, 2L});

        Mockito.when(serviceService.findById(SERVICE_ID)).thenReturn(Optional.of(service));
        Mockito.when(ratingDao.getRatingsAvgByRate(SERVICE_ID)).thenReturn(daoResult);

        Map<Integer, Double> result = ratingService.getRatingsAvgByRate(SERVICE_ID);

        Assert.assertNotNull(result);
        Assert.assertEquals(5, result.size());
        Assert.assertEquals(Double.valueOf(10.0), result.get(5));
        Assert.assertEquals(Double.valueOf(5.0), result.get(4));
        Assert.assertEquals(Double.valueOf(2.0), result.get(3));
        Assert.assertEquals(Double.valueOf(0.0), result.get(2));
        Assert.assertEquals(Double.valueOf(0.0), result.get(1));
    }

    @Test
    public void testGetRatingsAvgByRateEmpty() {
        Mockito.when(serviceService.findById(SERVICE_ID)).thenReturn(Optional.of(service));
        Mockito.when(ratingDao.getRatingsAvgByRate(SERVICE_ID)).thenReturn(Collections.emptyList());

        Map<Integer, Double> result = ratingService.getRatingsAvgByRate(SERVICE_ID);

        Assert.assertNotNull(result);
        Assert.assertEquals(5, result.size());
        for (int i = 1; i <= 5; i++) {
            Assert.assertEquals(Double.valueOf(0.0), result.get(i));
        }
    }

    @Test
    public void testGetRatingsAvgByRateServiceNotFound() {
        Mockito.when(serviceService.findById(SERVICE_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(ServiceNotFoundException.class,
                () -> ratingService.getRatingsAvgByRate(SERVICE_ID));

        verify(ratingDao, never()).getRatingsAvgByRate(Mockito.anyLong());
    }

    @Test
    public void testGetBusinessRatingsAvgByRate() {
        List<Object[]> daoResult = new ArrayList<>();
        daoResult.add(new Object[]{5, 8L});
        daoResult.add(new Object[]{4, 3L});

        Mockito.when(businessService.findById(BUSINESS_ID)).thenReturn(Optional.of(business));
        Mockito.when(ratingDao.getBusinessRatingsAvgByRate(BUSINESS_ID)).thenReturn(daoResult);

        Map<Integer, Double> result = ratingService.getBusinessRatingsAvgByRate(BUSINESS_ID);

        Assert.assertNotNull(result);
        Assert.assertEquals(5, result.size());
        Assert.assertEquals(Double.valueOf(8.0), result.get(5));
        Assert.assertEquals(Double.valueOf(3.0), result.get(4));
    }

    @Test
    public void testGetBusinessRatingsAvgByRateNotFound() {
        Mockito.when(businessService.findById(BUSINESS_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(ServiceNotFoundException.class,
                () -> ratingService.getBusinessRatingsAvgByRate(BUSINESS_ID));

        verify(ratingDao, never()).getBusinessRatingsAvgByRate(Mockito.anyLong());
    }

    @Test
    public void testGetBusinessRatingsPageCountRemainder() throws Exception {
        setFieldValue(business, "businessRatingCount", 25.0);

        int pageCount = ratingService.getBusinessRatingsPageCount(business);

        Assert.assertEquals(3, pageCount);
    }

    @Test
    public void testGetBusinessRatingsPageCountExact() throws Exception {
        setFieldValue(business, "businessRatingCount", 20.0);

        int pageCount = ratingService.getBusinessRatingsPageCount(business);

        Assert.assertEquals(2, pageCount);
    }

    @Test
    public void testGetBusinessRatingsPageCountZero() throws Exception {
        setFieldValue(business, "businessRatingCount", 0.0);

        int pageCount = ratingService.getBusinessRatingsPageCount(business);

        Assert.assertEquals(0, pageCount);
    }

    @Test
    public void testGetBusinessRatingsPageCountPartialPage() throws Exception {
        setFieldValue(business, "businessRatingCount", 5.0);

        int pageCount = ratingService.getBusinessRatingsPageCount(business);

        Assert.assertEquals(1, pageCount);
    }
}
