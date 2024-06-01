package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

// Me permite testear mi UserService
@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final long USER_ID = 1;
    private static final String USERNAME = "My user";
    private static final String PASSWORD = "My password";
    private static final String NAME = "My name";
    private static final String SURNAME = "My surname";
    private static final String EMAIL = "My email";
    private static final String TELEPHONE = "My telephone";
    private static final String LOCALE = "en";

    // Los Mocks hacen el constructor automaticamente (no necesito hacer un Before setup)
    @InjectMocks
    private UserServiceImpl userService;


    @Mock
    private UserDao userDao;

    @Test
    public void testFindByIdNonExisting() {
        // 1. Precondiciones
        Mockito.when(userDao.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        // 2. Ejecuta la class under test (una sola)
        Optional<User> maybeUser = userService.findById(1);

        // 3. Postcondiciones - assertions (todas las que sean necesarias)
        Assert.assertNotNull(maybeUser);
        Assert.assertFalse(maybeUser.isPresent());
    }

    @Test
    public void testFindByIdExistingUser() {
        // 1. Precondiciones
        Mockito.when(userDao.findById(Mockito.eq(USER_ID))).thenReturn(Optional.of(new User(USERNAME,PASSWORD, NAME, SURNAME, EMAIL, TELEPHONE, false,LOCALE)));

        // 2. Ejecuta la class under test (una sola)
        Optional<User> maybeUser = userService.findById(USER_ID);

        // 3. Postcondiciones - assertions (todas las que sean necesarias)
        Assert.assertNotNull(maybeUser);
//        Assert.assertFalse(maybeUser.isPresent());


    }
}
