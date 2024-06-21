 package ar.edu.itba.paw.persistance;


 import ar.edu.itba.paw.model.Business;
 import ar.edu.itba.paw.model.User;
 import ar.edu.itba.paw.persistance.config.TestConfig;
 import org.junit.Assert;
 import org.junit.Before;
 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.jdbc.core.JdbcTemplate;
 import org.springframework.test.annotation.Rollback;
 import org.springframework.test.context.ContextConfiguration;
 import org.springframework.test.context.jdbc.Sql;
 import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
 import org.springframework.test.jdbc.JdbcTestUtils;
 import org.springframework.transaction.annotation.Transactional;

 import javax.persistence.EntityManager;
 import javax.persistence.PersistenceContext;
 import javax.sql.DataSource;

 @Transactional
 @Rollback
 @Sql("classpath:sql/schema.sql")
 @RunWith(SpringJUnit4ClassRunner.class)
 @ContextConfiguration(classes = TestConfig.class)
 public class BusinessDaoJpaTest {

     private static final long BUS_ID =1;
     private static final long BUS_ID_SECONDARY= 2;
     private static final String BUSINESS_NAME = "Business";
     private static final String BUSINESS_NAME_SECONDARY = "Business2";
     private static final long USER_ID = 1;
     private static final String TELEPHONE = "123456789";
     private static final String EMAIL = "mail@mail.com";
     private static final String LOCATION = "Location";

     @Autowired
     private BusinessDaoJpa businessDaoJpa;
     @Autowired
     private DataSource ds;
     @PersistenceContext
     private EntityManager em;
     private JdbcTemplate jdbcTemplate;

     @Before
     public void setup() {
         this.jdbcTemplate = new JdbcTemplate(ds);
         jdbcTemplate.execute("INSERT INTO users (userid, username, password, name, surname, email, telephone,isprovider) VALUES (1, 'username', 'password', 'name', 'surname', 'email', 'telephone',true)");
     }

     @Test
     public void testCreateBusiness() {
         // 1. Precondiciones

         // 2. Ejecuta la class under test (una sola)
         Business business= businessDaoJpa.createBusiness(BUSINESS_NAME, USER_ID, TELEPHONE, EMAIL, LOCATION);
         em.flush();
         // 3. Postcondiciones - assertions (todas las que sean necesarias)
         Assert.assertNotNull(business);
         Assert.assertEquals(BUSINESS_NAME, business.getBusinessName());
         Assert.assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, "business"));
     }

     @Test
     public void testDeleteLastBusiness(){
         // 1. Precondiciones
         jdbcTemplate.execute(String.format("INSERT INTO business (businessid, businessname, userid, businessTelephone, businessEmail, businessLocation) VALUES (%d,'%s', 1, '%s','%s','%s')",BUS_ID,BUSINESS_NAME, TELEPHONE, EMAIL, LOCATION));
         jdbcTemplate.execute(String.format("INSERT INTO business (businessid, businessname, userid, businessTelephone, businessEmail, businessLocation) VALUES (%d,'%s', 1, '%s','%s','%s')",BUS_ID+1,BUSINESS_NAME+"2", TELEPHONE, EMAIL, LOCATION));

         boolean isStillProvider = businessDaoJpa.deleteBusiness(BUS_ID);
         em.flush();
         Assert.assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, "business"));
         Assert.assertTrue(isStillProvider);
     }

     @Test
     public void testDeleteBusinessWithMoreLeft(){
         jdbcTemplate.execute(String.format("INSERT INTO business (businessid, businessname, userid, businessTelephone, businessEmail, businessLocation) VALUES (%d,'%s', 1, '%s','%s','%s')",BUS_ID,BUSINESS_NAME, TELEPHONE, EMAIL, LOCATION));
         jdbcTemplate.execute(String.format("INSERT INTO business (businessid, businessname, userid, businessTelephone, businessEmail, businessLocation) VALUES (%d,'%s', 1, '%s','%s','%s')",BUS_ID_SECONDARY,BUSINESS_NAME_SECONDARY, TELEPHONE, EMAIL, LOCATION));

         boolean isStillProvider = businessDaoJpa.deleteBusiness(BUS_ID);
         em.flush();
         Assert.assertEquals(1,JdbcTestUtils.countRowsInTable(jdbcTemplate, "business"));
         Assert.assertTrue(isStillProvider);
     }

     @Test
     public void testDeleteBusinessWithNoBusiness(){
         jdbcTemplate.execute(String.format("INSERT INTO business (businessid, businessname, userid, businessTelephone, businessEmail, businessLocation) VALUES (%d,'%s', 1, '%s','%s','%s')",BUS_ID,BUSINESS_NAME, TELEPHONE, EMAIL, LOCATION));
         boolean isStillProvider=businessDaoJpa.deleteBusiness(BUS_ID);
         em.flush();
         Assert.assertEquals(0,JdbcTestUtils.countRowsInTable(jdbcTemplate,"business"));
         Assert.assertTrue(isStillProvider);

     }
 }
