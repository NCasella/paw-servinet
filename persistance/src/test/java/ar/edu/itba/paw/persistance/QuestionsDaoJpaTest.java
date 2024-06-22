

package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Question;
import ar.edu.itba.paw.model.User;
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

@Transactional
@Rollback
@Sql("classpath:sql/schema.sql")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class QuestionsDaoJpaTest {
    private static final String QUESTION = "This is a question";
    private static final String RESPONSE = "This is a response";
    private static final String RESPONSE2 = "This is a response2";
    private static final long USERID = 1;
    private static final long SERVICEID = 1;
    private static final int QUESTIONS_TO_RESPOND = 2;
    private static final int QUESTIONS_COUNT = 2;


    @Autowired
    private QuestionDaoJpa questionDao;
    @PersistenceContext
    private EntityManager em;


    @Before
    public void setup(){
        em.createNativeQuery("INSERT INTO users (userid, username, password, name, surname, email, telephone,isprovider) VALUES (1, 'username', 'password', 'name', 'surname', 'email', 'telephone',true)").executeUpdate();
        em.createNativeQuery("INSERT INTO business(businessid, userid, businessname, businessTelephone, businessEmail, businessLocation) VALUES (1, 1, 'businessname', 'businessTelephone', 'businessEmail', 'businessLocation')").executeUpdate();
        em.createNativeQuery("INSERT INTO services (id, businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges, imageId) VALUES (1, 1, 'serviceName', 'serviceDescription', true, 'serviceLocation', 'Belleza', 30, 'Total', '1000', false, null);").executeUpdate();
        em.flush();
    }

    @Test
    public void testCreate() {
        Question qst = questionDao.create(SERVICEID, USERID, QUESTION);
        em.flush();
        Assert.assertNotNull(qst);
        Assert.assertEquals(SERVICEID, qst.getServiceid());
        Assert.assertEquals(USERID, qst.getUserid());
        Assert.assertEquals(QUESTION, qst.getQuestion());
        Assert.assertEquals(BigInteger.valueOf(1),em.createNativeQuery("select count(*) from questions").getSingleResult());
    }

    @Test
    public void testRespond() {
        em.createNativeQuery("insert into questions (questionid, serviceid, userid, question, response, date) values (1, 1, 1, 'question', null, '2024-01-01')").executeUpdate();
        em.createNativeQuery("insert into questions (questionid, serviceid, userid, question, response, date) values (2, 1, 1, 'question', null, '2024-01-01')").executeUpdate();
        em.flush();
        questionDao.addResponse(1, RESPONSE);
        questionDao.addResponse(2, RESPONSE2);

        String response = em.find(Question.class, (long)1).getResponse();
        String response2 = em.find(Question.class, (long)2).getResponse();
        Assert.assertEquals(RESPONSE, response);
        Assert.assertEquals(RESPONSE2, response2);
    }

    @Test
    public void testQuestionsToRespond() {
        User user = em.find(User.class, USERID);
        em.flush();
        em.createNativeQuery("insert into questions (questionid, serviceid, userid, question, response, date) values (1, 1,"+ USERID +", 'question', 'responded', '2024-01-01')").executeUpdate();
        em.createNativeQuery("insert into questions (questionid, serviceid, userid, question, response, date) values (2, 1,"+ USERID +", 'question', null, '2024-01-01')").executeUpdate();
        em.createNativeQuery("insert into questions (questionid, serviceid, userid, question, response, date) values (3, 1,"+ USERID +", 'question3', null, '2024-01-01')").executeUpdate();
        em.flush();
        Assert.assertEquals(QUESTIONS_TO_RESPOND, questionDao.getQuestionsToRespond(user, 1, 10).size());
    }

    @Test
    public void testQuestionCount() {
        em.createNativeQuery("insert into questions (questionid, serviceid, userid, question, response, date) values (1, 1, 1, 'question', 'responded', '2024-01-01')").executeUpdate();
        em.createNativeQuery("insert into questions (questionid, serviceid, userid, question, response, date) values (2, 1, 1, 'question', null, '2024-01-01')").executeUpdate();
        em.flush();
        Assert.assertEquals(QUESTIONS_COUNT, questionDao.getQuestionsCount(SERVICEID));
    }

}
