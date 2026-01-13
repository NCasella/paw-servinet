package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Business;
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
public class BusinessDaoJpaTest {

    private static final long BUSINESS_ID = 1;
    private static final long ANOTHER_BUSINESS_ID = 2;
    private static final String BUSINESS_NAME = "Business";
    private static final String ANOTHER_BUSINESS_NAME = "Business2";
    private static final long USER_ID = 1;
    private static final long ANOTHER_USER_ID = 2;
    private static final String TELEPHONE = "123456789";
    private static final String EMAIL = "mail@mail.com";
    private static final String LOCATION = "Location";

    @Autowired
    private BusinessDaoJpa businessDaoJpa;
    @PersistenceContext
    private EntityManager em;

    @Before
    public void setup() {
        insertUser(USER_ID, "username", "email");
        insertUser(ANOTHER_USER_ID, "username2", "email2");
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

    private void insertBusiness(long id, String name, long userId) {
        String sql = """
            INSERT INTO business (businessid, businessname, userid, businessTelephone, businessEmail, businessLocation)
            VALUES (:id, :name, :userId, :telephone, :email, :location)
            """;
        em.createNativeQuery(sql)
            .setParameter("id", id)
            .setParameter("name", name)
            .setParameter("userId", userId)
            .setParameter("telephone", TELEPHONE)
            .setParameter("email", EMAIL)
            .setParameter("location", LOCATION)
            .executeUpdate();
    }

    @Test
    public void testCreateBusiness() {
        Business business = businessDaoJpa.createBusiness(BUSINESS_NAME, USER_ID, TELEPHONE, EMAIL, LOCATION);
        em.flush();
        em.clear();

        Assert.assertNotNull("A new business SHOULD be returned after creation.",
                business);

        Business persisted = em.find(Business.class, business.getBusinessid());
        Assert.assertNotNull("The created business SHOULD be persisted in the database.",
                persisted);
        Assert.assertEquals("The business name SHOULD be preserved after persistence.",
                BUSINESS_NAME, persisted.getBusinessName());
        Assert.assertEquals("The business SHOULD belong to the user who created it.",
                USER_ID, persisted.getUserId());

        long count = em.createQuery("SELECT count(b) FROM Business b", Long.class).getSingleResult();
        Assert.assertEquals("Exactly one business SHOULD exist after a single creation.",
                1L, count);
    }

    @Test
    public void testFindById() {
        insertBusiness(BUSINESS_ID, BUSINESS_NAME, USER_ID);
        em.flush();
        em.clear();

        Optional<Business> business = businessDaoJpa.findById(BUSINESS_ID);

        Assert.assertTrue("An existing business SHOULD be retrievable by its ID.",
                business.isPresent());
        Assert.assertEquals("The retrieved business SHOULD have its name preserved.",
                BUSINESS_NAME, business.get().getBusinessName());
    }

    @Test
    public void testFindByIdNotFound() {
        Optional<Business> business = businessDaoJpa.findById(999);

        Assert.assertFalse("A non-existent business SHOULD NOT be retrievable.",
                business.isPresent());
    }

    @Test
    public void testFindByBusinessName() {
        insertBusiness(BUSINESS_ID, BUSINESS_NAME, USER_ID);
        em.flush();
        em.clear();

        Optional<Business> business = businessDaoJpa.findByBusinessName(BUSINESS_NAME);

        Assert.assertTrue("An existing business SHOULD be retrievable by its name.",
                business.isPresent());
        Assert.assertEquals("The retrieved business SHOULD have the requested name.",
                BUSINESS_NAME, business.get().getBusinessName());
    }

    @Test
    public void testFindByBusinessNameNotFound() {
        Optional<Business> business = businessDaoJpa.findByBusinessName("NonExistent");

        Assert.assertFalse("A non-existent business name SHOULD return empty.",
                business.isPresent());
    }

    @Test
    public void testGetAllBusinesses() {
        insertBusiness(BUSINESS_ID, BUSINESS_NAME, USER_ID);
        insertBusiness(ANOTHER_BUSINESS_ID, ANOTHER_BUSINESS_NAME, USER_ID);
        em.flush();
        em.clear();

        List<Business> businesses = businessDaoJpa.getAllBusinesses(1, 10);

        Assert.assertNotNull("The businesses list SHOULD never be null.",
                businesses);
        Assert.assertEquals("All existing businesses SHOULD be returned.",
                2, businesses.size());
    }

    @Test
    public void testGetAllBusinessesEmpty() {
        List<Business> businesses = businessDaoJpa.getAllBusinesses(1, 10);

        Assert.assertNotNull("The businesses list SHOULD never be null even when empty.",
                businesses);
        Assert.assertTrue("No businesses SHOULD be returned when none exist.",
                businesses.isEmpty());
    }

    @Test
    public void testGetBusinessesByUser() {
        insertBusiness(BUSINESS_ID, BUSINESS_NAME, USER_ID);
        insertBusiness(ANOTHER_BUSINESS_ID, ANOTHER_BUSINESS_NAME, ANOTHER_USER_ID);
        em.flush();
        em.clear();

        List<Business> businessesForFirstUser = businessDaoJpa.getBusinessesByUser(USER_ID, 1, 10);
        List<Business> businessesForSecondUser = businessDaoJpa.getBusinessesByUser(ANOTHER_USER_ID, 1, 10);

        Assert.assertNotNull("The user's businesses list SHOULD never be null.",
                businessesForFirstUser);
        Assert.assertEquals("Only businesses owned by the user SHOULD be returned.",
                1, businessesForFirstUser.size());

        Assert.assertNotNull("The user's businesses list SHOULD never be null.",
                businessesForSecondUser);
        Assert.assertEquals("Only businesses owned by the user SHOULD be returned.",
                1, businessesForSecondUser.size());
    }

    @Test
    public void testGetBusinessesByUserEmpty() {
        List<Business> businesses = businessDaoJpa.getBusinessesByUser(USER_ID, 1, 10);

        Assert.assertNotNull("The user's businesses list SHOULD never be null.",
                businesses);
        Assert.assertTrue("No businesses SHOULD be returned for a user with none.",
                businesses.isEmpty());
    }

    @Test
    public void testGetBusinessesCount() {
        insertBusiness(BUSINESS_ID, BUSINESS_NAME, USER_ID);
        insertBusiness(ANOTHER_BUSINESS_ID, ANOTHER_BUSINESS_NAME, USER_ID);
        em.flush();
        em.clear();

        int count = businessDaoJpa.getBusinessesCount();

        Assert.assertEquals("The count SHOULD reflect all existing businesses.",
                2, count);
    }

    @Test
    public void testGetBusinessesCountZero() {
        int count = businessDaoJpa.getBusinessesCount();

        Assert.assertEquals("The count SHOULD be zero when no businesses exist.",
                0, count);
    }

    @Test
    public void testGetBusinessesCountByUser() {
        insertBusiness(BUSINESS_ID, BUSINESS_NAME, USER_ID);
        insertBusiness(ANOTHER_BUSINESS_ID, ANOTHER_BUSINESS_NAME, ANOTHER_USER_ID);
        em.flush();
        em.clear();

        int countFirstUser = businessDaoJpa.getBusinessesCountByUser(USER_ID);
        int countSecondUser = businessDaoJpa.getBusinessesCountByUser(ANOTHER_USER_ID);

        Assert.assertEquals("The count SHOULD reflect only businesses owned by that user.",
                1, countFirstUser);
        Assert.assertEquals("The count SHOULD reflect only businesses owned by that user.",
                1, countSecondUser);
    }

    @Test
    public void testGetBusinessesCountByUserZero() {
        int count = businessDaoJpa.getBusinessesCountByUser(99);

        Assert.assertEquals("The count SHOULD be zero for a user with no businesses.",
                0, count);
    }

    @Test
    public void testGetBusinessEmail() {
        insertBusiness(BUSINESS_ID, BUSINESS_NAME, USER_ID);
        em.flush();
        em.clear();

        Optional<String> email = businessDaoJpa.getBusinessEmail(BUSINESS_ID);

        Assert.assertTrue("The email SHOULD be retrievable for an existing business.",
                email.isPresent());
        Assert.assertEquals("The business email SHOULD be preserved in the database.",
                EMAIL, email.get());
    }

    @Test
    public void testGetBusinessEmailEmpty() {
        Optional<String> email = businessDaoJpa.getBusinessEmail(BUSINESS_ID);

        Assert.assertFalse("No email SHOULD be returned for a non-existent business.",
                email.isPresent());
    }

    @Test
    public void testDeleteLastBusiness() {
        insertBusiness(BUSINESS_ID, BUSINESS_NAME, USER_ID);
        em.flush();
        em.clear();

        businessDaoJpa.deleteBusiness(BUSINESS_ID);
        em.flush();
        em.clear();

        long count = em.createQuery("SELECT count(b) FROM Business b", Long.class).getSingleResult();
        Assert.assertEquals("No businesses SHOULD remain after deleting the only one.",
                0L, count);

        Business deleted = em.find(Business.class, BUSINESS_ID);
        Assert.assertNull("A deleted business SHOULD NOT be retrievable.",
                deleted);
    }

    @Test
    public void testDeleteBusinessWithMoreLeft() {
        insertBusiness(BUSINESS_ID, BUSINESS_NAME, USER_ID);
        insertBusiness(ANOTHER_BUSINESS_ID, ANOTHER_BUSINESS_NAME, USER_ID);
        em.flush();
        em.clear();

        businessDaoJpa.deleteBusiness(BUSINESS_ID);
        em.flush();
        em.clear();

        Business remaining = em.find(Business.class, ANOTHER_BUSINESS_ID);
        Assert.assertNotNull("Unrelated businesses SHOULD NOT be affected by deletion.",
                remaining);

        Business deleted = em.find(Business.class, BUSINESS_ID);
        Assert.assertNull("A deleted business SHOULD NOT be retrievable.",
                deleted);

        long count = em.createQuery("SELECT count(b) FROM Business b", Long.class).getSingleResult();
        Assert.assertEquals("Only the targeted business SHOULD be deleted.",
                1L, count);
    }

    @Test
    public void testChangeBusinessEmail() {
        insertBusiness(BUSINESS_ID, BUSINESS_NAME, USER_ID);
        em.flush();
        em.clear();

        businessDaoJpa.changeBusinessEmail(BUSINESS_ID, "newemail@test.com");
        em.flush();
        em.clear();

        Business business = em.find(Business.class, BUSINESS_ID);
        Assert.assertNotNull("The business SHOULD still exist after email update.",
                business);
        Assert.assertEquals("The business email SHOULD reflect the new value.",
                "newemail@test.com", business.getEmail());
    }

    @Test
    public void testChangeBusinessTelephone() {
        insertBusiness(BUSINESS_ID, BUSINESS_NAME, USER_ID);
        em.flush();
        em.clear();

        businessDaoJpa.changeBusinessTelephone(BUSINESS_ID, "999888777");
        em.flush();
        em.clear();

        Business business = em.find(Business.class, BUSINESS_ID);
        Assert.assertNotNull("The business SHOULD still exist after telephone update.",
                business);
        Assert.assertEquals("The business telephone SHOULD reflect the new value.",
                "999888777", business.getTelephone());
    }

    @Test
    public void testChangeBusinessLocation() {
        insertBusiness(BUSINESS_ID, BUSINESS_NAME, USER_ID);
        em.flush();
        em.clear();

        businessDaoJpa.changeBusinessLocation(BUSINESS_ID, "New Location");
        em.flush();
        em.clear();

        Business business = em.find(Business.class, BUSINESS_ID);
        Assert.assertNotNull("The business SHOULD still exist after location update.",
                business);
        Assert.assertEquals("The business location SHOULD reflect the new value.",
                "New Location", business.getLocation());
    }
}
