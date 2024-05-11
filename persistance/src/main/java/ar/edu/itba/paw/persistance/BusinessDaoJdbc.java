package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Business;
import ar.edu.itba.paw.services.BusinessDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class BusinessDaoJdbc implements BusinessDao {
    private static final RowMapper<Business> ROW_MAPPER = (rs, rowNum) -> new Business(rs.getLong("businessid"),
            rs.getString("businessname"), rs.getLong("userid"), rs.getString("businessTelephone"), rs.getString("businessEmail"),
            rs.getString("businessLocation"));
    private final Logger LOGGER = LoggerFactory.getLogger(BusinessDaoJdbc.class);
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    @Autowired
    public BusinessDaoJdbc(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        simpleJdbcInsert = new SimpleJdbcInsert(ds).usingGeneratedKeyColumns("businessid")
                .withTableName("business");
    }

    @Override
    public Optional<Business> findById(long id) {
        final List<Business> list = jdbcTemplate.query("SELECT * from business WHERE businessid = ?", new Object[] {id}, ROW_MAPPER);
        return list.stream().findFirst();
    }

    @Override
    public Optional<Business> findByBusinessName(String businessName) {
        final List<Business> list = jdbcTemplate.query("SELECT * from business WHERE businessname= ?", new Object[] {businessName}, ROW_MAPPER);
        return list.stream().findFirst();
    }

    @Override
    public List<Business> findByAdminId(long adminId){
        return jdbcTemplate.query("SELECT * from business WHERE userid= ?", new Object[] {adminId}, ROW_MAPPER);
    }

    @Override
    public void deleteBusiness(long businessid) {
        try {
            jdbcTemplate.update("delete from business where businessid = ?", businessid);
            LOGGER.info("Business successfully deleted");
        }catch (DataAccessException e){
            LOGGER.warn("Error deleting business: {}", e.getMessage());
        }
    }
    private void changeField(final String field, long businessId, String value) {
        try {
            jdbcTemplate.update(String.format("update business set %s = ? where businessid = ?", field), value, businessId);
            LOGGER.info("Field '{}' successfully changed", field);
        } catch (DataAccessException e) {
            LOGGER.warn("Error changing field '{}': {}", field, e.getMessage());
        }
    }

    @Override
    public void changeBusinessEmail(long businessId, String value) {
        changeField("email", businessId, value);
    }

    @Override
    public Business createBusiness(String businessName, long userId, String telephone, String email, String location) {
        Map<String,Object> businessData = new HashMap<>();
        businessData.put("businessname", businessName);
        businessData.put("userid", userId);
        businessData.put("telephone", telephone);
        businessData.put("email", email);
        businessData.put("location", location);
        final Number generatedId = simpleJdbcInsert.executeAndReturnKey(businessData);
        return new Business(generatedId.longValue(), businessName, userId, telephone, email, location);
    }

}
