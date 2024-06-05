package ar.edu.itba.paw.persistance;

// import ar.edu.itba.paw.model.Appointment;
// import ar.edu.itba.paw.model.Service;
// import ar.edu.itba.paw.model.User;
// import ar.edu.itba.paw.persistance.config.TestConfig;
// import org.junit.Assert;
// import org.junit.Before;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.test.annotation.Rollback;
// import org.springframework.test.context.ContextConfiguration;
// import org.springframework.test.context.jdbc.Sql;
// import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
// import org.springframework.test.jdbc.JdbcTestUtils;
// import org.springframework.transaction.annotation.Transactional;
//
//
// import javax.persistence.EntityManager;
// import javax.persistence.PersistenceContext;
// import javax.sql.DataSource;
// import java.sql.Timestamp;
// import java.time.LocalDateTime;
//
// @Transactional
// @Rollback
// @Sql("classpath:sql/schema.sql")
// @RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration(classes = TestConfig.class)
// public class AppointmentDaoJpaTest {
//
//     @PersistenceContext
//     private EntityManager em;
//     @Autowired
//     private AppointmentDaoJpa appointmentDao;
//
//     @Autowired
//     private DataSource ds;
//
//
//     private JdbcTemplate jdbcTemplate;
//     private final long APPOINTMENT_ID = 1;
//     private final long BUS_ID=1;
//     private final String BUSINESS_NAME="business name";
//     private final long APPOINTMENT_ID2=2;
//     private final LocalDateTime STARTDATE = LocalDateTime.now();
//     private final LocalDateTime ENDDATE = STARTDATE.plusHours(2);
//     private final long SERVICEID = 1;
//     private final long USERID = 1;
//     private final String LOCATION = "calle falsa 123";
//     private final String DESCRIPTION = "generic description";
//     private User USER;
//     private Service SERVICE;
//
//     @Before
//     public void setup() {
//         this.jdbcTemplate = new JdbcTemplate(ds);
//         jdbcTemplate.execute("insert into users(userid,username, name, surname, email, telephone, password, isprovider) values (1,'solro', 'sol', 'rodri', 'solrodriguezgiana@gmail.com', '113452343', 'solro', true);");
//         jdbcTemplate.execute(String.format("INSERT INTO business (businessid, businessname, userid, businessTelephone, businessEmail, businessLocation) VALUES (%d,'%s', 1, '%s','%s','%s')",BUS_ID,BUSINESS_NAME, "113452343", "solrodriguezgiana@gmail.com", LOCATION));
//         jdbcTemplate.execute("INSERT INTO services VALUES (1,1,'Peluqueria Ramon','Veni, peinate y divertite!',false,'calle falsa 123','Belleza',60,'Por hora',5000,true,null);");
//         USER=em.find(User.class,USERID);
//         SERVICE=em.find(Service.class,SERVICEID);
//     }
//
//     @Test
//     public void testCreate() {
//         // 1. Precondiciones (una sola)
//
//         // 2. Ejecuta la class under test (una sola)
//         Appointment appointment = appointmentDao.create(SERVICE, USER, STARTDATE, ENDDATE, LOCATION, DESCRIPTION); em.flush();
//         // 3. Postcondiciones - assertions (todas las que sean necesarias)
//         Assert.assertNotNull(appointment);
//         Assert.assertFalse(appointment.getConfirmed());
//         Assert.assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "appointments"));
//     }
//
//     @Test
//     public void testCreate2() {
//         // 1. Precondiciones (una sola)
//
//         // 2. Ejecuta la class under test (una sola)
//         Appointment a1 = appointmentDao.create(SERVICE, USER, STARTDATE, ENDDATE, LOCATION, DESCRIPTION);
//         em.flush();
//         // 3. Postcondiciones - assertions (todas las que sean necesarias)
//         Assert.assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, "appointments"));
//     }
//
//     @Test
//     public void testFindById(){
//         jdbcTemplate.execute(String.format("insert into appointments(appointmentid, serviceid, userid, startdate, enddate, location, confirmed) values (%d, %d, %d, '%s', '%s', '%s', false);", APPOINTMENT_ID,SERVICEID, USERID, Timestamp.valueOf(STARTDATE), Timestamp.valueOf(ENDDATE), LOCATION));
//
//         Appointment appointment = appointmentDao.findById(APPOINTMENT_ID).get();
//         em.flush();
//         Assert.assertEquals(SERVICEID,appointment.getServiceid());
//         Assert.assertTrue(ENDDATE.isAfter(appointment.getStartDate()));
//     }
//
//     @Test
//     public void testConfirmAppointment(){
//         jdbcTemplate.execute(String.format("insert into appointments(appointmentid, serviceid, userid, startdate, enddate, location, confirmed) values (%d, %d, %d, '%s', '%s', '%s', false);", APPOINTMENT_ID,SERVICEID, USERID, Timestamp.valueOf(STARTDATE), Timestamp.valueOf(ENDDATE), LOCATION));
//         jdbcTemplate.execute(String.format("insert into appointments(appointmentid, serviceid, userid, startdate, enddate, location, confirmed) values (%d, %d, %d, '%s', '%s', '%s', false);", APPOINTMENT_ID2,SERVICEID, USERID, Timestamp.valueOf(STARTDATE.plusHours(1)), Timestamp.valueOf(ENDDATE.plusHours(1)), LOCATION));
//         //Appointment toConfirmAppointment = appointmentDao.create(SERVICEID, USERID, STARTDATE, ENDDATE, LOCATION);
//
//         appointmentDao.confirmAppointment(1);
//         em.flush();
//         Assert.assertEquals(JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "appointments", "confirmed = true"), 1);
//         Assert.assertEquals(JdbcTestUtils.countRowsInTable(jdbcTemplate, "appointments"), 2);
//     }
//
//     @Test
//     public void testCancelAppointment(){
//         jdbcTemplate.execute(String.format("insert into appointments(appointmentid, serviceid, userid, startdate, enddate, location, confirmed) values (1, %d, %d, '%s', '%s', '%s', false);", SERVICEID, USERID, Timestamp.valueOf(STARTDATE), Timestamp.valueOf(ENDDATE), LOCATION));
//         jdbcTemplate.execute(String.format("insert into appointments(appointmentid, serviceid, userid, startdate, enddate, location, confirmed) values (2, %d, %d, '%s', '%s', '%s', false);", SERVICEID, USERID, Timestamp.valueOf(STARTDATE.plusHours(1)), Timestamp.valueOf(ENDDATE.plusHours(1)), LOCATION));
//
//         //Appointment appointment = appointmentDao.create(SERVICEID, USERID, STARTDATE, ENDDATE, LOCATION);
//         //Appointment anotherAppointment = appointmentDao.create(SERVICEID, USERID, STARTDATE.plusHours(1), ENDDATE, LOCATION);
//         em.flush();
//         appointmentDao.cancelAppointment(APPOINTMENT_ID);
//         Assert.assertFalse( appointmentDao.findById(APPOINTMENT_ID).isPresent());
//     }
// }
//