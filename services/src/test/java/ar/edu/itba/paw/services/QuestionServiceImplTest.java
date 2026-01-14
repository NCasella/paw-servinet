package ar.edu.itba.paw.services;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.model.Business;
import ar.edu.itba.paw.model.Categories;
import ar.edu.itba.paw.model.PagedList;
import ar.edu.itba.paw.model.PricingTypes;
import ar.edu.itba.paw.model.Question;
import ar.edu.itba.paw.model.Service;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.exceptions.BusinessNotFoundException;
import ar.edu.itba.paw.model.exceptions.QuestionNotFoundException;
import ar.edu.itba.paw.model.exceptions.ServiceNotFoundException;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class QuestionServiceImplTest {

    private static final long SERVICE_ID = 1L;
    private static final long USER_ID = 1L;
    private static final long OWNER_USER_ID = 2L;
    private static final long BUSINESS_ID = 1L;
    private static final long QUESTION_ID = 1L;
    private static final String QUESTION_TEXT = "Is this service available?";
    private static final String RESPONSE_TEXT = "Yes, it is!";
    private static final String USERNAME = "testuser";
    private static final String EMAIL = "test@test.com";
    private static final String LOCALE = "en";
    private static final String BUSINESS_NAME = "Test Business";
    private static final int PAGE_SIZE = 10;

    @InjectMocks
    private QuestionServiceImpl questionService;

    @Mock
    private QuestionDao questionDao;

    @Mock
    private EmailService emailService;

    @Mock
    private UserService userService;

    @Mock
    private ServiceService serviceService;

    @Mock
    private BusinessService businessService;

    private User user;
    private User businessOwner;
    private Business business;
    private Service service;
    private Question question;

    @Before
    public void setup() throws Exception {
        businessOwner = createUserWithId(OWNER_USER_ID, "owner", "password", "Owner", "User",
                "owner@test.com", "111111111", true, LOCALE);
        user = createUserWithId(USER_ID, USERNAME, "password", "Test", "User", EMAIL, "222222222", false, LOCALE);

        business = createBusinessWithId(BUSINESS_ID, BUSINESS_NAME, businessOwner, "333333333",
                EMAIL, "Business Location");

        service = createServiceWithId(SERVICE_ID, business, "Test Service", "Service Description",
                false, "Location", Categories.BELLEZA, 60, PricingTypes.PER_TOTAL, "1000", false, null);

        question = createQuestionWithId(QUESTION_ID, service, user, QUESTION_TEXT, null, LocalDate.now());
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

    private Question createQuestionWithId(long id, Service service, User user, String questionText,
                                           String response, LocalDate date) throws Exception {
        Question question = new Question(service, user, questionText, response, date);
        setFieldValue(question, "questionid", id);
        return question;
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
    public void testGetAllQuestions() {
        List<Question> questions = List.of(question);
        Mockito.when(questionDao.getAllQuestions(Mockito.eq(0), Mockito.anyInt()))
                .thenReturn(questions);
        Mockito.when(questionDao.getQuestionsCount()).thenReturn(1);

        PagedList<Question> result = questionService.getAllQuestions(0);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getList().size());
        Assert.assertEquals(1, result.getTotalElements());
    }

    @Test
    public void testGetAllQuestionsEmpty() {
        Mockito.when(questionDao.getAllQuestions(Mockito.eq(0), Mockito.anyInt()))
                .thenReturn(Collections.emptyList());
        Mockito.when(questionDao.getQuestionsCount()).thenReturn(0);

        PagedList<Question> result = questionService.getAllQuestions(0);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.getList().isEmpty());
        Assert.assertEquals(0, result.getTotalElements());
    }

    @Test
    public void testGetQuestionsByServiceSuccess() {
        List<Question> questions = List.of(question);
        Mockito.when(serviceService.findById(SERVICE_ID)).thenReturn(Optional.of(service));
        Mockito.when(questionDao.getServiceQuestions(Mockito.eq(SERVICE_ID), Mockito.eq(0), Mockito.anyInt()))
                .thenReturn(questions);
        Mockito.when(questionDao.getQuestionsCountByService(SERVICE_ID)).thenReturn(1);

        PagedList<Question> result = questionService.getQuestionsByService(SERVICE_ID, 0);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getList().size());
    }

    @Test
    public void testGetQuestionsByServiceNotFound() {
        Mockito.when(serviceService.findById(SERVICE_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(ServiceNotFoundException.class,
                () -> questionService.getQuestionsByService(SERVICE_ID, 0));

        verify(questionDao, never()).getServiceQuestions(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void testFindByIdFound() {
        Mockito.when(questionDao.findById(QUESTION_ID)).thenReturn(Optional.of(question));

        Optional<Question> result = questionService.findById(QUESTION_ID);

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(QUESTION_ID, result.get().getId());
    }

    @Test
    public void testFindByIdNotFound() {
        Mockito.when(questionDao.findById(QUESTION_ID)).thenReturn(Optional.empty());

        Optional<Question> result = questionService.findById(QUESTION_ID);

        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void testCreateSuccess() {
        Mockito.when(serviceService.findBasicServiceById(SERVICE_ID)).thenReturn(Optional.of(service));
        Mockito.when(businessService.findById(BUSINESS_ID)).thenReturn(Optional.of(business));
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(questionDao.create(SERVICE_ID, USER_ID, QUESTION_TEXT)).thenReturn(question);
        Mockito.when(userService.getUserLocale(OWNER_USER_ID)).thenReturn(LOCALE);

        Question result = questionService.create(SERVICE_ID, USER_ID, QUESTION_TEXT);

        Assert.assertNotNull(result);
        Assert.assertEquals(QUESTION_ID, result.getId());
        verify(questionDao).create(SERVICE_ID, USER_ID, QUESTION_TEXT);
        verify(emailService).askedQuestion(Mockito.eq(service), Mockito.eq(EMAIL), Mockito.eq(user), Mockito.eq(QUESTION_TEXT), Mockito.eq(LOCALE));
    }

    @Test
    public void testCreateServiceNotFound() {
        Mockito.when(serviceService.findBasicServiceById(SERVICE_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(ServiceNotFoundException.class,
                () -> questionService.create(SERVICE_ID, USER_ID, QUESTION_TEXT));

        verify(questionDao, never()).create(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testCreateBusinessNotFound() {
        Mockito.when(serviceService.findBasicServiceById(SERVICE_ID)).thenReturn(Optional.of(service));
        Mockito.when(businessService.findById(BUSINESS_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(BusinessNotFoundException.class,
                () -> questionService.create(SERVICE_ID, USER_ID, QUESTION_TEXT));

        verify(questionDao, never()).create(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testCreateUserNotFound() {
        Mockito.when(serviceService.findBasicServiceById(SERVICE_ID)).thenReturn(Optional.of(service));
        Mockito.when(businessService.findById(BUSINESS_ID)).thenReturn(Optional.of(business));
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(UserNotFoundException.class,
                () -> questionService.create(SERVICE_ID, USER_ID, QUESTION_TEXT));

        verify(questionDao, never()).create(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testAddResponseSuccess() {
        Mockito.when(questionDao.findById(QUESTION_ID)).thenReturn(Optional.of(question));
        Mockito.when(serviceService.findBasicServiceById(SERVICE_ID)).thenReturn(Optional.of(service));
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(user));

        questionService.addResponse(QUESTION_ID, RESPONSE_TEXT);

        verify(questionDao).addResponse(QUESTION_ID, RESPONSE_TEXT);
        verify(emailService).answeredQuestion(Mockito.eq(service), Mockito.eq(user), Mockito.eq(QUESTION_TEXT), Mockito.eq(RESPONSE_TEXT));
    }

    @Test
    public void testAddResponseQuestionNotFound() {
        Mockito.when(questionDao.findById(QUESTION_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(QuestionNotFoundException.class,
                () -> questionService.addResponse(QUESTION_ID, RESPONSE_TEXT));

        verify(questionDao, never()).addResponse(Mockito.anyLong(), Mockito.anyString());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testGetAllQuestionsCount() {
        Mockito.when(questionDao.getQuestionsCount()).thenReturn(10);

        int result = questionService.getAllQuestionsCount();

        Assert.assertEquals(10, result);
    }

    @Test
    public void testGetQuestionsCountByServiceSuccess() {
        Mockito.when(serviceService.findById(SERVICE_ID)).thenReturn(Optional.of(service));
        Mockito.when(questionDao.getQuestionsCountByService(SERVICE_ID)).thenReturn(5);

        int result = questionService.getQuestionsCountByService(SERVICE_ID);

        Assert.assertEquals(5, result);
    }

    @Test
    public void testGetQuestionsCountByServiceNotFound() {
        Mockito.when(serviceService.findById(SERVICE_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(ServiceNotFoundException.class,
                () -> questionService.getQuestionsCountByService(SERVICE_ID));

        verify(questionDao, never()).getQuestionsCountByService(Mockito.anyLong());
    }

    @Test
    public void testGetQuestionsToRespondSuccess() {
        List<Question> questions = List.of(question);
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(questionDao.getQuestionsToRespond(Mockito.eq(USER_ID), Mockito.eq(0), Mockito.anyInt()))
                .thenReturn(questions);
        Mockito.when(questionDao.getQuestionsToRespondCount(USER_ID)).thenReturn(1);

        PagedList<Question> result = questionService.getQuestionsToRespond(USER_ID, 0);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getList().size());
    }

    @Test
    public void testGetQuestionsToRespondUserNotFound() {
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(UserNotFoundException.class,
                () -> questionService.getQuestionsToRespond(USER_ID, 0));

        verify(questionDao, never()).getQuestionsToRespond(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void testGetQuestionsToRespondCountSuccess() {
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(questionDao.getQuestionsToRespondCount(USER_ID)).thenReturn(5);

        int result = questionService.getQuestionsToRespondCount(USER_ID);

        Assert.assertEquals(5, result);
    }

    @Test
    public void testGetQuestionsToRespondPageCountExact() {
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(questionDao.getQuestionsToRespondCount(USER_ID)).thenReturn(20);

        int result = questionService.getQuestionsToRespondPageCount(USER_ID);

        Assert.assertEquals(2, result);
    }

    @Test
    public void testGetQuestionsToRespondPageCountWithRemainder() {
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(questionDao.getQuestionsToRespondCount(USER_ID)).thenReturn(15);

        int result = questionService.getQuestionsToRespondPageCount(USER_ID);

        Assert.assertEquals(2, result);
    }

    @Test
    public void testGetQuestionsToRespondPageCountZero() {
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(questionDao.getQuestionsToRespondCount(USER_ID)).thenReturn(0);

        int result = questionService.getQuestionsToRespondPageCount(USER_ID);

        Assert.assertEquals(0, result);
    }
}
