

package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Rating;
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
import java.util.Optional;

@Transactional
@Rollback
@Sql("classpath:sql/schema.sql")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class RatingsDaoJpaTest {
    private static final String COMMENT = "This is a comment";
    private static final int RATING5 = 5;
    private static final int RATING3 = 3;
    private static final int RATING1 = 1;
    private static final long USERID = 1;
    private static final long USERID2=2;
    private static final long USERID3=3;
    private static final long SERVICEID = 1;

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private RatingDaoJpa ratingDao;

    @Autowired
    private DataSource ds;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup(){
        this.jdbcTemplate = new JdbcTemplate(ds);
        jdbcTemplate.execute("INSERT INTO users (userid, username, password, name, surname, email, telephone) VALUES (1, 'username', 'password', 'name', 'surname', 'email', 'telephone')");
        jdbcTemplate.execute("INSERT INTO users (userid, username, password, name, surname, email, telephone) VALUES (2, 'username2', 'password2', 'name2', 'surname2', 'email2', 'telephone2')");
        jdbcTemplate.execute("INSERT INTO users (userid, username, password, name, surname, email, telephone) VALUES (3, 'username3', 'password3', 'name2', 'surname3', 'email3', 'telephone3')");

        jdbcTemplate.execute("INSERT INTO business(businessid, userid, businessname, businessTelephone, businessEmail, businessLocation) VALUES (1, 1, 'businessname', 'businessTelephone', 'businessEmail', 'businessLocation')");
        jdbcTemplate.execute("INSERT INTO services (id, businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges, imageId) VALUES (1, 1, 'serviceName', 'serviceDescription', true, 'serviceLocation', 'Belleza', 30, 'Total', '1000', false, null);");
    }

    @Test
    public void testCreate() {
        Rating rating = ratingDao.create(SERVICEID, USERID, RATING1, COMMENT);
        em.flush();
        Assert.assertNotNull(rating);
        Assert.assertEquals(SERVICEID, rating.getServiceid());
        Assert.assertEquals(USERID, rating.getUserid());
        Assert.assertEquals(COMMENT, rating.getComment());
        Assert.assertEquals(RATING1, rating.getRating());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "ratings"));
    }


    @Test
    public void testFindById() {
        jdbcTemplate.execute(String.format("insert into ratings (ratingid, serviceid, userid, rating, comment) values (1, %d, %d, %d, '%s')", SERVICEID, USERID, RATING1, COMMENT));
        Optional<Rating> ratingFound = ratingDao.findById(1);

        Assert.assertTrue(ratingFound.isPresent());
        Assert.assertEquals(SERVICEID, ratingFound.get().getServiceid());
        Assert.assertEquals(USERID, ratingFound.get().getUserid());
        Assert.assertEquals(COMMENT, ratingFound.get().getComment());
        Assert.assertEquals(RATING1, ratingFound.get().getRating());
    }





}

