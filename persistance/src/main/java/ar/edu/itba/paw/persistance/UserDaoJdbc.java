package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.services.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.util.*;

@Repository
public class UserDaoJdbc implements UserDao {

    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) -> new User(rs.getInt("userid") ,
            rs.getString("username"),rs.getString("password"), rs.getString("name"), rs.getString("surname"), rs.getString("email"),
            rs.getString("telephone"), rs.getBoolean("isprovider"));

    private static final RowMapper<Boolean> PROVIDER_MAPPER = (rs, rowNum) -> rs.getBoolean("isprovider");


    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private Logger LOGGER = LoggerFactory.getLogger(UserDaoJdbc.class);

    @Autowired
    public UserDaoJdbc(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        simpleJdbcInsert = new SimpleJdbcInsert(ds).usingGeneratedKeyColumns("userid")
                .withTableName("users");
    }

    @Override
    public Optional<User> findById(long id) {
        // nunca se debe concatenar parametros en una query
        // "SELECT * from users WHERE userID " + id esta mal
        final List<User> list = jdbcTemplate.query("SELECT * from users WHERE userid = ?", new Object[] {id}, ROW_MAPPER);
        return list.stream().findFirst();
    }
    @Override
    public Optional<User> findByEmail(String email) {
        // nunca se debe concatenar parametros en una query
        // "SELECT * from users WHERE userID " + id esta mal
        final List<User> list = jdbcTemplate.query("SELECT * from users WHERE email = ?", new Object[] {email}, ROW_MAPPER);
        return list.stream().findFirst();
    }
    @Override
    public Optional<User> findByUsername(String username) {
        final List<User> list = jdbcTemplate.query("SELECT * from users WHERE username = ?", new Object[] {username}, ROW_MAPPER);
        return list.stream().findFirst();
    }

    @Override
    public boolean isProvider(long userid) {
        final List<Boolean> list = jdbcTemplate.query("SELECT isprovider from users WHERE userid = ?", new Object[] {userid}, PROVIDER_MAPPER );
        return list.stream().findFirst().isPresent() ? list.stream().findFirst().get() : false;
    }

    @Override
    public void changeUsername(long userid,String value){
        changeField("username",userid,value);
    }
    @Override
    public void changeEmail(long userid,String value){
        changeField("email",userid,value);
    }
    @Override
    public void changePassword(String email,String value){
        changeField("password",findByEmail(email).get().getUserId(),value);
    }
    @Override
    public void changeUserType(long userid){
        try {
            jdbcTemplate.update("update users set isprovider = not isprovider where userid = ?", userid);
        }catch(DataAccessException e){
            LOGGER.warn("Error changing user type: {}", e.getMessage());
        }
    }

    private void changeField(final String field,long userid,String value){//CONSULTAR: si se puede concatenar asi el field
        try {
            jdbcTemplate.update(String.format("update users set  %s  = ? where userid = ? ", field), value, userid);//deberia ser seguro, pues field es un parametro que no viene del usuario
        }catch(DataAccessException e){
            LOGGER.warn("Error changing field '{}': {}", field, e.getMessage());
        }
    }

    @Override
    public User create(final String username, final String name,final String surname, final String password, final String email, final String telephone, final boolean isProvider) {
        final Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("name", name);
        userData.put("password", password); // TODO: hash password (bcrypt
        userData.put("surname", surname);
        userData.put("email", email);
        userData.put("telephone", telephone);
        userData.put("isprovider", isProvider);
        final Number generatedId = simpleJdbcInsert.executeAndReturnKey(userData);
        return new User(generatedId.longValue(), username ,password, name,surname, email, telephone, isProvider);
    }

}
