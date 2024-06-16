

package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Question;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.config.TestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

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
    private static final int QUESTIONS_TO_RESPOND = 1;
    private static final int QUESTIONS_COUNT = 2;


    @Autowired
    private QuestionDaoJpa questionDao;
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DataSource ds;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup(){
        this.jdbcTemplate = new JdbcTemplate(ds);
        jdbcTemplate.execute("INSERT INTO users (userid, username, password, name, surname, email, telephone) VALUES (1, 'username', 'password', 'name', 'surname', 'email', 'telephone')");
        jdbcTemplate.execute("INSERT INTO business(businessid, userid, businessname, businessTelephone, businessEmail, businessLocation) VALUES (1, 1, 'businessname', 'businessTelephone', 'businessEmail', 'businessLocation')");
        jdbcTemplate.execute("INSERT INTO services (id, businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges, imageId) VALUES (1, 1, 'serviceName', 'serviceDescription', true, 'serviceLocation', 'Belleza', 30, 'Total', '1000', false, null);");
    }

    @Test
    public void testCreate() {
        Question qst = questionDao.create(SERVICEID, USERID, QUESTION);
        em.flush();
        Assert.assertNotNull(qst);
        Assert.assertEquals(SERVICEID, qst.getServiceid());
        Assert.assertEquals(USERID, qst.getUserid());
        Assert.assertEquals(QUESTION, qst.getQuestion());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "questions"));
    }

    @Test
    public void testRespond() {
        jdbcTemplate.execute("insert into questions (questionid, serviceid, userid, question, response, date) values (1, 1, 1, 'question', null, '2024-01-01')");
        jdbcTemplate.execute("insert into questions (questionid, serviceid, userid, question, response, date) values (2, 1, 1, 'question', null, '2024-01-01')");
        questionDao.addResponse(1, RESPONSE);
        questionDao.addResponse(2,RESPONSE2);
        em.flush();
        String response = jdbcTemplate.queryForObject("SELECT response FROM questions WHERE questionid = 1", String.class);
        String response2 = jdbcTemplate.queryForObject("SELECT response FROM questions WHERE questionid = 2", String.class);
        Assert.assertEquals(RESPONSE, response);
        Assert.assertEquals(RESPONSE2, response2);
        Assert.assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, "questions"));
    }

    @Test
    public void testQuestionsToRespond() {
        User user = em.find(User.class, USERID);
        jdbcTemplate.execute("insert into questions (questionid, serviceid, userid, question, response, date) values (1, 1,"+ user.getUserId() +", 'question', 'responded', '2024-01-01')");
        jdbcTemplate.execute("insert into questions (questionid, serviceid, userid, question, response, date) values (2, 1,"+ user.getUserId() +", 'question', null, '2024-01-01')");
        Assert.assertEquals(QUESTIONS_TO_RESPOND, questionDao.getQuestionsToRespond(user, 0, 10).size());
    }

    @Test
    public void testQuestionCount() {
        jdbcTemplate.execute("insert into questions (questionid, serviceid, userid, question, response, date) values (1, 1, 1, 'question', 'responded', '2024-01-01')");
        jdbcTemplate.execute("insert into questions (questionid, serviceid, userid, question, response, date) values (2, 1, 1, 'question', null, '2024-01-01')");

        Assert.assertEquals(QUESTIONS_COUNT, questionDao.getQuestionsCount(SERVICEID));
    }

}
