package ar.edu.itba.paw.services;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserVerificationCode;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserVerificationServiceImplTest {

    private static final long USER_ID = 1L;
    private static final String VERIFICATION_CODE = "123456";
    private static final String LOCALE = "en";

    @InjectMocks
    private UserVerificationServiceImpl userVerificationService;

    @Mock
    private UserVerificationDao userVerificationDao;

    @Mock
    private EmailService emailService;

    private User user;
    private UserVerificationCode validCode;
    private UserVerificationCode expiredCode;

    @Before
    public void setup() throws Exception {
        user = createUserWithId(USER_ID, "testuser", "password", "Test", "User",
                "test@test.com", "123456789", false, LOCALE);

        validCode = new UserVerificationCode(user, VERIFICATION_CODE, LocalDateTime.now().plusDays(1));
        expiredCode = new UserVerificationCode(user, VERIFICATION_CODE, LocalDateTime.now().minusDays(1));
    }

    private User createUserWithId(long id, String username, String password, String name,
                                   String surname, String email, String telephone,
                                   boolean isProvider, String locale) throws Exception {
        User user = new User(username, password, name, surname, email, telephone, isProvider, locale);
        setFieldValue(user, "userId", id);
        return user;
    }

    private void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Class<?> clazz = obj.getClass();
        Field field = null;
        while (clazz != null && field == null) {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        if (field == null) {
            throw new NoSuchFieldException("Field " + fieldName + " not found in class hierarchy");
        }
        field.setAccessible(true);
        field.set(obj, value);
    }

    @Test
    public void testSendVerificationCodeWhenExpired() {
        UserVerificationCode newCode = new UserVerificationCode(user, "654321", LocalDateTime.now().plusDays(1));

        Mockito.when(userVerificationDao.getVerificationCodeByUserId(USER_ID))
                .thenReturn(Optional.of(expiredCode))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(newCode));
        Mockito.when(userVerificationDao.saveCodes(Mockito.eq(USER_ID), Mockito.anyString(), Mockito.any(LocalDateTime.class)))
                .thenReturn(newCode);

        userVerificationService.sendVerificationCode(user);

        verify(userVerificationDao, times(2)).deleteCode(USER_ID);
        verify(emailService).sendVerificationCode(Mockito.eq(user), Mockito.any(UserVerificationCode.class));
    }

    @Test
    public void testSendVerificationCodeWhenNotExpired() {
        Mockito.when(userVerificationDao.getVerificationCodeByUserId(USER_ID))
                .thenReturn(Optional.of(validCode));

        userVerificationService.sendVerificationCode(user);

        verify(userVerificationDao, never()).saveCodes(Mockito.anyLong(), Mockito.anyString(), Mockito.any(LocalDateTime.class));
        verify(emailService).sendVerificationCode(user, validCode);
    }

    @Test
    public void testSendVerificationCodeWhenNoExistingCode() {
        UserVerificationCode newCode = new UserVerificationCode(user, "654321", LocalDateTime.now().plusDays(1));

        Mockito.when(userVerificationDao.getVerificationCodeByUserId(USER_ID))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(newCode));
        Mockito.when(userVerificationDao.saveCodes(Mockito.eq(USER_ID), Mockito.anyString(), Mockito.any(LocalDateTime.class)))
                .thenReturn(newCode);

        userVerificationService.sendVerificationCode(user);

        verify(userVerificationDao, times(2)).deleteCode(USER_ID);
        verify(userVerificationDao).saveCodes(Mockito.eq(USER_ID), Mockito.anyString(), Mockito.any(LocalDateTime.class));
        verify(emailService).sendVerificationCode(Mockito.eq(user), Mockito.any(UserVerificationCode.class));
    }

    @Test
    public void testIsVerificationExpiredWhenExpired() {
        Mockito.when(userVerificationDao.getVerificationCodeByUserId(USER_ID))
                .thenReturn(Optional.of(expiredCode));

        boolean result = userVerificationService.isVerificationExpired(USER_ID);

        Assert.assertTrue(result);
    }

    @Test
    public void testIsVerificationExpiredWhenNotExpired() {
        Mockito.when(userVerificationDao.getVerificationCodeByUserId(USER_ID))
                .thenReturn(Optional.of(validCode));

        boolean result = userVerificationService.isVerificationExpired(USER_ID);

        Assert.assertFalse(result);
    }

    @Test
    public void testIsVerificationExpiredWhenNoCode() {
        Mockito.when(userVerificationDao.getVerificationCodeByUserId(USER_ID))
                .thenReturn(Optional.empty());

        boolean result = userVerificationService.isVerificationExpired(USER_ID);

        Assert.assertTrue(result);
    }

    @Test
    public void testGenerateVerificationCode() {
        UserVerificationCode newCode = new UserVerificationCode(user, VERIFICATION_CODE, LocalDateTime.now().plusDays(1));
        Mockito.when(userVerificationDao.saveCodes(Mockito.eq(USER_ID), Mockito.anyString(), Mockito.any(LocalDateTime.class)))
                .thenReturn(newCode);

        UserVerificationCode result = userVerificationService.generateVerificationCode(USER_ID);

        Assert.assertNotNull(result);
        verify(userVerificationDao).deleteCode(USER_ID);
        verify(userVerificationDao).saveCodes(Mockito.eq(USER_ID), Mockito.anyString(), Mockito.any(LocalDateTime.class));
    }

    @Test
    public void testVerifyUserSuccess() {
        Mockito.when(userVerificationDao.getVerificationCodeByUserId(USER_ID))
                .thenReturn(Optional.of(validCode));

        boolean result = userVerificationService.verifyUser(USER_ID, VERIFICATION_CODE);

        Assert.assertTrue(result);
    }

    @Test
    public void testVerifyUserWrongCode() {
        Mockito.when(userVerificationDao.getVerificationCodeByUserId(USER_ID))
                .thenReturn(Optional.of(validCode));

        boolean result = userVerificationService.verifyUser(USER_ID, "wrongcode");

        Assert.assertFalse(result);
    }

    @Test
    public void testVerifyUserNoCode() {
        Mockito.when(userVerificationDao.getVerificationCodeByUserId(USER_ID))
                .thenReturn(Optional.empty());

        boolean result = userVerificationService.verifyUser(USER_ID, VERIFICATION_CODE);

        Assert.assertFalse(result);
    }

    @Test
    public void testDeleteCode() {
        userVerificationService.deleteCode(USER_ID);

        verify(userVerificationDao).deleteCode(USER_ID);
    }

    @Test
    public void testGetUserVerificationCodeByUserId() {
        Mockito.when(userVerificationDao.getVerificationCodeByUserId(USER_ID))
                .thenReturn(Optional.of(validCode));

        Optional<UserVerificationCode> result = userVerificationService.getUserVerificationCodeByUserId(USER_ID);

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(VERIFICATION_CODE, result.get().getVerificationCode());
    }

    @Test
    public void testGetUserVerificationCodeByUserIdNotFound() {
        Mockito.when(userVerificationDao.getVerificationCodeByUserId(USER_ID))
                .thenReturn(Optional.empty());

        Optional<UserVerificationCode> result = userVerificationService.getUserVerificationCodeByUserId(USER_ID);

        Assert.assertFalse(result.isPresent());
    }
}
