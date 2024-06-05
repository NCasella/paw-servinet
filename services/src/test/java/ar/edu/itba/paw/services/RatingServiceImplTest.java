package ar.edu.itba.paw.services;


//import ar.edu.itba.paw.model.Rating;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.MockitoJUnitRunner;
//
//
//import java.time.LocalDate;
//import java.util.Optional;
//
//@RunWith(MockitoJUnitRunner.class)
//public class RatingServiceImplTest {
//    private static final String COMMENT = "This is a comment";
//    private static final int RATING5 = 5;
//    private static final long USERID = 1;
//    private static final long SERVICEID = 1;
//
//    @InjectMocks
//    private  RatingServiceImpl ratingService;
//
//    @Mock
//    private RatingDao ratingDao;
//
//
//    @Test
//    public void testHasAlreadyRated(){
//        Mockito.when(ratingDao.hasAlreadyRated(USERID, SERVICEID)).thenReturn(Optional.of(new Rating(1,USERID, SERVICEID, RATING5, COMMENT, LocalDate.now())));
//
//        Rating rating= ratingService.hasAlreadyRated(USERID, SERVICEID);
//
//        Assert.assertNotNull(rating);
//        Assert.assertEquals(rating.getRating(), RATING5);
//        Assert.assertEquals(rating.getComment(), COMMENT);
//        Assert.assertEquals(rating.getUserid(), USERID);
//        Assert.assertEquals(rating.getServiceid(), SERVICEID);
//
//    }
//
//    @Test
//    public void testHasNotAlreadyRated(){
//        Mockito.when(ratingDao.hasAlreadyRated(USERID, SERVICEID)).thenReturn(Optional.empty());
//
//        Rating rating = ratingService.hasAlreadyRated(USERID, SERVICEID);
//
//        Assert.assertNull(rating);
//
//    }
//
//}
