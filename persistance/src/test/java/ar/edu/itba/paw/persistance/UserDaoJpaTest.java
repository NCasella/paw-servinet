package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.AvailableLanguages;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.config.TestConfig;
import org.junit.Assert;
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
public class UserDaoJpaTest {
    private static final long USER_ID = 1;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "mepassword";
    private static final String NAME = "name";
    private static final String SURNAME = "surname";
    private static final String EMAIL = "email@test.com";
    private static final String TELEPHONE = "telephone";
    private static final String LOCALE = "en";

    @Autowired
    private UserDaoJpa userDao;

    @PersistenceContext
    private EntityManager em;

    private void insertUser(long id, String username, String email, boolean isVerified) {
        String sql = """
            INSERT INTO users (userid, username, password, name, surname, email, telephone, isprovider, isverified, profilepic)
            VALUES (:id, :username, :password, :name, :surname, :email, :telephone, false, :isVerified, 3)
            """;
        em.createNativeQuery(sql)
            .setParameter("id", id)
            .setParameter("username", username)
            .setParameter("password", PASSWORD)
            .setParameter("name", NAME)
            .setParameter("surname", SURNAME)
            .setParameter("email", email)
            .setParameter("telephone", TELEPHONE)
            .setParameter("isVerified", isVerified)
            .executeUpdate();
    }

    private void insertUserWithLocale(long id, String locale) {
        String sql = """
            INSERT INTO users (userid, username, password, name, surname, email, telephone, isprovider, isverified, profilepic, locale)
            VALUES (:id, :username, :password, :name, :surname, :email, :telephone, false, true, 1, :locale)
            """;
        em.createNativeQuery(sql)
            .setParameter("id", id)
            .setParameter("username", USERNAME)
            .setParameter("password", PASSWORD)
            .setParameter("name", NAME)
            .setParameter("surname", SURNAME)
            .setParameter("email", EMAIL)
            .setParameter("telephone", TELEPHONE)
            .setParameter("locale", locale)
            .executeUpdate();
    }

    @Test
    public void testCreate() {
        User user = userDao.create(USERNAME, NAME, SURNAME, PASSWORD, EMAIL, TELEPHONE, false, LOCALE);
        em.flush();
        em.clear();

        Assert.assertNotNull("The user object SHOULD be returned by the DAO after creation.", user);

        User persisted = em.find(User.class, user.getUserId());
        Assert.assertNotNull("The user SHOULD be retrievable from the DB by its ID.", persisted);
        Assert.assertEquals("The username SHOULD be preserved after persistence.",
                USERNAME, persisted.getUsername());
        Assert.assertEquals("The name SHOULD be preserved after persistence.",
                NAME, persisted.getName());
        Assert.assertEquals("The surname SHOULD be preserved after persistence.",
                SURNAME, persisted.getSurname());
        Assert.assertEquals("The email SHOULD be preserved after persistence.",
                EMAIL, persisted.getEmail());

        long count = em.createQuery("SELECT count(u) FROM User u", Long.class).getSingleResult();
        Assert.assertEquals("The DB SHOULD contain exactly 1 user after creation.",
                1L, count);
    }

    @Test
    public void testFindById() {
        insertUser(USER_ID, USERNAME, EMAIL, true);
        em.flush();
        em.clear();

        Optional<User> user = userDao.findById(USER_ID);

        Assert.assertTrue("An existing user SHOULD be retrievable by their ID.", user.isPresent());
        Assert.assertEquals("The user ID SHOULD be preserved after retrieval.",
                USER_ID, user.get().getUserId());
        Assert.assertEquals("The username SHOULD be preserved in the database.",
                USERNAME, user.get().getUsername());
        Assert.assertEquals("The password SHOULD be preserved in the database.",
                PASSWORD, user.get().getPassword());
        Assert.assertEquals("The name SHOULD be preserved in the database.",
                NAME, user.get().getName());
    }

    @Test
    public void testFindByIdNotFound() {
        Optional<User> user = userDao.findById(999);

        Assert.assertFalse("The response for a non-existent ID SHOULD be empty.", user.isPresent());
    }

    @Test
    public void testFindByEmail() {
        insertUser(USER_ID, USERNAME, EMAIL, true);
        em.flush();
        em.clear();

        Optional<User> user = userDao.findByEmail(EMAIL);

        Assert.assertTrue("An existing user SHOULD be retrievable by their email.",
                user.isPresent());
        Assert.assertEquals("The email SHOULD be preserved in the database.",
                EMAIL, user.get().getEmail());
    }

    @Test
    public void testFindByEmailNotFound() {
        Optional<User> user = userDao.findByEmail("nonexistent@email.com");

        Assert.assertFalse("The response for a non-existent email SHOULD be empty.",
                user.isPresent());
    }

    @Test
    public void testFindByUsername() {
        insertUser(USER_ID, USERNAME, EMAIL, true);
        em.flush();
        em.clear();

        Optional<User> user = userDao.findByUsername(USERNAME);

        Assert.assertTrue("An existing user SHOULD be retrievable by their username.",
                user.isPresent());
        Assert.assertEquals("The username SHOULD be preserved in the database.",
                USERNAME, user.get().getUsername());
    }

    @Test
    public void testFindByUsernameNotFound() {
        Optional<User> user = userDao.findByUsername("nonexistent");

        Assert.assertFalse("The response for a non-existent username SHOULD be empty.",
                user.isPresent());
    }

    @Test
    public void testChangeUsername() {
        insertUser(USER_ID, USERNAME, EMAIL, true);
        em.flush();
        em.clear();

        userDao.changeUsername(USER_ID, "newUsername");
        em.flush();
        em.clear();

        User user = em.find(User.class, USER_ID);
        Assert.assertNotNull("The user SHOULD exist after username change.", user);
        Assert.assertEquals("The username SHOULD be updated to the new value.",
                "newUsername", user.getUsername());
    }

    @Test
    public void testChangeEmail() {
        insertUser(USER_ID, USERNAME, EMAIL, true);
        em.flush();
        em.clear();

        userDao.changeEmail(USER_ID, "newemail@test.com");
        em.flush();
        em.clear();

        User user = em.find(User.class, USER_ID);
        Assert.assertNotNull("The user SHOULD exist after email change.", user);
        Assert.assertEquals("The email SHOULD be updated to the new value.",
                "newemail@test.com", user.getEmail());
    }

    @Test
    public void testChangeTelephone() {
        insertUser(USER_ID, USERNAME, EMAIL, true);
        em.flush();
        em.clear();

        userDao.changeTelephone(USER_ID, "999888777");
        em.flush();
        em.clear();

        User user = em.find(User.class, USER_ID);
        Assert.assertNotNull("The user SHOULD exist after telephone change.", user);
        Assert.assertEquals("The telephone SHOULD be updated to the new value.",
                "999888777", user.getTelephone());
    }

    @Test
    public void testChangePassword() {
        insertUser(USER_ID, USERNAME, EMAIL, true);
        em.flush();
        em.clear();

        userDao.changePassword(USER_ID, "newPassword123");
        em.flush();
        em.clear();

        User user = em.find(User.class, USER_ID);
        Assert.assertNotNull("The user SHOULD exist after password change.", user);
        Assert.assertEquals("The password SHOULD be updated to the new value.",
                "newPassword123", user.getPassword());
    }

    @Test
    public void testChangeProfilePicId() {
        insertUser(USER_ID, USERNAME, EMAIL, true);
        em.flush();
        em.clear();

        userDao.changeProfilePicId(USER_ID, 100L);
        em.flush();
        em.clear();

        User user = em.find(User.class, USER_ID);
        Assert.assertNotNull("The user SHOULD exist after profile pic change.", user);
        Assert.assertTrue("The profile pic ID SHOULD be present after update.",
                user.getProfilePicId().isPresent());
        Assert.assertEquals("The profile pic ID SHOULD be updated to the new value.",
                Long.valueOf(100L), user.getProfilePicId().get());
    }

    @Test
    public void testChangeUserType() {
        insertUser(USER_ID, USERNAME, EMAIL, true);
        em.flush();
        em.clear();

        userDao.changeUserType(USER_ID);
        em.flush();
        em.clear();

        User user = em.find(User.class, USER_ID);
        Assert.assertNotNull("The user SHOULD exist after user type change.", user);
        Assert.assertTrue("The user SHOULD be a provider after type change.",
                user.getProvider());
    }

    @Test
    public void testChangeLocale() {
        insertUserWithLocale(USER_ID, "en");
        em.flush();
        em.clear();

        userDao.changeLocale(USER_ID, AvailableLanguages.SPANISH);
        em.flush();
        em.clear();

        User user = em.find(User.class, USER_ID);
        Assert.assertNotNull("The user SHOULD exist after locale change.", user);
        Assert.assertEquals("The locale SHOULD be updated to Spanish.",
                "es", user.getLocale());
    }

    @Test
    public void testVerifyUser() {
        insertUser(USER_ID, USERNAME, EMAIL, false);
        em.flush();
        em.clear();

        userDao.verifyUser(USER_ID);
        em.flush();
        em.clear();

        User user = em.find(User.class, USER_ID);
        Assert.assertNotNull("The user SHOULD exist after verification.", user);
        Assert.assertTrue("The user SHOULD be verified after verification.",
                user.getIsVerified());
    }

    @Test
    public void testGetUsers() {
        insertUser(1, "user1", "email1@test.com", true);
        insertUser(2, "user2", "email2@test.com", true);
        em.flush();
        em.clear();

        List<User> users = userDao.getUsers(1);

        Assert.assertNotNull("The users list SHOULD be returned.", users);
        Assert.assertEquals("The users list SHOULD contain all users in the DB.",
                2, users.size());
    }

    @Test
    public void testGetUserCount() {
        insertUser(1, "user1", "email1@test.com", true);
        insertUser(2, "user2", "email2@test.com", true);
        em.flush();
        em.clear();

        int count = userDao.getUserCount();

        Assert.assertEquals("The user count SHOULD match the number of users in the DB.",
                2, count);
    }
}
