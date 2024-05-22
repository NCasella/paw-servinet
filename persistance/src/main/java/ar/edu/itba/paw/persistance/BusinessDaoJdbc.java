package ar.edu.itba.paw.persistance;

//import ar.edu.itba.paw.model.Business;
//import ar.edu.itba.paw.services.BusinessDao;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
//import org.springframework.stereotype.Repository;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//@Repository
//public class BusinessDaoJdbc implements BusinessDao {
//    private static final RowMapper<Business> ROW_MAPPER = (rs, rowNum) -> new Business(rs.getLong("businessid"),
//            rs.getString("businessname"), rs.getLong("userid"), rs.getString("businessTelephone"), rs.getString("businessEmail"),
//            rs.getString("businessLocation"));
//    private final Logger LOGGER = LoggerFactory.getLogger(BusinessDaoJdbc.class);
//    private final JdbcTemplate jdbcTemplate;
//    private final SimpleJdbcInsert simpleJdbcInsert;
//
//    @Autowired
//    public BusinessDaoJdbc(final DataSource ds) {
//        jdbcTemplate = new JdbcTemplate(ds);
//        simpleJdbcInsert = new SimpleJdbcInsert(ds).usingGeneratedKeyColumns("businessid")
//                .withTableName("business");
//    }
//
//    @Override
//    public Optional<Business> findById(long id) {
//        final List<Business> list = jdbcTemplate.query("SELECT * from business WHERE businessid = ?", new Object[] {id}, ROW_MAPPER);
//        return list.stream().findFirst();
//    }
//
//    @Override
//    public Optional<Business> findByBusinessName(String businessName) {
//        final List<Business> list = jdbcTemplate.query("SELECT * from business WHERE businessname= ?", new Object[] {businessName}, ROW_MAPPER);
//        return list.stream().findFirst();
//    }
//
//    @Override
//    public List<Business> findByAdminId(long adminId){
//        return jdbcTemplate.query("SELECT * from business WHERE userid= ?", new Object[] {adminId}, ROW_MAPPER);
//    }
//
//    @Override
//    public Optional<String> getBusinessEmail(long businessid) {
//        final List<String> list = jdbcTemplate.query("SELECT businessEmail FROM business WHERE businessid = ?;",
//                new Object[]{ businessid }, (rs, rowNum) -> new String(rs.getString("businessEmail")));
//        return list.stream().findFirst();
//    }
//
//    @Override
//    public boolean deleteBusiness(long businessid) {
//        Optional<Business> business = findById(businessid);
//        if (business.isEmpty()) {
//            LOGGER.warn("Business not found");
//            return true;
//        }
//        long userId = business.get().getUserId();
//        jdbcTemplate.update("delete from business where businessid = ?", businessid);
//        LOGGER.info("Business successfully deleted");
//        int count = jdbcTemplate.queryForObject("select count(*) from business where userid= ?", Integer.class , userId);
//        if(count == 0){
//            //podría usarse la de userDao
//            jdbcTemplate.update("update users set isprovider = false where userid = ?", userId);
//            LOGGER.info("User status updated");
//            return false;
//        }
//        return true;
//    }
//    private void changeField(final String field, long businessId, String value) {
//        jdbcTemplate.update(String.format("update business set %s = ? where businessid = ?", field), value, businessId);
//        LOGGER.info("Field '{}' successfully changed", field);
//
//    }
//
//    @Override
//    public void changeBusinessEmail(long businessId, String value) {
//        changeField("businessemail", businessId, value);
//    }
//    @Override
//    public void changeBusinessTelephone(long businessId,String value){
//        changeField("businesstelephone",businessId,value);
//    }
//    @Override
//    public void changeBusinessLocation(long businessId,String value){
//        changeField("businesslocation", businessId, value);
//    }
//
//    @Override
//    public Business createBusiness(String businessName, long userId, String telephone, String email, String location) {
//        Map<String,Object> businessData = new HashMap<>();
//        businessData.put("businessname", businessName);
//        businessData.put("userid", userId);
//        businessData.put("businesstelephone", telephone);
//        businessData.put("businessemail", email);
//        businessData.put("businesslocation", location);
//        final Number generatedId = simpleJdbcInsert.executeAndReturnKey(businessData);
//        return new Business(generatedId.longValue(), businessName, userId, telephone, email, location);
//    }
//
//}
//