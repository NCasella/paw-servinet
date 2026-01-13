package ar.edu.itba.paw.persistance;

import java.time.LocalDateTime;
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

import ar.edu.itba.paw.model.UserVerificationCode;
import ar.edu.itba.paw.persistance.config.TestConfig;

@Transactional
@Rollback
@Sql("classpath:sql/schema.sql")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserVerificationDaoJpaTest {

    private static final long USER_ID = 1L;
    private static final long ANOTHER_USER_ID = 2L;
    private static final String VERIFICATION_CODE = "123456";

    @Autowired
    private UserVerificationDaoJpa userVerificationDao;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setup() {
        String sql = """
            INSERT INTO users (userid, username, password, name, surname, email, telephone, isprovider, isverified, locale)
            VALUES (:id, 'testuser', 'password', 'Test', 'User', 'test@test.com', '123456789', false, false, 'en')
            """;
        em.createNativeQuery(sql)
            .setParameter("id", USER_ID)
            .executeUpdate();
        em.flush();
    }

    private void insertAnotherUser() {
        String sql = """
            INSERT INTO users (userid, username, password, name, surname, email, telephone, isprovider, isverified, locale)
            VALUES (:id, 'testuser2', 'password', 'Test2', 'User2', 'test2@test.com', '987654321', false, false, 'en')
            """;
        em.createNativeQuery(sql)
            .setParameter("id", ANOTHER_USER_ID)
            .executeUpdate();
    }

    @Test
    public void testSaveCodes() {
        LocalDateTime expirationDate = LocalDateTime.now().plusDays(1);

        UserVerificationCode code = userVerificationDao.saveCodes(USER_ID, VERIFICATION_CODE, expirationDate);
        em.flush();
        em.clear();

        Assert.assertNotNull("The verification code object SHOULD be returned by the DAO after creation.", code);
        Assert.assertEquals("The verification code SHOULD be preserved after persistence.",
                VERIFICATION_CODE, code.getVerificationCode());

        long count = em.createQuery("SELECT COUNT(v) FROM UserVerificationCode v", Long.class).getSingleResult();
        Assert.assertEquals("The DB SHOULD contain exactly 1 verification code after creation.",
                1L, count);
    }

    @Test
    public void testGetVerificationCodeByUserIdFound() {
        LocalDateTime expirationDate = LocalDateTime.now().plusDays(1);
        userVerificationDao.saveCodes(USER_ID, VERIFICATION_CODE, expirationDate);
        em.flush();
        em.clear();

        Optional<UserVerificationCode> result = userVerificationDao.getVerificationCodeByUserId(USER_ID);

        Assert.assertTrue("An existing verification code SHOULD be retrievable by user ID.",
                result.isPresent());
        Assert.assertEquals("The verification code SHOULD be preserved in the database.",
                VERIFICATION_CODE, result.get().getVerificationCode());
    }

    @Test
    public void testGetVerificationCodeByUserIdNotFound() {
        Optional<UserVerificationCode> result = userVerificationDao.getVerificationCodeByUserId(USER_ID);

        Assert.assertFalse("The response for a user without verification code SHOULD be empty.",
                result.isPresent());
    }

    @Test
    public void testDeleteCode() {
        LocalDateTime expirationDate = LocalDateTime.now().plusDays(1);
        userVerificationDao.saveCodes(USER_ID, VERIFICATION_CODE, expirationDate);
        em.flush();
        em.clear();

        long countBefore = em.createQuery("SELECT COUNT(v) FROM UserVerificationCode v", Long.class).getSingleResult();
        Assert.assertEquals("The DB SHOULD contain 1 verification code before deletion.",
                1L, countBefore);

        userVerificationDao.deleteCode(USER_ID);
        em.flush();
        em.clear();

        long countAfter = em.createQuery("SELECT COUNT(v) FROM UserVerificationCode v", Long.class).getSingleResult();
        Assert.assertEquals("The DB SHOULD contain no verification codes after deletion.",
                0L, countAfter);
    }

    @Test
    public void testDeleteCodeWhenNoCodeExists() {
        userVerificationDao.deleteCode(USER_ID);
        em.flush();
        em.clear();

        long count = em.createQuery("SELECT COUNT(v) FROM UserVerificationCode v", Long.class).getSingleResult();
        Assert.assertEquals("The DB SHOULD remain empty when deleting non-existent code.",
                0L, count);
    }

    @Test
    public void testSaveMultipleCodesForDifferentUsers() {
        insertAnotherUser();
        em.flush();

        LocalDateTime expirationDate = LocalDateTime.now().plusDays(1);

        UserVerificationCode code1 = userVerificationDao.saveCodes(USER_ID, "111111", expirationDate);
        UserVerificationCode code2 = userVerificationDao.saveCodes(ANOTHER_USER_ID, "222222", expirationDate);
        em.flush();
        em.clear();

        Assert.assertNotNull("The first verification code object SHOULD be returned.", code1);
        Assert.assertNotNull("The second verification code object SHOULD be returned.", code2);
        Assert.assertEquals("The first verification code SHOULD be preserved after persistence.",
                "111111", code1.getVerificationCode());
        Assert.assertEquals("The second verification code SHOULD be preserved after persistence.",
                "222222", code2.getVerificationCode());

        long count = em.createQuery("SELECT COUNT(v) FROM UserVerificationCode v", Long.class).getSingleResult();
        Assert.assertEquals("The DB SHOULD contain 2 verification codes after creation.",
                2L, count);
    }

    @Test
    public void testDeleteCodeOnlyDeletesForSpecificUser() {
        insertAnotherUser();
        em.flush();

        LocalDateTime expirationDate = LocalDateTime.now().plusDays(1);
        userVerificationDao.saveCodes(USER_ID, "111111", expirationDate);
        userVerificationDao.saveCodes(ANOTHER_USER_ID, "222222", expirationDate);
        em.flush();
        em.clear();

        userVerificationDao.deleteCode(USER_ID);
        em.flush();
        em.clear();

        long count = em.createQuery("SELECT COUNT(v) FROM UserVerificationCode v", Long.class).getSingleResult();
        Assert.assertEquals("The DB SHOULD contain 1 verification code after deleting one.",
                1L, count);

        Optional<UserVerificationCode> remainingCode = userVerificationDao.getVerificationCodeByUserId(ANOTHER_USER_ID);
        Assert.assertTrue("The second user's verification code SHOULD remain.",
                remainingCode.isPresent());
        Assert.assertEquals("The remaining code SHOULD belong to the second user.",
                "222222", remainingCode.get().getVerificationCode());
    }
}
