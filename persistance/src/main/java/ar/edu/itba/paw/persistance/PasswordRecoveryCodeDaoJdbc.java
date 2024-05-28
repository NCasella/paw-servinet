package ar.edu.itba.paw.persistance;

// import ar.edu.itba.paw.model.PasswordRecoveryCode;
// import ar.edu.itba.paw.services.PasswordRecoveryCodeDao;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.jdbc.core.RowMapper;
// import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
// import org.springframework.stereotype.Repository;
//
// import javax.sql.DataSource;
// import java.sql.Timestamp;
// import java.time.LocalDateTime;
// import java.util.*;
//
// @Repository
// public class PasswordRecoveryCodeDaoJdbc implements PasswordRecoveryCodeDao {
//     private static final RowMapper<PasswordRecoveryCode> ROW_MAPPER = (rs, rowNum) -> new PasswordRecoveryCode(rs.getLong("userid"),
//              UUID.fromString(rs.getString("code")), rs.getTimestamp("expirationdate").toLocalDateTime());
//     private final JdbcTemplate jdbcTemplate;
//     private final SimpleJdbcInsert simpleJdbcInsert;
//     private final Logger LOGGER = LoggerFactory.getLogger(PasswordRecoveryCodeDaoJdbc.class);
//
//     @Autowired
//     public PasswordRecoveryCodeDaoJdbc(final DataSource ds){
//         jdbcTemplate = new JdbcTemplate(ds);
//         this.simpleJdbcInsert = new SimpleJdbcInsert(ds).withTableName("passwordrecoverycodes").usingGeneratedKeyColumns("userid");
//     }
//     @Override
//     public PasswordRecoveryCode saveCode(long userid, UUID code, LocalDateTime expirationDate) {
//         jdbcTemplate.update("INSERT INTO passwordrecoverycodes (userid, code, expirationdate) VALUES (?,?,?)", userid, code, Timestamp.valueOf(expirationDate));
//         return new PasswordRecoveryCode(userid, code, expirationDate);
//     }
//
//     @Override
//     public Optional<PasswordRecoveryCode> getCodeByUserId(long userid) {
//         final List<PasswordRecoveryCode> list = jdbcTemplate.query("SELECT * from passwordrecoverycodes WHERE userid= ?", new Object[] {userid}, ROW_MAPPER);
//         return list.stream().findFirst();
//     }
//     @Override
//     public Optional<PasswordRecoveryCode> getCodeByUUID(UUID code) {
//         final List<PasswordRecoveryCode> list = jdbcTemplate.query("SELECT * from passwordrecoverycodes WHERE code= ?", new Object[] {code}, ROW_MAPPER);
//         return list.stream().findFirst();
//     }
//
//     @Override
//     public void deleteCode(long userid) {
//         jdbcTemplate.update("DELETE FROM passwordrecoverycodes WHERE userid= ?", userid);
//     }
// }
