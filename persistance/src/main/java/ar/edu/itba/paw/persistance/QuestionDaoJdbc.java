package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Question;
import ar.edu.itba.paw.services.QuestionDao;
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
public class QuestionDaoJdbc implements QuestionDao {

    private static final RowMapper<Question> ROW_MAPPER = (rs, rowNum) -> new Question(rs.getInt("questionid") ,
            rs.getInt("serviceid"),rs.getInt("userid"), rs.getString("question"),
            rs.getString("response"), rs.getDate("date").toLocalDate());

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;
    private final Logger LOGGER = LoggerFactory.getLogger(QuestionDaoJdbc.class);

    @Autowired
    public QuestionDaoJdbc(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        simpleJdbcInsert = new SimpleJdbcInsert(ds).usingGeneratedKeyColumns("questionid")
                .withTableName("questions");
    }

    @Override
    public List<Question> getAllQuestions(long serviceid, int page) {
        return jdbcTemplate.query("SELECT * from questions WHERE serviceid = ? ORDER BY date DESC OFFSET ? LIMIT 10", new Object[] {serviceid, page*10}, ROW_MAPPER);
    }

    @Override
    public Optional<Question> findById(long id) {
        final List<Question> list = jdbcTemplate.query("SELECT * from questions WHERE questionid = ?", new Object[] {id}, ROW_MAPPER);
        return list.stream().findFirst();
    }

    @Override
    public Question create(long serviceid, long userid, String question) {
        final Map<String, Object> questionData = new HashMap<>();
        questionData.put("serviceid", serviceid);
        questionData.put("userid", userid);
        questionData.put("question", question);
        questionData.put("response", null);
        questionData.put("date", new Date());
        final Number generatedId = simpleJdbcInsert.executeAndReturnKey(questionData);
        return new Question(generatedId.longValue(), serviceid, userid, question, null, null);
    }

    @Override
    public void addResponse(long id, String response) {
        try {
            jdbcTemplate.update("UPDATE questions SET response = ? WHERE questionid = ?", response, id);
        }catch(DataAccessException e){
           LOGGER.warn("Error adding response to question: {}", e.getMessage());
        }
    }

    @Override
    public int getQuestionsCount(long serviceid) {
        return jdbcTemplate.queryForObject(  "SELECT COUNT(*) FROM questions WHERE serviceId = ?", Integer.class, serviceid);
    }

    @Override
    public Optional<Map<Question, String>> getQuestionsToRespond(long userid) {
        final List<Question> questions = jdbcTemplate.query(
                "SELECT q.*\n" +
                "FROM questions q\n" +
                "JOIN services s ON q.serviceid = s.id\n" +
                "JOIN business b ON s.businessid = b.businessid\n" +
                "WHERE b.userid = ?\n" +
                "AND q.response IS NULL\n" +
                "ORDER BY q.date DESC;",
                new Object[] {userid}, ROW_MAPPER
        );

        Map<Question, String> questionServiceMap = new HashMap<>();

        for (Question question : questions) {
            String serviceName = getServiceNameForQuestion(question.getServiceid());
            questionServiceMap.put(question, serviceName);
        }
        return Optional.of(questionServiceMap);
    }

    private String getServiceNameForQuestion(long serviceId) {
        String serviceName = jdbcTemplate.queryForObject("SELECT servicename FROM services WHERE id = ?", new Object[]{serviceId}, String.class);
        return serviceName;
    }
}
