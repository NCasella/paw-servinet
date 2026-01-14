package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.PasswordRecoveryCode;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.exceptions.InvalidRecoveryCodeException;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class PasswordRecoveryCodeServiceImplTest {

    private static final UUID CODE = UUID.randomUUID();
    private static final String EMAIL = "mail@mail.com";
    private static final String NEW_PASSWORD = "newpassword";
    private static final long USER_ID = 1L;
    private static final String LOCALE = "en";

    private User user;
    private PasswordRecoveryCode passwordRecoveryCode;
    private PasswordRecoveryCode expiredCode;

    @InjectMocks
    private PasswordRecoveryCodeServiceImpl passwordRecoveryCodeService;

    @Mock
    private PasswordRecoveryCodeDao passwordRecoveryCodeDao;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @Before
    public void setup() throws Exception {
        user = createUserWithId(USER_ID, "testuser", "password", "Test", "User",
                EMAIL, "123456789", false, LOCALE);

        passwordRecoveryCode = new PasswordRecoveryCode(user, CODE, LocalDateTime.now().plusDays(1));
        expiredCode = new PasswordRecoveryCode(user, CODE, LocalDateTime.now().minusDays(1));
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
    public void testGetUserFromRecoveryCode() {
        Mockito.when(passwordRecoveryCodeDao.getCodeByUUID(CODE))
                .thenReturn(Optional.of(passwordRecoveryCode));

        Optional<User> result = passwordRecoveryCodeService.getUserFromRecoveryCode(CODE);

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(user, result.get());
        Assert.assertEquals(USER_ID, result.get().getUserId());
    }

    @Test
    public void testGetUserFromRecoveryCodeNotFound() {
        Mockito.when(passwordRecoveryCodeDao.getCodeByUUID(CODE)).thenReturn(Optional.empty());

        Optional<User> result = passwordRecoveryCodeService.getUserFromRecoveryCode(CODE);

        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void testSendCodeNewCode() {
        Mockito.when(userService.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        Mockito.when(passwordRecoveryCodeDao.getCodeByUserId(USER_ID)).thenReturn(Optional.empty());
        Mockito.when(passwordRecoveryCodeDao.saveCode(Mockito.eq(USER_ID), Mockito.any(), Mockito.any()))
                .thenReturn(passwordRecoveryCode);

        passwordRecoveryCodeService.sendCode(EMAIL);

        verify(passwordRecoveryCodeDao).deleteCode(USER_ID);
        verify(passwordRecoveryCodeDao).saveCode(Mockito.eq(USER_ID), Mockito.any(), Mockito.any());
        verify(emailService).recoverPassword(user, passwordRecoveryCode);
    }

    @Test
    public void testSendCodeExpiredCode() {
        Mockito.when(userService.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        Mockito.when(passwordRecoveryCodeDao.getCodeByUserId(USER_ID)).thenReturn(Optional.of(expiredCode));
        Mockito.when(passwordRecoveryCodeDao.saveCode(Mockito.eq(USER_ID), Mockito.any(), Mockito.any()))
                .thenReturn(passwordRecoveryCode);

        passwordRecoveryCodeService.sendCode(EMAIL);

        verify(passwordRecoveryCodeDao).deleteCode(USER_ID);
        verify(passwordRecoveryCodeDao).saveCode(Mockito.eq(USER_ID), Mockito.any(), Mockito.any());
        verify(emailService).recoverPassword(Mockito.eq(user), Mockito.any());
    }

    @Test
    public void testSendCodeExistingValidCode() {
        Mockito.when(userService.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        Mockito.when(passwordRecoveryCodeDao.getCodeByUserId(USER_ID))
                .thenReturn(Optional.of(passwordRecoveryCode));

        passwordRecoveryCodeService.sendCode(EMAIL);

        verify(passwordRecoveryCodeDao, never()).saveCode(Mockito.anyLong(), Mockito.any(), Mockito.any());
        verify(emailService).recoverPassword(user, passwordRecoveryCode);
    }

    @Test
    public void testSendCodeUserNotFound() {
        Mockito.when(userService.findByEmail(EMAIL)).thenReturn(Optional.empty());

        passwordRecoveryCodeService.sendCode(EMAIL);

        verify(passwordRecoveryCodeDao, never()).getCodeByUserId(Mockito.anyLong());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testGenerateCode() {
        Mockito.when(passwordRecoveryCodeDao.saveCode(Mockito.eq(USER_ID), Mockito.any(), Mockito.any()))
                .thenReturn(passwordRecoveryCode);

        PasswordRecoveryCode result = passwordRecoveryCodeService.generateCode(USER_ID);

        Assert.assertNotNull(result);
        verify(passwordRecoveryCodeDao).deleteCode(USER_ID);
        verify(passwordRecoveryCodeDao).saveCode(Mockito.eq(USER_ID), Mockito.any(), Mockito.any());
    }

    @Test
    public void testValidateCodeValid() {
        Mockito.when(passwordRecoveryCodeDao.getCodeByUUID(CODE))
                .thenReturn(Optional.of(passwordRecoveryCode));

        boolean result = passwordRecoveryCodeService.validateCode(EMAIL, CODE);

        Assert.assertTrue(result);
    }

    @Test
    public void testValidateCodeInvalidEmail() {
        Mockito.when(passwordRecoveryCodeDao.getCodeByUUID(CODE))
                .thenReturn(Optional.of(passwordRecoveryCode));

        boolean result = passwordRecoveryCodeService.validateCode("wrong@email.com", CODE);

        Assert.assertFalse(result);
    }

    @Test
    public void testValidateCodeNotFound() {
        Mockito.when(passwordRecoveryCodeDao.getCodeByUUID(CODE)).thenReturn(Optional.empty());

        boolean result = passwordRecoveryCodeService.validateCode(EMAIL, CODE);

        Assert.assertFalse(result);
    }

    @Test
    public void testChangePassword() {
        Mockito.when(passwordRecoveryCodeDao.getCodeByUUID(CODE))
                .thenReturn(Optional.of(passwordRecoveryCode));
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(user));

        passwordRecoveryCodeService.changePassword(CODE, NEW_PASSWORD);

        verify(userService).changePassword(USER_ID, NEW_PASSWORD);
        verify(passwordRecoveryCodeDao).deleteCode(USER_ID);
        verify(emailService).confirmNewPassword(user);
    }

    @Test
    public void testChangePasswordInvalidCode() {
        Mockito.when(passwordRecoveryCodeDao.getCodeByUUID(CODE)).thenReturn(Optional.empty());

        Assert.assertThrows(InvalidRecoveryCodeException.class,
                () -> passwordRecoveryCodeService.changePassword(CODE, NEW_PASSWORD));

        verify(userService, never()).changePassword(Mockito.anyLong(), Mockito.anyString());
        verify(passwordRecoveryCodeDao, never()).deleteCode(Mockito.anyLong());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testChangePasswordUserNotFound() {
        Mockito.when(passwordRecoveryCodeDao.getCodeByUUID(CODE))
                .thenReturn(Optional.of(passwordRecoveryCode));
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(UserNotFoundException.class,
                () -> passwordRecoveryCodeService.changePassword(CODE, NEW_PASSWORD));

        verify(userService, never()).changePassword(Mockito.anyLong(), Mockito.anyString());
        verify(passwordRecoveryCodeDao, never()).deleteCode(Mockito.anyLong());
        verifyZeroInteractions(emailService);
    }

    @Test
    public void testDeleteCode() {
        passwordRecoveryCodeService.deleteCode(USER_ID);

        verify(passwordRecoveryCodeDao).deleteCode(USER_ID);
    }
}
