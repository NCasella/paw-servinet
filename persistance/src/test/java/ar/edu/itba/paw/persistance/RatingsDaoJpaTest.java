

package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Rating;
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
import java.util.Optional;

@Transactional
@Rollback
@Sql("classpath:sql/schema.sql")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class RatingsDaoJpaTest {
    private static final String COMMENT = "This is a comment";
    private static final int RATING1 = 1;
    private static final long USERID = 1;
    private static final long SERVICEID = 1;

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private RatingDaoJpa ratingDao;


    @Before
    public void setup(){
        em.createNativeQuery("INSERT INTO users (userid, username, password, name, surname, email, telephone,isprovider,isverified,profilepic) VALUES (1, 'username', 'password', 'name', 'surname', 'email', 'telephone',true,true,3)").executeUpdate();
        em.createNativeQuery("INSERT INTO users (userid, username, password, name, surname, email, telephone,isprovider,isverified,profilepic) VALUES (2, 'username2', 'password2', 'name2', 'surname2', 'email2', 'telephone2',false,true,3)").executeUpdate();
        em.createNativeQuery("INSERT INTO users (userid, username, password, name, surname, email, telephone,isprovider,isverified,profilepic) VALUES (3, 'username3', 'password3', 'name2', 'surname3', 'email3', 'telephone3',false,true,3)").executeUpdate();

        em.createNativeQuery("INSERT INTO business(businessid, userid, businessname, businessTelephone, businessEmail, businessLocation) VALUES (1, 1, 'businessname', 'businessTelephone', 'businessEmail', 'businessLocation')").executeUpdate();
        em.createNativeQuery("INSERT INTO services (id, businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges, imageId) VALUES (1, 1, 'serviceName', 'serviceDescription', true, 'serviceLocation', 'Belleza', 30, 'Total', '1000', false, null);").executeUpdate();
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
        Assert.assertEquals(1, ((BigInteger)em.createNativeQuery("select count(*) from ratings").getSingleResult()).intValue());
    }


    @Test
    public void testFindById() {
        em.createNativeQuery(String.format("insert into ratings (ratingid, serviceid, userid, rating, comment,date) values (1, %d, %d, %d, '%s',CURRENT_TIMESTAMP)", SERVICEID, USERID, RATING1, COMMENT)).executeUpdate();
        Optional<Rating> ratingFound = ratingDao.findById(1);

        Assert.assertTrue(ratingFound.isPresent());
        Assert.assertEquals(SERVICEID, ratingFound.get().getServiceid());
        Assert.assertEquals(USERID, ratingFound.get().getUserid());
        Assert.assertEquals(COMMENT, ratingFound.get().getComment());
        Assert.assertEquals(RATING1, ratingFound.get().getRating());
    }





}

