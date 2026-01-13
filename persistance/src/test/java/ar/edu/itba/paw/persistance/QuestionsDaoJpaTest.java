package ar.edu.itba.paw.persistance;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

import ar.edu.itba.paw.model.Question;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.config.TestConfig;

@Transactional
@Rollback
@Sql("classpath:sql/schema.sql")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class QuestionsDaoJpaTest {
    private static final String QUESTION = "This is a question";
    private static final String RESPONSE = "This is a response";
    private static final long USER_ID = 1;
    private static final long ANOTHER_USER_ID = 2;
    private static final long SERVICE_ID = 1;
    private static final long ANOTHER_SERVICE_ID = 2;
    private static final long BUSINESS_ID = 1;
    private static final long QUESTION_ID = 1;
    private static final long ANOTHER_QUESTION_ID = 2;

    @Autowired
    private QuestionDaoJpa questionDao;
    @PersistenceContext
    private EntityManager em;

    @Before
    public void setup() {
        insertUser(USER_ID, "username", "email", true);
        insertUser(ANOTHER_USER_ID, "username2", "email2", false);
        insertBusiness(BUSINESS_ID, USER_ID);
        insertService(SERVICE_ID, BUSINESS_ID, "serviceName", "Belleza");
        insertService(ANOTHER_SERVICE_ID, BUSINESS_ID, "serviceName2", "Limpieza");
        em.flush();
    }

    private void insertUser(long id, String username, String email, boolean isProvider) {
        String sql = """
            INSERT INTO users (userid, username, password, name, surname, email, telephone, isprovider, isverified, profilepic)
            VALUES (:id, :username, 'password', 'name', 'surname', :email, 'telephone', :isProvider, true, 3)
            """;
        em.createNativeQuery(sql)
            .setParameter("id", id)
            .setParameter("username", username)
            .setParameter("email", email)
            .setParameter("isProvider", isProvider)
            .executeUpdate();
    }

    private void insertBusiness(long id, long userId) {
        String sql = """
            INSERT INTO business (businessid, userid, businessname, businessTelephone, businessEmail, businessLocation)
            VALUES (:id, :userId, 'businessname', 'businessTelephone', 'businessEmail', 'businessLocation')
            """;
        em.createNativeQuery(sql)
            .setParameter("id", id)
            .setParameter("userId", userId)
            .executeUpdate();
    }

    private void insertService(long id, long businessId, String name, String category) {
        String sql = """
            INSERT INTO services (id, businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges, imageId)
            VALUES (:id, :businessId, :name, 'description', true, 'location', :category, 30, 'Total', '1000', false, null)
            """;
        em.createNativeQuery(sql)
            .setParameter("id", id)
            .setParameter("businessId", businessId)
            .setParameter("name", name)
            .setParameter("category", category)
            .executeUpdate();
    }

    private void insertQuestion(long id, long serviceId, long userId, String question, String response) {
        String sql = """
            INSERT INTO questions (questionid, serviceid, userid, question, response, date)
            VALUES (:id, :serviceId, :userId, :question, :response, '2024-01-01')
            """;
        em.createNativeQuery(sql)
            .setParameter("id", id)
            .setParameter("serviceId", serviceId)
            .setParameter("userId", userId)
            .setParameter("question", question)
            .setParameter("response", response)
            .executeUpdate();
    }

    @Test
    public void testCreate() {
        Question qst = questionDao.create(SERVICE_ID, USER_ID, QUESTION);
        em.flush();
        em.clear();

        Assert.assertNotNull("The question object SHOULD be returned by the DAO after creation.", qst);

        Question persisted = em.find(Question.class, qst.getId());
        Assert.assertNotNull("The question SHOULD be retrievable from the DB by its ID.", persisted);
        Assert.assertEquals("The questioned service SHOULD be preserved after persistence.",
                SERVICE_ID, persisted.getServiceid());
        Assert.assertEquals("The question author SHOULD be preserved after persistence.",
                USER_ID, persisted.getUserid());
        Assert.assertEquals("The question text SHOULD be preserved after persistence.",
                QUESTION, persisted.getQuestion());

        long count = em.createQuery("SELECT count(q) FROM Question q", Long.class).getSingleResult();
        Assert.assertEquals("The DB SHOULD contain exactly 1 question after creation.",
                1L, count);
    }

    @Test
    public void testRespond() {
        insertQuestion(QUESTION_ID, SERVICE_ID, USER_ID, QUESTION, null);
        insertQuestion(ANOTHER_QUESTION_ID, SERVICE_ID, USER_ID, QUESTION, null);
        em.flush();
        em.clear();

        questionDao.addResponse(QUESTION_ID, RESPONSE);
        em.flush();
        em.clear();

        Question firstQuestion = em.find(Question.class, QUESTION_ID);
        Question secondQuestion = em.find(Question.class, ANOTHER_QUESTION_ID);

        Assert.assertEquals("The first question's response SHOULD be updated to the new value.",
                RESPONSE, firstQuestion.getResponse());
        Assert.assertNull("The second question's response SHOULD remain null as it was not updated.",
                secondQuestion.getResponse());
    }

    @Test
    public void testQuestionsToRespond() {
        User user = em.find(User.class, USER_ID);
        insertQuestion(1, SERVICE_ID, USER_ID, QUESTION, RESPONSE);
        insertQuestion(2, SERVICE_ID, USER_ID, QUESTION, null);
        insertQuestion(3, SERVICE_ID, USER_ID, QUESTION, null);
        em.flush();
        em.clear();

        List<Question> questionsToRespond = questionDao.getQuestionsToRespond(user.getUserId(), 1, 10);

        Assert.assertEquals("The questions to respond list SHOULD contain only unanswered questions.",
                2, questionsToRespond.size());
    }

    @Test
    public void testQuestionCount() {
        insertQuestion(QUESTION_ID, SERVICE_ID, USER_ID, QUESTION, RESPONSE);
        insertQuestion(ANOTHER_QUESTION_ID, SERVICE_ID, USER_ID, QUESTION, null);
        em.flush();
        em.clear();

        int count = questionDao.getQuestionsCount();

        Assert.assertEquals("The questions count SHOULD match the total number of questions in the DB.",
                2, count);
    }

    @Test
    public void testFindById() {
        insertQuestion(QUESTION_ID, SERVICE_ID, USER_ID, QUESTION, null);
        em.flush();
        em.clear();

        Optional<Question> result = questionDao.findById(QUESTION_ID);

        Assert.assertTrue("An existing question SHOULD be retrievable by its ID.",
                result.isPresent());
        Assert.assertEquals("The question text SHOULD be preserved in the database.",
                QUESTION, result.get().getQuestion());
        Assert.assertEquals("The questioned service SHOULD be preserved in the database.",
                SERVICE_ID, result.get().getServiceid());
    }

    @Test
    public void testFindByIdNotFound() {
        Optional<Question> result = questionDao.findById(999);

        Assert.assertFalse("A non-existent question SHOULD NOT be retrievable.",
                result.isPresent());
    }

    @Test
    public void testGetAllQuestions() {
        insertQuestion(1, SERVICE_ID, USER_ID, QUESTION, null);
        insertQuestion(2, SERVICE_ID, USER_ID, QUESTION, null);
        insertQuestion(3, ANOTHER_SERVICE_ID, USER_ID, QUESTION, null);
        em.flush();
        em.clear();

        List<Question> questions = questionDao.getAllQuestions(1, 10);

        Assert.assertNotNull("The questions list SHOULD be returned.", questions);
        Assert.assertEquals("The questions list SHOULD contain all questions in the DB.",
                3, questions.size());
    }

    @Test
    public void testGetAllQuestionsEmpty() {
        List<Question> questions = questionDao.getAllQuestions(1, 10);

        Assert.assertNotNull("The questions list SHOULD be returned even when empty.", questions);
        Assert.assertTrue("The questions list SHOULD be empty when there are no questions in the DB.",
                questions.isEmpty());
    }

    @Test
    public void testGetAllQuestionsPagination() {
        for (int i = 1; i <= 5; i++) {
            insertQuestion(i, SERVICE_ID, USER_ID, QUESTION + i, null);
        }
        em.flush();
        em.clear();

        List<Question> page1 = questionDao.getAllQuestions(1, 2);
        List<Question> page2 = questionDao.getAllQuestions(2, 2);
        List<Question> page3 = questionDao.getAllQuestions(3, 2);

        Assert.assertEquals("The first page SHOULD contain 2 questions.", 2, page1.size());
        Assert.assertEquals("The second page SHOULD contain 2 questions.", 2, page2.size());
        Assert.assertEquals("The third page SHOULD contain 1 question.", 1, page3.size());
    }

    @Test
    public void testGetServiceQuestions() {
        insertQuestion(1, SERVICE_ID, USER_ID, QUESTION, null);
        insertQuestion(2, SERVICE_ID, USER_ID, QUESTION, null);
        insertQuestion(3, ANOTHER_SERVICE_ID, USER_ID, QUESTION, null);
        em.flush();
        em.clear();

        List<Question> service1Questions = questionDao.getServiceQuestions(SERVICE_ID, 1, 10);

        Assert.assertNotNull("The service questions list SHOULD be returned.", service1Questions);
        Assert.assertEquals("The service questions list SHOULD contain only questions for the specified service.",
                2, service1Questions.size());
        for (Question q : service1Questions) {
            Assert.assertEquals("Each question's service ID SHOULD match the filter parameter.",
                    SERVICE_ID, q.getServiceid());
        }
    }

    @Test
    public void testGetServiceQuestionsEmpty() {
        List<Question> questions = questionDao.getServiceQuestions(SERVICE_ID, 1, 10);

        Assert.assertNotNull("The service questions list SHOULD be returned even when empty.",
                questions);
        Assert.assertTrue("The service questions list SHOULD be empty when there are no questions for the service.",
                questions.isEmpty());
    }

    @Test
    public void testGetServiceQuestionsOtherServiceExcluded() {
        insertQuestion(QUESTION_ID, ANOTHER_SERVICE_ID, USER_ID, QUESTION, null);
        em.flush();
        em.clear();

        List<Question> service1Questions = questionDao.getServiceQuestions(SERVICE_ID, 1, 10);

        Assert.assertNotNull("The service questions list SHOULD be returned.", service1Questions);
        Assert.assertTrue("The service questions list SHOULD NOT contain questions from other services.",
                service1Questions.isEmpty());
    }

    @Test
    public void testGetServiceQuestionsPagination() {
        for (int i = 1; i <= 5; i++) {
            insertQuestion(i, SERVICE_ID, USER_ID, QUESTION + i, null);
        }
        em.flush();
        em.clear();

        List<Question> page1 = questionDao.getServiceQuestions(SERVICE_ID, 1, 3);
        List<Question> page2 = questionDao.getServiceQuestions(SERVICE_ID, 2, 3);

        Assert.assertEquals("The first page SHOULD contain 3 questions.", 3, page1.size());
        Assert.assertEquals("The second page SHOULD contain 2 questions.", 2, page2.size());
    }

    @Test
    public void testGetQuestionsCountByService() {
        insertQuestion(1, SERVICE_ID, USER_ID, QUESTION, null);
        insertQuestion(2, SERVICE_ID, USER_ID, QUESTION, null);
        insertQuestion(3, ANOTHER_SERVICE_ID, USER_ID, QUESTION, null);
        em.flush();
        em.clear();

        int countService1 = questionDao.getQuestionsCountByService(SERVICE_ID);
        int countService2 = questionDao.getQuestionsCountByService(ANOTHER_SERVICE_ID);

        Assert.assertEquals("The questions count for first service SHOULD match its question count.",
                2, countService1);
        Assert.assertEquals("The questions count for second service SHOULD match its question count.",
                1, countService2);
    }

    @Test
    public void testGetQuestionsCountByServiceEmpty() {
        int count = questionDao.getQuestionsCountByService(SERVICE_ID);

        Assert.assertEquals("The questions count SHOULD be zero for a service with no questions.",
                0, count);
    }

    @Test
    public void testGetQuestionsToRespondCount() {
        insertQuestion(1, SERVICE_ID, ANOTHER_USER_ID, QUESTION, null);
        insertQuestion(2, SERVICE_ID, ANOTHER_USER_ID, QUESTION, null);
        insertQuestion(3, SERVICE_ID, ANOTHER_USER_ID, QUESTION, RESPONSE);
        em.flush();
        em.clear();

        int count = questionDao.getQuestionsToRespondCount(USER_ID);

        Assert.assertEquals("The questions to respond count SHOULD match the number of unanswered questions.",
                2, count);
    }

    @Test
    public void testGetQuestionsToRespondCountEmpty() {
        int count = questionDao.getQuestionsToRespondCount(USER_ID);

        Assert.assertEquals("The questions to respond count SHOULD be zero when there are no unanswered questions.",
                0, count);
    }

    @Test
    public void testGetQuestionsToRespondCountAllAnswered() {
        insertQuestion(QUESTION_ID, SERVICE_ID, ANOTHER_USER_ID, QUESTION, RESPONSE);
        insertQuestion(ANOTHER_QUESTION_ID, SERVICE_ID, ANOTHER_USER_ID, QUESTION, RESPONSE);
        em.flush();
        em.clear();

        int count = questionDao.getQuestionsToRespondCount(USER_ID);

        Assert.assertEquals("The questions to respond count SHOULD be zero when all questions are answered.",
                0, count);
    }

    @Test
    public void testQuestionsToRespondEmpty() {
        List<Question> questions = questionDao.getQuestionsToRespond(USER_ID, 1, 10);

        Assert.assertNotNull("The questions to respond list SHOULD be returned even when empty.", questions);
        Assert.assertTrue("The questions to respond list SHOULD be empty when there are no unanswered questions.",
                questions.isEmpty());
    }
}
