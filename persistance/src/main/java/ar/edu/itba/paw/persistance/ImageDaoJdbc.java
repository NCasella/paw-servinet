package ar.edu.itba.paw.persistance;

// import ar.edu.itba.paw.model.ImageModel;
// import ar.edu.itba.paw.services.ImageDao;
// import org.springframework.beans.factory.annotation.Autowired;
//
//
// import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.jdbc.core.RowMapper;
// import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
// import org.springframework.stereotype.Repository;
//
//
//
// import javax.sql.DataSource;
//
//
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
//
//
// @Repository
// public class ImageDaoJdbc implements ImageDao {
//
//     private static final RowMapper<ImageModel> ROW_MAPPER = (rs, rowNum) -> new ImageModel(rs.getLong("imageid"),
//              rs.getBytes("imageBytes"));
//
//     private final JdbcTemplate jdbcTemplate;
//     private final SimpleJdbcInsert simpleJdbcInsert;
//
//     @Autowired
//     public ImageDaoJdbc(final DataSource ds){
//         jdbcTemplate = new JdbcTemplate(ds);
//         simpleJdbcInsert = new SimpleJdbcInsert(ds).withTableName("images").usingGeneratedKeyColumns("imageid");
//     }
//     @Override
//     public Optional<ImageModel> getImageById(long id)  {
//         final List<ImageModel> list = jdbcTemplate.query("SELECT * from Images WHERE imageId = ?", new Object[] {id}, ROW_MAPPER);
//         return list.stream().findFirst();
//     }
//
//     @Override
//     public ImageModel addImage( byte[] image){
//         final Map<String, Object> userData = new HashMap<>();
//         userData.put("imageBytes", image);
//        final Number generatedId = simpleJdbcInsert.executeAndReturnKey(userData);
//         return new ImageModel(generatedId.longValue(), image);
//     }
// }
