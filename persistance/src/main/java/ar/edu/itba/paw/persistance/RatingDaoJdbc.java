package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Rating;
import ar.edu.itba.paw.services.RatingDao;
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
public class RatingDaoJdbc implements RatingDao {

    private static final RowMapper<Rating> ROW_MAPPER = (rs, rowNum) -> new Rating(rs.getInt("ratingid") ,
            rs.getInt("serviceid"),rs.getInt("userid"), rs.getInt("rating"),
            rs.getString("comment"), rs.getDate("date").toLocalDate());

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final Logger LOGGER = LoggerFactory.getLogger(RatingDaoJdbc.class);

    @Autowired
    public RatingDaoJdbc(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        simpleJdbcInsert = new SimpleJdbcInsert(ds).usingGeneratedKeyColumns("ratingid")
                .withTableName("ratings");
    }

    @Override
    public List<Rating> getAllRatings(long serviceid, int page) {
        return jdbcTemplate.query("SELECT * from ratings WHERE serviceid = ? ORDER BY date DESC OFFSET ? LIMIT 10", new Object[] {serviceid, page*10}, ROW_MAPPER);
    }

    @Override
    public Optional<Rating> findById(long id) {
        final List<Rating> list = jdbcTemplate.query("SELECT * from ratings WHERE ratingid = ?", new Object[] {id}, ROW_MAPPER);
        return list.stream().findFirst();
    }

    @Override
    public Rating create(long serviceid, long userid, int rating, String comment) {
        final Map<String, Object> ratingData = new HashMap<>();
        ratingData.put("serviceid", serviceid);
        ratingData.put("userid", userid);
        ratingData.put("rating", rating);
        ratingData.put("comment", comment);
        ratingData.put("date", new Date());
        final Number generatedId = simpleJdbcInsert.executeAndReturnKey(ratingData);
        return new Rating(generatedId.longValue(), serviceid, userid, rating, comment, null);
    }

    @Override
    public double getRatingsAvg(long serviceid) {
        final Double avg = jdbcTemplate.queryForObject("SELECT AVG(rating) FROM ratings WHERE serviceid = ?", Double.class, serviceid);
        return avg != null ? avg : 0.0;
    }

    @Override
    public int getRatingsCount(long serviceid) {
        return jdbcTemplate.queryForObject(  "SELECT COUNT(*) FROM ratings WHERE serviceId = ?", Integer.class, serviceid);
    }

    @Override
    public Optional<Rating> hasAlreadyRated(long userid, long serviceid) {
        List<Rating> ratings = jdbcTemplate.query("SELECT * FROM ratings WHERE serviceid = ? AND userid = ?", new Object[] {serviceid, userid}, ROW_MAPPER);
        return ratings.stream().findFirst();
    }

    @Override
    public void edit(long ratingid, int rating, String comment) {
        try {
            jdbcTemplate.update("UPDATE ratings SET rating = ?, comment = ?, date = ? WHERE ratingid = ?", rating, comment, new Date(), ratingid);
        }catch(DataAccessException e){
            LOGGER.warn("Error editing rating: {}", e.getMessage());
        }
    }

}
