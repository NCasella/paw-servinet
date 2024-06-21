package ar.edu.itba.paw.services;


import ar.edu.itba.paw.model.Rating;
import ar.edu.itba.paw.model.Service;
import ar.edu.itba.paw.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


import java.time.LocalDate;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class RatingServiceImplTest {
    private static final String COMMENT = "This is a comment";
    private static final int RATING5 = 5;
    private static final long USERID = 1;
    private static final long SERVICEID = 1;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";
    private static final String SURNAME = "surname";
    private static final String TELEPHONE = "123456789";
    private static final String LOCALE = "en";
    private static final String EMAIL = "mail@mail.com";

    private static final User user= new User(USERNAME, PASSWORD, NAME,SURNAME , EMAIL, TELEPHONE, false,LOCALE);
    private static final Service service=Mockito.mock(Service.class);

    @InjectMocks
    private  RatingServiceImpl ratingService;

    @Mock
    private RatingDao ratingDao;


    @Test
    public void testHasAlreadyRated(){
        Mockito.when(ratingDao.hasAlreadyRated(USERID, SERVICEID)).thenReturn(Optional.of(new Rating(service, user, RATING5, COMMENT, LocalDate.now())));
        Rating rating= ratingService.hasAlreadyRated(USERID, SERVICEID);

        Assert.assertNotNull(rating);
        Assert.assertEquals(rating.getRating(), RATING5);
        Assert.assertEquals(rating.getComment(), COMMENT);
        Assert.assertEquals(rating.getUser(), user);
        Assert.assertEquals(rating.getService(), service);
    }

    @Test
    public void testHasNotAlreadyRated(){
        Mockito.when(ratingDao.hasAlreadyRated(USERID, SERVICEID)).thenReturn(Optional.empty());
        Rating rating = ratingService.hasAlreadyRated(USERID, SERVICEID);
        Assert.assertNull(rating);

    }

}
