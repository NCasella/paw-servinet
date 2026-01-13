package ar.edu.itba.paw.persistance;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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

import ar.edu.itba.paw.model.PasswordRecoveryCode;
import ar.edu.itba.paw.persistance.config.TestConfig;

@Transactional
@Rollback
@Sql("classpath:sql/schema.sql")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class PasswordRecoveryCodeDaoJpaTest {

    private static final long USER_ID = 1L;
    private static final UUID CODE = UUID.randomUUID();

    @Autowired
    private PasswordRecoveryCodeDaoJpa passwordRecoveryCodeDao;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setup() {
        String sql = """
            INSERT INTO users (userid, username, password, name, surname, email, telephone, isprovider, isverified, locale)
            VALUES (:id, 'testuser', 'password', 'Test', 'User', 'test@test.com', '123456789', false, true, 'en')
            """;
        em.createNativeQuery(sql)
            .setParameter("id", USER_ID)
            .executeUpdate();
        em.flush();
    }

    @Test
    public void testSaveCode() {
        LocalDateTime expirationDate = LocalDateTime.now().plusDays(1);

        PasswordRecoveryCode code = passwordRecoveryCodeDao.saveCode(USER_ID, CODE, expirationDate);
        em.flush();
        em.clear();

        Assert.assertNotNull("The password recovery code object SHOULD be returned by the DAO after creation.", code);
        Assert.assertEquals("The recovery code SHOULD be preserved after persistence.",
                CODE, code.getCode());
        Assert.assertEquals("The code owner SHOULD be preserved after persistence.",
                USER_ID, code.getUserId());

        long count = em.createQuery("SELECT COUNT(p) FROM PasswordRecoveryCode p", Long.class).getSingleResult();
        Assert.assertEquals("The DB SHOULD contain exactly 1 password recovery code after creation.",
                1L, count);
    }

    @Test
    public void testGetCodeByUserIdFound() {
        LocalDateTime expirationDate = LocalDateTime.now().plusDays(1);
        passwordRecoveryCodeDao.saveCode(USER_ID, CODE, expirationDate);
        em.flush();
        em.clear();

        Optional<PasswordRecoveryCode> result = passwordRecoveryCodeDao.getCodeByUserId(USER_ID);

        Assert.assertTrue("An existing password recovery code SHOULD be retrievable by user ID.",
                result.isPresent());
        Assert.assertEquals("The recovery code SHOULD be preserved in the database.",
                CODE, result.get().getCode());
        Assert.assertEquals("The code owner SHOULD be preserved in the database.",
                USER_ID, result.get().getUserId());
    }

    @Test
    public void testGetCodeByUserIdNotFound() {
        Optional<PasswordRecoveryCode> result = passwordRecoveryCodeDao.getCodeByUserId(USER_ID);

        Assert.assertFalse("The response for a user without password recovery code SHOULD be empty.",
                result.isPresent());
    }

    @Test
    public void testGetCodeByUUIDFound() {
        LocalDateTime expirationDate = LocalDateTime.now().plusDays(1);
        passwordRecoveryCodeDao.saveCode(USER_ID, CODE, expirationDate);
        em.flush();
        em.clear();

        Optional<PasswordRecoveryCode> result = passwordRecoveryCodeDao.getCodeByUUID(CODE);

        Assert.assertTrue("An existing password recovery code SHOULD be retrievable by UUID.",
                result.isPresent());
        Assert.assertEquals("The recovery code SHOULD be preserved in the database.",
                CODE, result.get().getCode());
    }

    @Test
    public void testGetCodeByUUIDNotFound() {
        Optional<PasswordRecoveryCode> result = passwordRecoveryCodeDao.getCodeByUUID(UUID.randomUUID());

        Assert.assertFalse("The response for a non-existent UUID SHOULD be empty.",
                result.isPresent());
    }

    @Test
    public void testDeleteCode() {
        LocalDateTime expirationDate = LocalDateTime.now().plusDays(1);
        passwordRecoveryCodeDao.saveCode(USER_ID, CODE, expirationDate);
        em.flush();
        em.clear();

        long countBefore = em.createQuery("SELECT COUNT(p) FROM PasswordRecoveryCode p", Long.class).getSingleResult();
        Assert.assertEquals("The DB SHOULD contain 1 password recovery code before deletion.",
                1L, countBefore);

        passwordRecoveryCodeDao.deleteCode(USER_ID);
        em.flush();
        em.clear();

        long countAfter = em.createQuery("SELECT COUNT(p) FROM PasswordRecoveryCode p", Long.class).getSingleResult();
        Assert.assertEquals("The DB SHOULD contain no password recovery codes after deletion.",
                0L, countAfter);
    }

    @Test
    public void testDeleteCodeWhenNoCodeExists() {
        passwordRecoveryCodeDao.deleteCode(USER_ID);
        em.flush();
        em.clear();

        long count = em.createQuery("SELECT COUNT(p) FROM PasswordRecoveryCode p", Long.class).getSingleResult();
        Assert.assertEquals("The DB SHOULD remain empty when deleting non-existent code.",
                0L, count);
    }

    @Test
    public void testSaveCodeOverwritesExisting() {
        LocalDateTime expirationDate1 = LocalDateTime.now().plusDays(1);
        passwordRecoveryCodeDao.saveCode(USER_ID, CODE, expirationDate1);
        em.flush();
        em.clear();

        passwordRecoveryCodeDao.deleteCode(USER_ID);
        em.flush();

        UUID newCode = UUID.randomUUID();
        LocalDateTime expirationDate2 = LocalDateTime.now().plusDays(2);

        PasswordRecoveryCode result = passwordRecoveryCodeDao.saveCode(USER_ID, newCode, expirationDate2);
        em.flush();
        em.clear();

        Assert.assertNotNull("The new password recovery code SHOULD be returned.", result);
        Assert.assertEquals("The new recovery code SHOULD be preserved after persistence.", newCode, result.getCode());

        long count = em.createQuery("SELECT COUNT(p) FROM PasswordRecoveryCode p", Long.class).getSingleResult();
        Assert.assertEquals("The DB SHOULD contain exactly 1 password recovery code after overwrite.",
                1L, count);
    }
}
