package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.PasswordRecoveryCode;
import ar.edu.itba.paw.services.PasswordRecoveryCodeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PasswordRecoveryCodeJdbc implements PasswordRecoveryCodeDao {
    private static final RowMapper<PasswordRecoveryCode> ROW_MAPPER = (rs, rowNum) -> new PasswordRecoveryCode(rs.getLong("userid"),
             UUID.fromString(rs.getString("code")), rs.getTimestamp("expirationdate").toLocalDateTime());
    private final JdbcTemplate jdbcTemplate;
     private Logger LOGGER = LoggerFactory.getLogger(PasswordRecoveryCodeJdbc.class);

    @Autowired
    public PasswordRecoveryCodeJdbc(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
    }
    @Override
    public void saveCode(long userid, UUID code) {
        try {
            jdbcTemplate.update("INSERT INTO passwordrecoverycodes (userid, code, expirationdate) VALUES (?,?,?)", userid, code, LocalDateTime.now().plusHours(1));
        }catch(DataAccessException e){
            LOGGER.warn("Error saving password recovery code: {}", e.getMessage());
        }
    }

    @Override
    public Optional<PasswordRecoveryCode> getCode(long userid) {
        final List<PasswordRecoveryCode> list = jdbcTemplate.query("SELECT * from passwordrecoverycodes WHERE userid= ?", new Object[] {userid}, ROW_MAPPER);
        return list.stream().findFirst();
    }
    @Override
    public Optional<PasswordRecoveryCode> getCode(UUID code) {
        final List<PasswordRecoveryCode> list = jdbcTemplate.query("SELECT * from passwordrecoverycodes WHERE code= ?", new Object[] {code}, ROW_MAPPER);
        return list.stream().findFirst();
    }

    @Override
    public void deleteCode(long userid) {
        try {
            jdbcTemplate.update("DELETE FROM passwordrecoverycodes WHERE userid= ?", userid);
        }catch(DataAccessException e){
            LOGGER.warn("Error deleting password recovery code: {}", e.getMessage());
        }
    }
}
