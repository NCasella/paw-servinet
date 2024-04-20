package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Appointment;
import ar.edu.itba.paw.model.AppointmentInfo;
import ar.edu.itba.paw.services.AppointmentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class AppointmentDaoJdbc implements AppointmentDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    private static final RowMapper<Appointment> ROW_MAPPER = (rs, rowNum) -> new Appointment(rs.getInt("appointmentid"), rs.getInt("serviceid"), rs.getInt("userid"),
            rs.getTimestamp("startDate").toLocalDateTime(), rs.getTimestamp("endDate").toLocalDateTime(),rs.getString("location") ,rs.getBoolean("confirmed"));

    private static final RowMapper<AppointmentInfo> APPOINTMENT_INFO_ROW_MAPPER = (rs, rowNum) -> new AppointmentInfo(rs.getInt("appointmentid"),
            rs.getInt("serviceid"), rs.getString("serviceName"),
            rs.getString("businessName"), rs.getString("location"),
            rs.getTimestamp("startDate").toLocalDateTime(), rs.getBoolean("confirmed"),
            rs.getString("businessemail"), rs.getString("businesstelephone"));
    @Autowired
    public AppointmentDaoJdbc(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        simpleJdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("appointments").usingGeneratedKeyColumns("appointmentid");
    }

    @Override
    public List<AppointmentInfo> getAppointmentsByUser(long userid){
        final List<AppointmentInfo> list =jdbcTemplate.query("SELECT a.appointmentid, a.serviceid, s.servicename as serviceName, businessName, a.location, a.startDate, a.confirmed, businessemail, businesstelephone FROM appointments a JOIN services s ON a.serviceid = s.id JOIN business b ON s.businessid = b.businessid WHERE a.userid = ?", new Object[] {userid}, APPOINTMENT_INFO_ROW_MAPPER);
        return list;
    }

    @Override
    public Optional<Appointment> findById(long id) {
        final List<Appointment> list = jdbcTemplate.query("SELECT * from appointments WHERE appointmentid = ?", new Object[] {id}, ROW_MAPPER);
        return list.stream().findFirst();
    }

    @Override
    public Optional<List<Appointment>> getAllUpcomingServiceAppointments(long serviceid) {
        final List<Appointment> list = jdbcTemplate.query("SELECT * from appointments WHERE serviceid = ? and startdate > ? ", new Object[] {serviceid, Timestamp.valueOf(LocalDateTime.now()) }, ROW_MAPPER);
        return Optional.of(list);
    }

    @Override
    public Appointment create(long serviceid, long userid, LocalDateTime startDate, LocalDateTime endDate, String location) {

        final Map<String, Object> appointmentData = new HashMap<>();
        appointmentData.put("serviceid", serviceid);
        appointmentData.put("userid", userid);
        appointmentData.put("startdate", Timestamp.valueOf(startDate));
        appointmentData.put("enddate", Timestamp.valueOf(endDate) );
        appointmentData.put("location", location);
        //appointmentData.put("confirmed", false); pues default es false

        final Number generatedId = simpleJdbcInsert.executeAndReturnKey(appointmentData);
        return new Appointment(generatedId.longValue(), serviceid, userid, startDate, endDate, location, false);
    }

    @Override
    public void confirmAppointment(long appointmentid) {
        /*
        try {
            jdbcTemplate.update("UPDATE appointments SET confirmed=TRUE WHERE appointmentid = ?", appointmentid);
        } catch (DataAccessException e) {
            throw new NoSuchElementException(); //AppointmentNotFoundException;
        }
        */
        jdbcTemplate.update("UPDATE appointments SET confirmed=TRUE WHERE appointmentid = ?", appointmentid);

    }

    @Override
    public void cancelAppointment(long appointmentid) {
        //TODO: Falta excep
        jdbcTemplate.update("DELETE FROM appointments WHERE appointmentid = ?", appointmentid);
    }
}
