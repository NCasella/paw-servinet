package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Appointment;
import ar.edu.itba.paw.model.AppointmentInfo;
import ar.edu.itba.paw.services.AppointmentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger LOGGER = LoggerFactory.getLogger(AppointmentDaoJdbc.class);

    private static final RowMapper<Appointment> APPOINTMENT_ROW_MAPPER = (rs, rowNum) -> new Appointment(rs.getInt("appointmentid"), rs.getInt("serviceid"), rs.getInt("userid"),
            rs.getTimestamp("startDate").toLocalDateTime(), rs.getTimestamp("endDate").toLocalDateTime(),rs.getString("location") ,rs.getBoolean("confirmed"));

    private static final RowMapper<AppointmentInfo> APPOINTMENT_INFO_ROW_MAPPER = (rs, rowNum) -> new AppointmentInfo(rs.getInt("appointmentid"), rs.getInt("serviceid"),
            rs.getTimestamp("startDate").toLocalDateTime(), rs.getTimestamp("endDate").toLocalDateTime(),rs.getString("location") ,rs.getBoolean("confirmed"),
            rs.getString("servicename"),rs.getString("businessemail"),rs.getString("businesstelephone") );


    @Autowired
    public AppointmentDaoJdbc(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        simpleJdbcInsert = new SimpleJdbcInsert(ds)
                .withTableName("appointments").usingGeneratedKeyColumns("appointmentid");
    }

    @Override
    public Optional<Appointment> findById(long id) {
        final List<Appointment> list = jdbcTemplate.query("SELECT * from appointments WHERE appointmentid = ?", new Object[] {id}, APPOINTMENT_ROW_MAPPER);
        return list.stream().findFirst();
    }

    @Override
    public List<Appointment> getAllUpcomingServiceAppointments(long serviceid) {
        return jdbcTemplate.query("SELECT * from appointments WHERE serviceid = ? and startdate > NOW() ", new Object[] {serviceid }, APPOINTMENT_ROW_MAPPER);
    }

    @Override
    public List<Appointment> getAllUpcomingServicesAppointments(Collection<Long> serviceIds, boolean confirmed){

        List<Object> params = new ArrayList<>(serviceIds);
        params.add(confirmed);

        String inSql = String.join(",", Collections.nCopies(serviceIds.size(), "?"));
        final List<Appointment> list = jdbcTemplate.query(String.format("SELECT * from appointments WHERE serviceid in (%s) and startdate > NOW() and confirmed = ? ",inSql), params.toArray(), APPOINTMENT_ROW_MAPPER);
        return list;
    }

    @Override
    public List<AppointmentInfo> getAllUpcomingUserAppointments(long userid, boolean confirmed) {
        final List<AppointmentInfo> list = jdbcTemplate.query(
                "SELECT info.* , businessemail, businesstelephone\n" +
                        "    FROM business as b RIGHT JOIN\n" +
                        "    (SELECT a.*, servicename, businessid FROM\n" +
                        "        (SELECT id, servicename, businessid FROM services) as s\n" +
                        "        RIGHT JOIN\n" +
                        "        (SELECT * FROM appointments\n" +
                        "                WHERE userid = ? and confirmed= ? and startdate > NOW()) as a\n" +
                        "                ON a.serviceid = s.id) as info\n" +
                        "on info.businessid = b.businessid;", new Object[] {userid, confirmed }, APPOINTMENT_INFO_ROW_MAPPER);
        return list;
    }



    @Override
    public Appointment create(long serviceid, long userid, LocalDateTime startDate, LocalDateTime endDate, String location) {

        final Map<String, Object> appointmentData = new HashMap<>();
        appointmentData.put("serviceid", serviceid);
        appointmentData.put("userid", userid);
        appointmentData.put("startdate", Timestamp.valueOf(startDate));
        appointmentData.put("enddate", Timestamp.valueOf(endDate) );
        appointmentData.put("location", location);
        appointmentData.put("confirmed", false);

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
        try {
            jdbcTemplate.update("UPDATE appointments SET confirmed=TRUE WHERE appointmentid = ?", appointmentid);
        }catch(Exception e){
            LOGGER.warn("Error while updating appointment status: {}", e.getMessage());
        }

    }

    @Override
    public void cancelAppointment(long appointmentid) {
        try {
            jdbcTemplate.update("DELETE FROM appointments WHERE appointmentid = ?", appointmentid);
        }catch(Exception e){
            LOGGER.warn("Error while deleting appointment: {}", e.getMessage());
        }
    }
}
