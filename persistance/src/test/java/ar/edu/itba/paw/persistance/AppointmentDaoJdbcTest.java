package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Appointment;
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

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Transactional
@Rollback
@Sql("classpath:sql/schema.sql")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class AppointmentDaoJdbcTest {

    @Autowired
    private AppointmentDaoJpa appointmentDao;

    @Autowired
    private DataSource ds;

    private JdbcTemplate jdbcTemplate;
    private final long APPOINTMENT_ID = 1;
    private final long APPOINTMENT_ID2=2;
    private final LocalDateTime STARTDATE = LocalDateTime.now();
    private final LocalDateTime ENDDATE = STARTDATE.plusHours(2);
    private final long SERVICEID = 1;
    private final long USERID = 1;
    private final String LOCATION = "calle falsa 123";


    @Before
    public void setup() {
        this.jdbcTemplate = new JdbcTemplate(ds);
        jdbcTemplate.execute("insert into users(userid,username, name, surname, email, telephone, password, isprovider) values (1,'solro', 'sol', 'rodri', 'solrodriguezgiana@gmail.com', '113452343', 'solro', true);");
        jdbcTemplate.execute("INSERT INTO services VALUES (1,null,'Peluqueria Ramon','Veni, peinate y divertite!',false,'calle falsa 123','Belleza',60,'Por hora',5000,true,null);");
    }

    @Test
    public void testCreate() {
        // 1. Precondiciones (una sola)

        // 2. Ejecuta la class under test (una sola)
        Appointment appointment = appointmentDao.create(SERVICEID, USERID, STARTDATE, ENDDATE, LOCATION);

        // 3. Postcondiciones - assertions (todas las que sean necesarias)
        Assert.assertNotNull(appointment);
        Assert.assertFalse(appointment.getConfirmed());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "appointments"));
    }

    @Test
    public void testCreate2() {
        // 1. Precondiciones (una sola)

        // 2. Ejecuta la class under test (una sola)
        Appointment a1 = appointmentDao.create(SERVICEID, USERID, STARTDATE, ENDDATE, LOCATION);
        Appointment a2 = appointmentDao.create(SERVICEID, USERID, STARTDATE.plusHours(2), ENDDATE, LOCATION);

        // 3. Postcondiciones - assertions (todas las que sean necesarias)
        Assert.assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, "appointments"));
    }

    @Test
    public void testFindById(){
        jdbcTemplate.execute(String.format("insert into appointments(appointmentid, serviceid, userid, startdate, enddate, location, confirmed) values (%d, %d, %d, '%s', '%s', '%s', false);", APPOINTMENT_ID,SERVICEID, USERID, Timestamp.valueOf(STARTDATE), Timestamp.valueOf(ENDDATE), LOCATION));

        Appointment appointment = appointmentDao.findById(APPOINTMENT_ID).get();

        Assert.assertEquals(SERVICEID,appointment.getServiceid());
        Assert.assertTrue(ENDDATE.isAfter(appointment.getStartDate()));
    }

    @Test
    public void testConfirmAppointment(){
        jdbcTemplate.execute(String.format("insert into appointments(appointmentid, serviceid, userid, startdate, enddate, location, confirmed) values (%d, %d, %d, '%s', '%s', '%s', false);", APPOINTMENT_ID,SERVICEID, USERID, Timestamp.valueOf(STARTDATE), Timestamp.valueOf(ENDDATE), LOCATION));
        jdbcTemplate.execute(String.format("insert into appointments(appointmentid, serviceid, userid, startdate, enddate, location, confirmed) values (%d, %d, %d, '%s', '%s', '%s', false);", APPOINTMENT_ID2,SERVICEID, USERID, Timestamp.valueOf(STARTDATE.plusHours(1)), Timestamp.valueOf(ENDDATE.plusHours(1)), LOCATION));
        //Appointment toConfirmAppointment = appointmentDao.create(SERVICEID, USERID, STARTDATE, ENDDATE, LOCATION);
        //Appointment pendingAppointment = appointmentDao.create(SERVICEID, USERID, STARTDATE.plusHours(1), ENDDATE, LOCATION);

        appointmentDao.confirmAppointment(1);

        Assert.assertEquals(JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "appointments", "confirmed = true"), 1);
        Assert.assertEquals(JdbcTestUtils.countRowsInTable(jdbcTemplate, "appointments"), 2);
    }

    @Test
    public void testCancelAppointment(){
        jdbcTemplate.execute(String.format("insert into appointments(appointmentid, serviceid, userid, startdate, enddate, location, confirmed) values (1, %d, %d, '%s', '%s', '%s', false);", SERVICEID, USERID, Timestamp.valueOf(STARTDATE), Timestamp.valueOf(ENDDATE), LOCATION));
        jdbcTemplate.execute(String.format("insert into appointments(appointmentid, serviceid, userid, startdate, enddate, location, confirmed) values (2, %d, %d, '%s', '%s', '%s', false);", SERVICEID, USERID, Timestamp.valueOf(STARTDATE.plusHours(1)), Timestamp.valueOf(ENDDATE.plusHours(1)), LOCATION));

        //Appointment appointment = appointmentDao.create(SERVICEID, USERID, STARTDATE, ENDDATE, LOCATION);
        //Appointment anotherAppointment = appointmentDao.create(SERVICEID, USERID, STARTDATE.plusHours(1), ENDDATE, LOCATION);

        appointmentDao.cancelAppointment(APPOINTMENT_ID);
        Assert.assertFalse( appointmentDao.findById(APPOINTMENT_ID).isPresent());
        Assert.assertTrue( appointmentDao.findById(APPOINTMENT_ID2).isPresent());
    }
}
