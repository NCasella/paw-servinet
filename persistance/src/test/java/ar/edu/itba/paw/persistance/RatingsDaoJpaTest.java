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
import java.util.List;
import java.util.Optional;

@Transactional
@Rollback
@Sql("classpath:sql/schema.sql")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class RatingsDaoJpaTest {
    private static final String COMMENT = "This is a comment";
    private static final int RATING = 1;
    private static final int HIGH_RATING = 5;
    private static final int LOW_RATING = 1;
    private static final long USER_ID = 1;
    private static final long ANOTHER_USER_ID = 2;
    private static final long THIRD_USER_ID = 3;
    private static final long SERVICE_ID = 1;
    private static final long BUSINESS_ID = 1;

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private RatingDaoJpa ratingDao;

    @Before
    public void setup() {
        insertUser(USER_ID, "username", "email");
        insertUser(ANOTHER_USER_ID, "username2", "email2");
        insertUser(THIRD_USER_ID, "username3", "email3");
        insertBusiness(BUSINESS_ID, USER_ID);
        insertService(SERVICE_ID, BUSINESS_ID);
    }

    private void insertUser(long id, String username, String email) {
        String sql = """
            INSERT INTO users (userid, username, password, name, surname, email, telephone, isprovider, isverified, profilepic)
            VALUES (:id, :username, 'password', 'name', 'surname', :email, 'telephone', true, true, 3)
            """;
        em.createNativeQuery(sql)
            .setParameter("id", id)
            .setParameter("username", username)
            .setParameter("email", email)
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

    private void insertService(long id, long businessId) {
        String sql = """
            INSERT INTO services (id, businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges, imageId)
            VALUES (:id, :businessId, 'serviceName', 'serviceDescription', true, 'serviceLocation', 'Belleza', 30, 'Total', '1000', false, null)
            """;
        em.createNativeQuery(sql)
            .setParameter("id", id)
            .setParameter("businessId", businessId)
            .executeUpdate();
    }

    private void insertRating(long id, long serviceId, long userId, int rating, String comment) {
        String sql = """
            INSERT INTO ratings (ratingid, serviceid, userid, rating, comment, date)
            VALUES (:id, :serviceId, :userId, :rating, :comment, CURRENT_TIMESTAMP)
            """;
        em.createNativeQuery(sql)
            .setParameter("id", id)
            .setParameter("serviceId", serviceId)
            .setParameter("userId", userId)
            .setParameter("rating", rating)
            .setParameter("comment", comment)
            .executeUpdate();
    }

    @Test
    public void testCreate() {
        Rating rating = ratingDao.create(SERVICE_ID, USER_ID, RATING, COMMENT);
        em.flush();
        em.clear();

        Assert.assertNotNull("The rating object SHOULD be returned by the DAO after creation.", rating);

        Rating persisted = em.find(Rating.class, rating.getId());
        Assert.assertNotNull("The rating SHOULD be retrievable from the DB by its ID.", persisted);
        Assert.assertEquals("The rated service SHOULD be preserved after persistence.",
                SERVICE_ID, persisted.getServiceid());
        Assert.assertEquals("The rating author SHOULD be preserved after persistence.",
                USER_ID, persisted.getUserid());
        Assert.assertEquals("The rating comment SHOULD be preserved after persistence.",
                COMMENT, persisted.getComment());
        Assert.assertEquals("The rating score SHOULD be preserved after persistence.",
                RATING, persisted.getRating());

        long count = em.createQuery("SELECT count(r) FROM Rating r", Long.class).getSingleResult();
        Assert.assertEquals("The DB SHOULD contain exactly 1 rating after creation.", 1L, count);
    }

    @Test
    public void testFindById() {
        insertRating(1, SERVICE_ID, USER_ID, RATING, COMMENT);
        em.flush();
        em.clear();

        Optional<Rating> ratingFound = ratingDao.findById(1);

        Assert.assertTrue("An existing rating SHOULD be retrievable by its ID.",
                ratingFound.isPresent());
        Assert.assertEquals("The rated service SHOULD be preserved in the database.",
                SERVICE_ID, ratingFound.get().getServiceid());
        Assert.assertEquals("The rating author SHOULD be preserved in the database.",
                USER_ID, ratingFound.get().getUserid());
        Assert.assertEquals("The rating comment SHOULD be preserved in the database.",
                COMMENT, ratingFound.get().getComment());
        Assert.assertEquals("The rating score SHOULD be preserved in the database.",
                RATING, ratingFound.get().getRating());
    }

    @Test
    public void testGetAllRatings() {
        insertRating(1, SERVICE_ID, USER_ID, RATING, COMMENT);
        insertRating(2, SERVICE_ID, ANOTHER_USER_ID, HIGH_RATING, COMMENT);
        em.flush();
        em.clear();

        List<Rating> ratings = ratingDao.getAllRatings(1, 10, null);

        Assert.assertNotNull("The ratings list SHOULD be returned.", ratings);
        Assert.assertEquals("The ratings list SHOULD contain all ratings in the DB.",
                2, ratings.size());
    }

    @Test
    public void testGetAllRatingsEmpty() {
        List<Rating> ratings = ratingDao.getAllRatings(1, 10, null);

        Assert.assertNotNull("The ratings list SHOULD be returned even when empty.", ratings);
        Assert.assertTrue("The ratings list SHOULD be empty when there are no ratings in the DB.",
                ratings.isEmpty());
    }

    @Test
    public void testGetRatingsByService() {
        insertRating(1, SERVICE_ID, USER_ID, RATING, COMMENT);
        insertRating(2, SERVICE_ID, ANOTHER_USER_ID, HIGH_RATING, COMMENT);
        em.flush();
        em.clear();

        List<Rating> ratings = ratingDao.getRatingsByService(SERVICE_ID, 1, 10, null);

        Assert.assertNotNull("The ratings list for service SHOULD be returned.", ratings);
        Assert.assertEquals("The ratings list SHOULD contain all ratings for the specified service.",
                2, ratings.size());
    }

    @Test
    public void testGetRatingsByServiceEmpty() {
        List<Rating> ratings = ratingDao.getRatingsByService(SERVICE_ID, 1, 10, null);

        Assert.assertNotNull("The ratings list SHOULD be returned even when empty.", ratings);
        Assert.assertTrue("The ratings list SHOULD be empty when there are no ratings for the service.",
                ratings.isEmpty());
    }

    @Test
    public void testGetAllRatingsCount() {
        insertRating(1, SERVICE_ID, USER_ID, RATING, COMMENT);
        insertRating(2, SERVICE_ID, ANOTHER_USER_ID, HIGH_RATING, COMMENT);
        em.flush();
        em.clear();

        int count = ratingDao.getAllRatingsCount(null);

        Assert.assertEquals("The ratings count SHOULD match the number of ratings in the DB.",
                2, count);
    }

    @Test
    public void testGetRatingsCountByService() {
        insertRating(1, SERVICE_ID, USER_ID, RATING, COMMENT);
        em.flush();
        em.clear();

        int count = ratingDao.getRatingsCountByService(SERVICE_ID, null);

        Assert.assertEquals("The ratings count SHOULD match the number of ratings for the service.",
                1, count);
    }

    @Test
    public void testHasAlreadyRatedTrue() {
        insertRating(1, SERVICE_ID, USER_ID, RATING, COMMENT);
        em.flush();
        em.clear();

        Optional<Rating> result = ratingDao.hasAlreadyRated(USER_ID, SERVICE_ID);

        Assert.assertTrue("The result SHOULD indicate the user has already rated the service.",
                result.isPresent());
    }

    @Test
    public void testHasAlreadyRatedFalse() {
        Optional<Rating> result = ratingDao.hasAlreadyRated(USER_ID, SERVICE_ID);

        Assert.assertFalse("The result SHOULD indicate the user has not rated the service.",
                result.isPresent());
    }

    @Test
    public void testEdit() {
        insertRating(1, SERVICE_ID, USER_ID, RATING, COMMENT);
        em.flush();
        em.clear();

        ratingDao.edit(1, HIGH_RATING, "Updated comment");
        em.flush();
        em.clear();

        Rating edited = em.find(Rating.class, 1L);
        Assert.assertNotNull("The rating SHOULD exist after editing.", edited);
        Assert.assertEquals("The rating value SHOULD be updated to the new value.",
                HIGH_RATING, edited.getRating());
        Assert.assertEquals("The comment SHOULD be updated to the new value.",
                "Updated comment", edited.getComment());
    }

    @Test
    public void testGetAllBusinessRatings() {
        insertRating(1, SERVICE_ID, ANOTHER_USER_ID, RATING, COMMENT);
        em.flush();
        em.clear();

        List<Rating> ratings = ratingDao.getAllBusinessRatings(BUSINESS_ID, 1, 10);

        Assert.assertNotNull("The business ratings list SHOULD be returned.", ratings);
        Assert.assertEquals("The business ratings list SHOULD contain all ratings for the business.",
                1, ratings.size());
    }

    @Test
    public void testGetRatingsAvgByRate() {
        insertRating(1, SERVICE_ID, USER_ID, HIGH_RATING, COMMENT);
        insertRating(2, SERVICE_ID, ANOTHER_USER_ID, HIGH_RATING, COMMENT);
        insertRating(3, SERVICE_ID, THIRD_USER_ID, LOW_RATING, COMMENT);
        em.flush();
        em.clear();

        List<Object[]> result = ratingDao.getRatingsAvgByRate(SERVICE_ID);

        Assert.assertNotNull("The ratings average by rate SHOULD be returned.", result);
        Assert.assertFalse("The ratings average result SHOULD NOT be empty.",
                result.isEmpty());
        Assert.assertEquals("The ratings average result SHOULD contain 2 distinct rating groups.",
                2, result.size());
    }

    @Test
    public void testGetBusinessRatingsAvgByRate() {
        insertRating(1, SERVICE_ID, ANOTHER_USER_ID, HIGH_RATING, COMMENT);
        insertRating(2, SERVICE_ID, THIRD_USER_ID, LOW_RATING, COMMENT);
        em.flush();
        em.clear();

        List<Object[]> result = ratingDao.getBusinessRatingsAvgByRate(BUSINESS_ID);

        Assert.assertNotNull("The business ratings average by rate SHOULD be returned.", result);
        Assert.assertFalse("The business ratings average result SHOULD NOT be empty.",
                result.isEmpty());
    }
}
