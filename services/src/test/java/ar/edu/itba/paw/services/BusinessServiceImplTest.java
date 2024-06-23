package ar.edu.itba.paw.services;

 import ar.edu.itba.paw.model.Business;
 import ar.edu.itba.paw.model.User;
 import ar.edu.itba.paw.model.exceptions.BusinessNotFoundException;
 import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
 import org.junit.Assert;
 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
 import org.mockito.Mockito;
 import org.mockito.junit.MockitoJUnitRunner;

 import java.util.Optional;

 @RunWith(MockitoJUnitRunner.class)
 public class BusinessServiceImplTest {
     private static final long ID =1;
     private static final String USERNAME = "username";
     private static final String PASSWORD = "password";
     private static final String NAME = "name";
     private static final String SURNAME = "surname";
     private static final String BUSINESS_NAME = "Business";
     private static final long USER_ID = 1;
     private static final String TELEPHONE = "123456789";
     private static final String LOCALE = "en";
     private static final String EMAIL = "mail@mail.com";
     private static final String LOCATION = "location";
     private static final User user =new User(USERNAME, PASSWORD, NAME,SURNAME , EMAIL, TELEPHONE, false,LOCALE);
     private static final Business business=new Business(BUSINESS_NAME,user,TELEPHONE,EMAIL,LOCATION);

     @InjectMocks
     private BusinessServiceImpl businessService;
     @Mock
     private BusinessDao businessDao;
     @Mock
     private ServiceService serviceService;
     @Mock
     private EmailService emailService;
     @Mock
     private UserService userService;

     @Test
     public void testCreate(){
         Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(user));
         Mockito.when(businessDao.createBusiness(BUSINESS_NAME,USER_ID,TELEPHONE,EMAIL,LOCATION)).thenReturn(business);
         Business biz= businessService.createBusiness(BUSINESS_NAME, USER_ID, TELEPHONE, EMAIL, LOCATION);
         Assert.assertEquals(BUSINESS_NAME,biz.getBusinessName());
         Assert.assertEquals(EMAIL,biz.getEmail());
         Assert.assertEquals(LOCATION,biz.getLocation());
         Assert.assertEquals(TELEPHONE,biz.getTelephone());
     }
     @Test(expected = BusinessNotFoundException.class)
     public void testDeleteNonExistentBusiness(){
         Mockito.when(businessService.findById(Mockito.anyLong())).thenReturn(Optional.empty());
         businessService.deleteBusiness(ID);
         Assert.fail();
     }

     @Test(expected = UserNotFoundException.class)
     public void testCreateWithoutValidUser(){
         Mockito.when(userService.findById(Mockito.anyLong())).thenReturn(Optional.empty());
         Business biz=businessService.createBusiness(BUSINESS_NAME,USER_ID,TELEPHONE,EMAIL,LOCATION);
         Assert.fail();
     }

 }

