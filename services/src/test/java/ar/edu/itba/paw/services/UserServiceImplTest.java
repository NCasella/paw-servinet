package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.AvailableLanguages;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserVerificationCode;
import ar.edu.itba.paw.model.exceptions.InvalidOperationException;
import ar.edu.itba.paw.model.exceptions.InvalidPasswordModificationException;
import ar.edu.itba.paw.model.exceptions.InvalidUsernameException;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final long USER_ID = 1L;
    private static final long ANOTHER_USER_ID = 2L;
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String NAME = "Test";
    private static final String SURNAME = "User";
    private static final String EMAIL = "test@test.com";
    private static final String TELEPHONE = "+1 123456789";
    private static final String LOCALE = "en";

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserDao userDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserVerificationService userVerificationService;

    private User user;
    private User anotherUser;

    @Before
    public void setup() throws Exception {
        user = createUserWithId(USER_ID, USERNAME, PASSWORD, NAME, SURNAME, EMAIL, TELEPHONE, false, LOCALE);
        anotherUser = createUserWithId(ANOTHER_USER_ID, "anotheruser", PASSWORD, "Another", "User",
                "another@test.com", TELEPHONE, false, LOCALE);
    }

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private User createUserWithId(long id, String username, String password, String name,
                                   String surname, String email, String telephone,
                                   boolean isProvider, String locale) throws Exception {
        User user = new User(username, password, name, surname, email, telephone, isProvider, locale);
        setFieldValue(user, "userId", id);
        return user;
    }

    private void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    @Test
    public void testFindByIdNonExisting() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(USER_ID);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void testFindByIdExisting() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findById(USER_ID);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(USERNAME, result.get().getUsername());
        Assert.assertEquals(USER_ID, result.get().getUserId());
    }

    @Test
    public void testFindByEmailFound() {
        Mockito.when(userDao.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail(EMAIL);

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(EMAIL, result.get().getEmail());
    }

    @Test
    public void testFindByEmailNotFound() {
        Mockito.when(userDao.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("nonexistent@test.com");

        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void testFindByUsernameFound() {
        Mockito.when(userDao.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUsername(USERNAME);

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(USERNAME, result.get().getUsername());
    }

    @Test
    public void testFindByUsernameNotFound() {
        Mockito.when(userDao.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername("nonexistent");

        Assert.assertFalse(result.isPresent());
    }

    @Test
    public void testGetAllUsers() throws Exception {
        User user2 = createUserWithId(2L, "user2", PASSWORD, "Name2", "Surname2", "email2@test.com", TELEPHONE, false, LOCALE);
        List<User> users = List.of(user, user2);
        Mockito.when(userDao.getUsers(0)).thenReturn(users);

        List<User> result = userService.getAllUsers(0);

        Assert.assertEquals(2, result.size());
    }

    @Test
    public void testGetUserCount() {
        Mockito.when(userDao.getUserCount()).thenReturn(5);

        int result = userService.getUserCount();

        Assert.assertEquals(5, result);
    }

    @Test
    public void testIsProviderTrue() throws Exception {
        User providerUser = createUserWithId(USER_ID, USERNAME, PASSWORD, NAME, SURNAME, EMAIL, TELEPHONE, true, LOCALE);
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(providerUser));

        boolean result = userService.isProvider(USER_ID);

        Assert.assertTrue(result);
    }

    @Test
    public void testIsProviderFalse() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));

        boolean result = userService.isProvider(USER_ID);

        Assert.assertFalse(result);
    }

    @Test
    public void testIsProviderUserNotFound() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(UserNotFoundException.class, () -> userService.isProvider(USER_ID));
    }

    @Test
    public void testIsVerifiedTrue() throws Exception {
        User verifiedUser = createUserWithId(USER_ID, USERNAME, PASSWORD, NAME, SURNAME, EMAIL, TELEPHONE, false, LOCALE);
        verifiedUser.setIsVerified(true);
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(verifiedUser));

        boolean result = userService.isVerified(USER_ID);

        Assert.assertTrue(result);
    }

    @Test
    public void testIsVerifiedFalse() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));

        boolean result = userService.isVerified(USER_ID);

        Assert.assertFalse(result);
    }

    @Test
    public void testIsVerifiedUserNotFound() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(UserNotFoundException.class, () -> userService.isVerified(USER_ID));
    }

    @Test
    public void testGetUserLocale() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));

        String result = userService.getUserLocale(USER_ID);

        Assert.assertEquals(LOCALE, result);
    }

    @Test
    public void testGetUserLocaleUserNotFound() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(UserNotFoundException.class, () -> userService.getUserLocale(USER_ID));
    }

    @Test
    public void testChangeUsernameSuccess() {
        String newUsername = "newusername";
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(userDao.findByUsername(newUsername)).thenReturn(Optional.empty());

        userService.changeUsername(USER_ID, newUsername);

        verify(userDao).changeUsername(USER_ID, newUsername);
    }

    @Test
    public void testChangeUsernameUserNotFound() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(UserNotFoundException.class,
                () -> userService.changeUsername(USER_ID, "newusername"));

        verify(userDao, never()).changeUsername(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    public void testChangeUsernameAlreadyTaken() {
        String takenUsername = "takenusername";
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(userDao.findByUsername(takenUsername)).thenReturn(Optional.of(anotherUser));

        Assert.assertThrows(InvalidUsernameException.class,
                () -> userService.changeUsername(USER_ID, takenUsername));

        verify(userDao, never()).changeUsername(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    public void testChangeTelephoneSuccess() {
        String newTelephone = "+1 987654321";
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.changeTelephone(USER_ID, newTelephone);

        verify(userDao).changeTelephone(USER_ID, newTelephone);
    }

    @Test
    public void testChangeTelephoneUserNotFound() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(UserNotFoundException.class,
                () -> userService.changeTelephone(USER_ID, "+1 987654321"));

        verify(userDao, never()).changeTelephone(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    public void testChangePasswordSuccess() {
        String newPassword = "newpassword";
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.encode(newPassword)).thenReturn(ENCODED_PASSWORD);

        userService.changePassword(USER_ID, newPassword);

        verify(passwordEncoder).encode(newPassword);
        verify(userDao).changePassword(USER_ID, ENCODED_PASSWORD);
    }

    @Test
    public void testChangePasswordUserNotFound() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(UserNotFoundException.class,
                () -> userService.changePassword(USER_ID, "newpassword"));

        verify(userDao, never()).changePassword(Mockito.anyLong(), Mockito.anyString());
        verify(passwordEncoder, never()).encode(Mockito.anyString());
    }

    @Test
    public void testChangePasswordWithOldPasswordSuccess() throws Exception {
        String oldPassword = "oldpassword";
        String newPassword = "newpassword";
        String newEncodedPassword = "newEncodedPassword";

        User userWithEncodedPassword = createUserWithId(USER_ID, USERNAME, ENCODED_PASSWORD, NAME, SURNAME, EMAIL, TELEPHONE, false, LOCALE);
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(userWithEncodedPassword));
        Mockito.when(passwordEncoder.matches(oldPassword, ENCODED_PASSWORD)).thenReturn(true);
        Mockito.when(passwordEncoder.encode(newPassword)).thenReturn(newEncodedPassword);

        userService.changePassword(USER_ID, oldPassword, newPassword);

        verify(passwordEncoder).matches(oldPassword, ENCODED_PASSWORD);
        verify(passwordEncoder).encode(newPassword);
        verify(userDao).changePassword(USER_ID, newEncodedPassword);
    }

    @Test
    public void testChangePasswordWithWrongOldPassword() throws Exception {
        String wrongPassword = "wrongpassword";
        User userWithEncodedPassword = createUserWithId(USER_ID, USERNAME, ENCODED_PASSWORD, NAME, SURNAME, EMAIL, TELEPHONE, false, LOCALE);
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(userWithEncodedPassword));
        Mockito.when(passwordEncoder.matches(wrongPassword, ENCODED_PASSWORD)).thenReturn(false);

        Assert.assertThrows(InvalidPasswordModificationException.class,
                () -> userService.changePassword(USER_ID, wrongPassword, "newpassword"));

        verify(userDao, never()).changePassword(Mockito.anyLong(), Mockito.anyString());
        verify(passwordEncoder, never()).encode(Mockito.anyString());
    }

    @Test
    public void testChangePasswordWithOldPasswordUserNotFound() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(UserNotFoundException.class,
                () -> userService.changePassword(USER_ID, "oldpassword", "newpassword"));

        verify(userDao, never()).changePassword(Mockito.anyLong(), Mockito.anyString());
        verify(passwordEncoder, never()).matches(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testChangeLocaleSuccess() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.changeLocale(USER_ID, AvailableLanguages.ENGLISH);

        verify(userDao).changeLocale(USER_ID, AvailableLanguages.ENGLISH);
    }

    @Test
    public void testChangeLocaleUserNotFound() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(UserNotFoundException.class,
                () -> userService.changeLocale(USER_ID, AvailableLanguages.ENGLISH));

        verify(userDao, never()).changeLocale(Mockito.anyLong(), Mockito.any());
    }

    @Test
    public void testVerifyUserSuccess() {
        String code = "123456";
        UserVerificationCode verificationCode = new UserVerificationCode(user, code, LocalDateTime.now().plusDays(1));
        Mockito.when(userVerificationService.verifyUser(USER_ID, code)).thenReturn(true);
        Mockito.when(userVerificationService.getUserVerificationCodeByUserId(USER_ID))
                .thenReturn(Optional.of(verificationCode));

        boolean result = userService.verifyUser(USER_ID, code);

        Assert.assertTrue(result);
        verify(userDao).verifyUser(USER_ID);
        verify(userVerificationService).deleteCode(USER_ID);
    }

    @Test
    public void testVerifyUserInvalidCode() {
        String wrongCode = "wrongcode";
        Mockito.when(userVerificationService.verifyUser(USER_ID, wrongCode)).thenReturn(false);

        boolean result = userService.verifyUser(USER_ID, wrongCode);

        Assert.assertFalse(result);
        verify(userDao, never()).verifyUser(Mockito.anyLong());
        verify(userVerificationService, never()).deleteCode(Mockito.anyLong());
    }

    @Test
    public void testVerifyUserNoCode() {
        String code = "123456";
        Mockito.when(userVerificationService.verifyUser(USER_ID, code)).thenReturn(true);
        Mockito.when(userVerificationService.getUserVerificationCodeByUserId(USER_ID))
                .thenReturn(Optional.empty());

        boolean result = userService.verifyUser(USER_ID, code);

        Assert.assertFalse(result);
        verify(userDao, never()).verifyUser(Mockito.anyLong());
    }

    @Test
    public void testIsUserProvideeTrue() {
        Mockito.when(userDao.isUserProvidee(1L, 2L)).thenReturn(true);

        boolean result = userService.isUserProvidee(1L, 2L);

        Assert.assertTrue(result);
    }

    @Test
    public void testIsUserProvideeFalse() {
        Mockito.when(userDao.isUserProvidee(1L, 2L)).thenReturn(false);

        boolean result = userService.isUserProvidee(1L, 2L);

        Assert.assertFalse(result);
    }

    @Test
    public void testChangeProfilePicSuccess() {
        long newPicId = 100L;
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.changeProfilePic(USER_ID, newPicId);

        verify(userDao).changeProfilePicId(USER_ID, newPicId);
    }

    @Test
    public void testChangeProfilePicUserNotFound() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(UserNotFoundException.class,
                () -> userService.changeProfilePic(USER_ID, 100L));

        verify(userDao, never()).changeProfilePicId(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void testCreateSuccess() throws Exception {
        User createdUser = createUserWithId(USER_ID, USERNAME, ENCODED_PASSWORD, NAME, SURNAME, EMAIL, TELEPHONE, false, LOCALE);
        Mockito.when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        Mockito.when(userDao.create(Mockito.eq(USERNAME), Mockito.eq(NAME), Mockito.eq(SURNAME),
                Mockito.eq(ENCODED_PASSWORD), Mockito.eq(EMAIL), Mockito.eq(TELEPHONE),
                Mockito.eq(false), Mockito.anyString())).thenReturn(createdUser);

        User result = userService.create(USERNAME, NAME, SURNAME, PASSWORD, EMAIL, TELEPHONE);

        Assert.assertNotNull(result);
        Assert.assertEquals(ENCODED_PASSWORD, result.getPassword());
        verify(passwordEncoder).encode(PASSWORD);
        verify(userVerificationService).sendVerificationCode(createdUser);
    }

    @Test
    public void testChangeEmailSuccess() {
        String newEmail = "newemail@test.com";
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(userDao.findByEmail(newEmail)).thenReturn(Optional.empty());

        userService.changeEmail(USER_ID, newEmail);

        verify(userDao).changeEmail(USER_ID, newEmail);
    }

    @Test
    public void testChangeEmailUserNotFound() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.empty());

        Assert.assertThrows(UserNotFoundException.class,
                () -> userService.changeEmail(USER_ID, "newemail@test.com"));

        verify(userDao, never()).changeEmail(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    public void testChangeEmailAlreadyTaken() {
        String takenEmail = "taken@test.com";
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(userDao.findByEmail(takenEmail)).thenReturn(Optional.of(anotherUser));

        Assert.assertThrows(InvalidOperationException.class,
                () -> userService.changeEmail(USER_ID, takenEmail));

        verify(userDao, never()).changeEmail(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    public void testChangeEmailSameEmail() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(userDao.findByEmail(EMAIL)).thenReturn(Optional.empty());

        userService.changeEmail(USER_ID, EMAIL);

        verify(userDao, never()).changeEmail(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    public void testChangeUserInfoWithProfilePic() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.changeUserInfo(USER_ID, "", "", "", null, 100L);

        verify(userDao).changeProfilePicId(USER_ID, 100L);
        verify(userDao, never()).changeUsername(Mockito.anyLong(), Mockito.anyString());
        verify(userDao, never()).changeEmail(Mockito.anyLong(), Mockito.anyString());
        verify(userDao, never()).changeTelephone(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    public void testChangeUserInfoWithUsername() {
        String newUsername = "newusername";
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));
        Mockito.when(userDao.findByUsername(newUsername)).thenReturn(Optional.empty());

        userService.changeUserInfo(USER_ID, newUsername, "", "", null, null);

        verify(userDao).changeUsername(USER_ID, newUsername);
    }

    @Test
    public void testChangeUserInfoWithLocale() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.changeUserInfo(USER_ID, "", "", "", AvailableLanguages.SPANISH, null);

        verify(userDao).changeLocale(USER_ID, AvailableLanguages.SPANISH);
    }

    @Test
    public void testChangeUserInfoEmptyFields() {
        userService.changeUserInfo(USER_ID, "", "", "", null, null);

        verify(userDao, never()).changeUsername(Mockito.anyLong(), Mockito.anyString());
        verify(userDao, never()).changeEmail(Mockito.anyLong(), Mockito.anyString());
        verify(userDao, never()).changeTelephone(Mockito.anyLong(), Mockito.anyString());
        verify(userDao, never()).changeLocale(Mockito.anyLong(), Mockito.any());
        verify(userDao, never()).changeProfilePicId(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void testMakeProviderSuccess() {
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.makeProvider(user);

        verify(userDao).changeUserType(USER_ID);
    }

    @Test
    public void testMakeProviderAlreadyProvider() throws Exception {
        User providerUser = createUserWithId(USER_ID, USERNAME, PASSWORD, NAME, SURNAME, EMAIL, TELEPHONE, true, LOCALE);
        Mockito.when(userDao.findById(USER_ID)).thenReturn(Optional.of(providerUser));

        userService.makeProvider(providerUser);

        verify(userDao, never()).changeUserType(Mockito.anyLong());
    }

    @Test
    public void testRevokeProviderRole() {
        userService.revokeProviderRole(user);

        Assert.assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
