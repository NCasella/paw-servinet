 package ar.edu.itba.paw.persistance;


 import ar.edu.itba.paw.model.Categories;
 import ar.edu.itba.paw.model.Neighbourhoods;
 import ar.edu.itba.paw.model.PricingTypes;
 import ar.edu.itba.paw.model.Service;
 import ar.edu.itba.paw.persistance.config.TestConfig;
 import org.junit.Assert;
 import org.junit.Before;
 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.jdbc.core.JdbcTemplate;
 import org.springframework.test.annotation.Rollback;
 import org.springframework.test.context.ContextConfiguration;
 import org.springframework.test.context.jdbc.Sql;
 import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
 import org.springframework.test.jdbc.JdbcTestUtils;
 import org.springframework.transaction.annotation.Transactional;

 import javax.persistence.EntityManager;
 import javax.persistence.PersistenceContext;
 import javax.sql.DataSource;
 import java.util.List;

 @Sql("classpath:sql/schema.sql")
 @Transactional
 @Rollback
 @RunWith(SpringJUnit4ClassRunner.class)
 @ContextConfiguration(classes = TestConfig.class)
 public class ServiceDaoJpaTest {
     private static final String NAME = "name";
     private static final long SERVICEID=1;
     private static final long BUSINESSID = 1;
     private static final String DESCRIPTION = "description";
     private static final Boolean HOMESERVICE = true;
     private static final String LOCATION = "calle 123";
     private static final Neighbourhoods[] NEIGHBOURHOODS = {Neighbourhoods.PALERMO};
     private static final int TOTAL_AMOUNT=10;
     private static final int CAPPIN_AMOUNT=7;
     private static final int CAPPIN_FILTERED_AMOUNT=6;
     private static final int PALERMO_AMOUNT=4;
     private static final Categories CATEGORY = Categories.BELLEZA;
     private static final int DURATION = 30;
     private static final String PRICE = "ARS 1000";
     private static final Boolean ADDITIONALCHARGES = false;
     private static final PricingTypes PRICING = PricingTypes.PER_TOTAL;

     @PersistenceContext
     private EntityManager em;
    @Autowired
    private ServiceDaoJpa serviceDao;
    @Autowired
    private DataSource ds;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup(){
        this.jdbcTemplate = new JdbcTemplate(ds);
        jdbcTemplate.execute("INSERT INTO users (userid, username, password, name, surname, email, telephone) VALUES (1, 'username', 'password', 'name', 'surname', 'email', 'telephone')");
        jdbcTemplate.execute("INSERT INTO business(businessid, userid, businessname, businessTelephone, businessEmail, businessLocation) VALUES (1, 1, 'businessname', 'businessTelephone', 'businessEmail', 'businessLocation')");
    }

    @Test
     public void testCreate() {
        Service service = serviceDao.create(BUSINESSID, NAME, DESCRIPTION, HOMESERVICE, LOCATION, NEIGHBOURHOODS,CATEGORY, DURATION, PRICING, PRICE, ADDITIONALCHARGES,null);
        em.flush();

        Assert.assertNotNull(service);
        Assert.assertEquals(BUSINESSID, service.getBusinessid());
        Assert.assertEquals(NAME, service.getName());
        Assert.assertEquals(DESCRIPTION, service.getDescription());
        Assert.assertEquals(HOMESERVICE, service.getHomeService());
        Assert.assertEquals(LOCATION, service.getLocation());
        Assert.assertEquals(CATEGORY, service.getCategory());
        Assert.assertEquals(DURATION, service.getDuration());
        Assert.assertEquals(PRICING, service.getPricing());
        Assert.assertEquals(PRICE, service.getPrice());
        Assert.assertEquals(ADDITIONALCHARGES, service.getAdditionalCharges());
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "services"));
    }

    @Test
     public void testFindById() {
        jdbcTemplate.execute(String.format("INSERT INTO services (id, businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges, imageId) VALUES (%d, %d, '%s', '%s', true, '%s', 'Belleza', 30, 'Total', '%s', false, null);", SERVICEID,BUSINESSID, NAME, DESCRIPTION, LOCATION,PRICE));
        jdbcTemplate.execute(String.format("INSERT INTO nbservices (serviceid, neighbourhood) VALUES (%d, '%s');", SERVICEID, NEIGHBOURHOODS[0].getValue()));
        Service service = serviceDao.findById(SERVICEID).get();

        Assert.assertNotNull(service);
        Assert.assertEquals(SERVICEID,service.getId());
        Assert.assertEquals(BUSINESSID,service.getBusinessid());
        Assert.assertEquals(NAME,service.getName());
        Assert.assertEquals(DESCRIPTION,service.getDescription());
        Assert.assertEquals(HOMESERVICE,service.getHomeService());
        Assert.assertEquals(LOCATION,service.getLocation());
        Assert.assertEquals(CATEGORY.getValue(),service.getCategory().getValue());
        Assert.assertEquals(DURATION,service.getDuration());
        Assert.assertEquals(PRICING,service.getPricing());
        Assert.assertEquals(PRICE,service.getPrice());
        Assert.assertEquals(ADDITIONALCHARGES,service.getAdditionalCharges());
    }

   @Test
    public void testDelete() {
       Service service = serviceDao.create(BUSINESSID, NAME, DESCRIPTION, HOMESERVICE, LOCATION, NEIGHBOURHOODS,CATEGORY, DURATION, PRICING, PRICE, ADDITIONALCHARGES,null);
        serviceDao.delete(service.getId());

        Assert.assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "services"));
    }

    @Test
    public void testWithQueryAndCategory(){
        Populate();

        List<Service> services=serviceDao.getServicesFilteredBy(0,CATEGORY.getValue(), null,0,"capping");
        Assert.assertEquals(CAPPIN_FILTERED_AMOUNT,services.size());
    }
    @Test
     public void testUnFiltered(){
        Populate();

        List<Service> services =serviceDao.getServicesFilteredBy(0,null,null,0,null);


        Assert.assertEquals(TOTAL_AMOUNT,services.size());
    }

    private void Populate(){
        jdbcTemplate.execute("INSERT INTO users (userid, username, password, name, surname, email, telephone) VALUES (2, 'username2', 'password2', 'name2', 'surname2', 'email2', 'telephone2')");
        jdbcTemplate.execute("insert into business (businessid,userid, businessname, businessTelephone, businessEmail, businessLocation) values (2,2, 'Sol nails shop', '11365335', 'mailfalso@gmail.com', 'Palermo')");
        jdbcTemplate.execute("insert into services(id,businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (1,2, 'Uñas capping', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'Palermo', 'Belleza', 60, 'Por hora', '10000', TRUE)");
        jdbcTemplate.execute("insert into services(id,businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (2,2, 'Uñas francecita', 'Servicio de uñas francesitas.', FALSE, 'Palermo', 'Belleza', 60, 'Por hora', '5000', TRUE)");
        jdbcTemplate.execute("insert into services(id,businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (3,2, 'Limpieza de cuticula', 'Servicio de uñas: limpieza de cuticula express.', FALSE, 'Palermo', 'Belleza', 60, 'Por hora', '3000', TRUE)");
        jdbcTemplate.execute("insert into services(id,businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (4,2, 'Ya no se que otro servicio inventar de uñas', 'Servicio de uñas: necesito que este texto sea largo. Chatgpt: La vida es un viaje lleno de sorpresas y aventuras. Cada día es una oportunidad para explorar, aprender y crecer. .', TRUE, 'Palermo', 'Belleza', 60, 'Por hora', '3000', FALSE)");
        jdbcTemplate.execute("insert into services(id,businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (5,2, 'Uñas capping1', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'calle123', 'Belleza', 60, 'Por hora', '10000', TRUE)");
        jdbcTemplate.execute("insert into services(id,businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (6,2, 'Uñas capping2', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'calle123', 'Limpieza', 60, 'Por hora', '10000', TRUE)");
        jdbcTemplate.execute("insert into services(id,businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (7,2, 'Uñas capping3', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'calle123', 'Belleza', 60, 'Por hora', '10000', TRUE)");
        jdbcTemplate.execute("insert into services(id,businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (8,2, 'Uñas capping4', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'calle123', 'Belleza', 60, 'Por hora', '10000', TRUE)");
        jdbcTemplate.execute("insert into services(id,businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (9,2, 'Uñas capping5', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'calle123', 'Belleza', 60, 'Por hora', '10000', TRUE)");
        jdbcTemplate.execute("insert into services(id,businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (10,2, 'Uñas capping6', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'calle123', 'Belleza', 60, 'Por hora', '10000', TRUE)");

        jdbcTemplate.execute("INSERT INTO nbservices (serviceid, neighbourhood) values (1,'Palermo')");
        jdbcTemplate.execute("Insert into nbservices (serviceid, neighbourhood) values (1,'Almagro')");
        jdbcTemplate.execute("INSERT INTO nbservices (serviceid, neighbourhood) values (2,'Barracas')");
        jdbcTemplate.execute("INSERT INTO nbservices (serviceid, neighbourhood) values (3,'Belgrano')");
        jdbcTemplate.execute("INSERT INTO nbservices (serviceid, neighbourhood) values (4,'Boedo')");
        jdbcTemplate.execute("INSERT INTO nbservices (serviceid, neighbourhood) values (5,'Caballito')");
        jdbcTemplate.execute("INSERT INTO nbservices (serviceid, neighbourhood) values (6,'Caballito')");
        jdbcTemplate.execute("INSERT INTO nbservices (serviceid, neighbourhood) values (7,'Almagro')");
        jdbcTemplate.execute("INSERT INTO nbservices (serviceid, neighbourhood) values (8,'Palermo')");
        jdbcTemplate.execute("INSERT INTO nbservices (serviceid, neighbourhood) values (9,'Palermo')");
        jdbcTemplate.execute("INSERT INTO nbservices (serviceid, neighbourhood) values (10,'Palermo')");

    }
 }
