package ar.edu.itba.paw.persistance;

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
import java.util.Optional;

// Uso una base de datos provista por un tercero
@Transactional
@Rollback
@Sql("classpath:sql/schema.sql")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserDaoJpaTest {
    private static final int USERID = 1;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "mepassword";
    private static final String NAME = "name";
    private static final String SURNAME = "surname";
    private static final String EMAIL = "email";
    private static final String TELEPHONE = "telephone";
    private static final String LOCALE = "en";

    @Autowired
    private UserDaoJpa userDao;

    @PersistenceContext
    private EntityManager em;


    @Before
    public void setup() {

    }

    @Test
    public void testCreate() {
        // 1. Precondiciones (una sola)

        // 2. Ejecuta la class under test (una sola)
        User user = userDao.create(USERNAME, NAME, SURNAME,PASSWORD, EMAIL, TELEPHONE, false, LOCALE);
        // 3. Postcondiciones - assertions (todas las que sean necesarias)
        em.flush();
        Assert.assertNotNull(user);
        Assert.assertEquals(USERNAME, user.getUsername());
        Assert.assertEquals(NAME, user.getName());
        Assert.assertEquals(SURNAME, user.getSurname());
        Assert.assertEquals(EMAIL, user.getEmail());
        Assert.assertEquals(1, ((BigInteger)em.createNativeQuery("select count(*) from users").getSingleResult()).intValue());
    }
    @Test
    public void testFindById() {
        em.createNativeQuery("INSERT INTO users (userid, username, password, name, surname, email, telephone,isprovider,isverified) VALUES (1, 'username', 'mepassword', 'name', 'surname', 'email', 'telephone',false,true)").executeUpdate();
        Optional<User> user = userDao.findById(USERID);
        Assert.assertTrue(user.isPresent());
        Assert.assertEquals(USERID, user.get().getUserId());
        Assert.assertEquals(USERNAME, user.get().getUsername());
        Assert.assertEquals(PASSWORD, user.get().getPassword());
        Assert.assertEquals(NAME, user.get().getName());
    }
    @Test
    public void testChangeUsername() {
        // 1. Precondiciones
        em.createNativeQuery(String.format("INSERT INTO users (userid,username, password, name, surname, email, telephone, isprovider,isverified) VALUES (%d,'%s','%s','%s','%s','%s','%s',false,true)",USERID,USERNAME, PASSWORD, NAME, SURNAME, EMAIL, TELEPHONE)).executeUpdate();

        // 2. Ejecuta la class under test (una sola)
        userDao.changeUsername(USERID,"newUsername");
        em.flush();
        // 3. Postcondiciones - assertions (todas las que sean necesarias)
        Assert.assertEquals(1,((BigInteger)em.createNativeQuery("select count(*) from users where username= 'newUsername' ").getSingleResult()).intValue());
    }
}
