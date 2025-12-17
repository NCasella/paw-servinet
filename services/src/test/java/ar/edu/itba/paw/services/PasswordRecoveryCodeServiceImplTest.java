package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.PasswordRecoveryCode;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class PasswordRecoveryCodeServiceImplTest {
    private static final UUID code=UUID.randomUUID();
    private static final User userMock=Mockito.mock(User.class);
    private static final String EMAIL = "mail@mail.com";
    private static final String NEW_PASSWORD = "newpassword";


    @InjectMocks
    private PasswordRecoveryCodeServiceImpl passwordRecoveryCodeService;

    @Mock
    private PasswordRecoveryCodeDao passwordRecoveryCodeDao;

    @Mock
    private UserService userService;

    private static final PasswordRecoveryCode passwordRecoveryCode=Mockito.mock(PasswordRecoveryCode.class);

    @Test(expected = UserNotFoundException.class)
    public void testCodeForNonExistentUser(){
        Mockito.when(passwordRecoveryCodeDao.getCodeByUUID(code)).thenReturn(Optional.of(passwordRecoveryCode));
        Mockito.when(userService.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        passwordRecoveryCodeService.changePassword(code,NEW_PASSWORD);
        Assert.fail();
    }
    @Test
    public void testCodeValidation(){
        Mockito.when(userMock.getEmail()).thenReturn(EMAIL);
        Mockito.when(passwordRecoveryCodeDao.getCodeByUUID(code)).thenReturn(Optional.of(new PasswordRecoveryCode(userMock,code, LocalDateTime.now().plusHours(1))));
        boolean validCode= passwordRecoveryCodeService.validateCode(userMock.getEmail(), code);
        Assert.assertTrue(validCode);
    }
}
