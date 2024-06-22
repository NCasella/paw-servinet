 package ar.edu.itba.paw.persistance;


 import ar.edu.itba.paw.model.*;
 import ar.edu.itba.paw.persistance.config.TestConfig;
 import org.junit.Assert;
 import org.junit.Before;
 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.test.annotation.Rollback;
 import org.springframework.test.context.ContextConfiguration;
 import org.springframework.test.context.jdbc.Sql;
 import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
 import org.springframework.transaction.annotation.Transactional;

 import javax.persistence.EntityManager;
 import javax.persistence.PersistenceContext;
 import java.math.BigInteger;
 import java.util.List;

 @Sql("classpath:sql/schema.sql")
 @Transactional
 @Rollback
 @RunWith(SpringJUnit4ClassRunner.class)
 @ContextConfiguration(classes = TestConfig.class)
 public class ServiceDaoJpaTest {
     private static final String NAME = "name";
     private static final long USERID=1;
     private static final long SERVICEID=1;
     private static final long BUSINESSID = 1;
     private static final String DESCRIPTION = "description";
     private static final Boolean HOMESERVICE = true;
     private static final String LOCATION = "calle 123";
     private static final Neighbourhoods[] NEIGHBOURHOODS = {Neighbourhoods.PALERMO};
     private static final int TOTAL_AMOUNT=10;
     private static final int CAPPIN_FILTERED_AMOUNT=6;
     private static final Categories CATEGORY = Categories.BELLEZA;
     private static final int DURATION = 30;
     private static final String PRICE = "ARS 1000";
     private static final Boolean ADDITIONALCHARGES = false;
     private static final PricingTypes PRICING = PricingTypes.PER_TOTAL;
     private  Business BUSINESS ;

     @PersistenceContext
     private EntityManager em;
    @Autowired
    private ServiceDaoJpa serviceDao;

    @Before
    public void setup(){
        em.createNativeQuery("INSERT INTO users (userid, username, password, name, surname, email, telephone,isprovider) VALUES (1, 'username', 'password', 'name', 'surname', 'email', 'telephone',false)").executeUpdate();
        em.createNativeQuery("INSERT INTO business(businessid, userid, businessname, businessTelephone, businessEmail, businessLocation) VALUES (1, 1, 'businessname', 'businessTelephone', 'businessEmail', 'businessLocation')").executeUpdate();
        BUSINESS=em.find(Business.class,BUSINESSID);
    }

    @Test
     public void testCreate() {
        Service service = serviceDao.create(BUSINESS, NAME, DESCRIPTION, HOMESERVICE, LOCATION, NEIGHBOURHOODS,CATEGORY, DURATION, PRICING, PRICE, ADDITIONALCHARGES,null);
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
        Assert.assertEquals(1, ((BigInteger)em.createNativeQuery("select count(*) from services").getSingleResult()).intValue());
    }

    @Test
     public void testFindById() {
        em.createNativeQuery(String.format("INSERT INTO services (id, businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges, imageId) VALUES (%d, %d, '%s', '%s', true, '%s', 'Belleza', 30, 'Total', '%s', false, null);", SERVICEID,BUSINESSID, NAME, DESCRIPTION, LOCATION,PRICE)).executeUpdate();
        em.createNativeQuery(String.format("INSERT INTO nbservices (insertid,serviceid, neighbourhood) VALUES (1,%d, '%s');", SERVICEID, NEIGHBOURHOODS[0].getValue())).executeUpdate();
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
       em.createNativeQuery(String.format("INSERT INTO services (id, businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges, imageId) VALUES (%d, %d, '%s', '%s', true, '%s', 'Belleza', 30, 'Total', '%s', false, null);", SERVICEID,BUSINESSID, NAME, DESCRIPTION, LOCATION,PRICE)).executeUpdate();
       em.flush();
       serviceDao.delete(SERVICEID);
       em.flush();
        Assert.assertEquals(0, ((BigInteger)em.createNativeQuery("select count(*) from services").getSingleResult()).intValue());
    }

    @Test
    public void testWithQueryAndCategory(){
        Populate();

        List<Service> services=serviceDao.getServicesFilteredBy(1,CATEGORY.getValue(), null,0,"capping");
        Assert.assertEquals(CAPPIN_FILTERED_AMOUNT,services.size());
    }
    @Test
     public void testUnFiltered(){
        Populate();

        List<Service> services =serviceDao.getServicesFilteredBy(1,null,null,0,null);


        Assert.assertEquals(TOTAL_AMOUNT,services.size());
    }

    private void Populate(){
        em.createNativeQuery("INSERT INTO users (userid, username, password, name, surname, email, telephone,isprovider) VALUES (2, 'username2', 'password2', 'name2', 'surname2', 'email2', 'telephone2',false)").executeUpdate();
        em.createNativeQuery("insert into business (businessid,userid, businessname, businessTelephone, businessEmail, businessLocation) values (2,2, 'Sol nails shop', '11365335', 'mailfalso@gmail.com', 'Palermo')").executeUpdate();
        em.createNativeQuery("insert into services(id,businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (1,2, 'Uñas capping', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'Palermo', 'Belleza', 60, 'Por hora', '10000', TRUE)").executeUpdate();
        em.createNativeQuery("insert into services(id,businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (2,2, 'Uñas francecita', 'Servicio de uñas francesitas.', FALSE, 'Palermo', 'Belleza', 60, 'Por hora', '5000', TRUE)").executeUpdate();
        em.createNativeQuery("insert into services(id,businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (3,2, 'Limpieza de cuticula', 'Servicio de uñas: limpieza de cuticula express.', FALSE, 'Palermo', 'Belleza', 60, 'Por hora', '3000', TRUE)").executeUpdate();
        em.createNativeQuery("insert into services(id,businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (4,2, 'Ya no se que otro servicio inventar de uñas', 'Servicio de uñas: necesito que este texto sea largo. Chatgpt: La vida es un viaje lleno de sorpresas y aventuras. Cada día es una oportunidad para explorar, aprender y crecer. .', TRUE, 'Palermo', 'Belleza', 60, 'Por hora', '3000', FALSE)").executeUpdate();
        em.createNativeQuery("insert into services(id,businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (5,2, 'Uñas capping1', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'calle123', 'Belleza', 60, 'Por hora', '10000', TRUE)").executeUpdate();
        em.createNativeQuery("insert into services(id,businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (6,2, 'Uñas capping2', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'calle123', 'Limpieza', 60, 'Por hora', '10000', TRUE)").executeUpdate();
        em.createNativeQuery("insert into services(id,businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (7,2, 'Uñas capping3', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'calle123', 'Belleza', 60, 'Por hora', '10000', TRUE)").executeUpdate();
        em.createNativeQuery("insert into services(id,businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (8,2, 'Uñas capping4', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'calle123', 'Belleza', 60, 'Por hora', '10000', TRUE)").executeUpdate();
        em.createNativeQuery("insert into services(id,businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (9,2, 'Uñas capping5', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'calle123', 'Belleza', 60, 'Por hora', '10000', TRUE)").executeUpdate();
        em.createNativeQuery("insert into services(id,businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges) values (10,2, 'Uñas capping6', 'Servicio de uñas, multiples colores y esmaltes de todo tipo. Diseño a eleccion del cliente. Arte en uñas. Consulte por disponibilidad.', FALSE, 'calle123', 'Belleza', 60, 'Por hora', '10000', TRUE)").executeUpdate();

        em.createNativeQuery("INSERT INTO nbservices (insertid,serviceid, neighbourhood) values (1,1,'Palermo')").executeUpdate();
        em.createNativeQuery("Insert into nbservices (insertid,serviceid, neighbourhood) values (2,1,'Almagro')").executeUpdate();
        em.createNativeQuery("INSERT INTO nbservices (insertid,serviceid, neighbourhood) values (3,2,'Barracas')").executeUpdate();
        em.createNativeQuery("INSERT INTO nbservices (insertid,serviceid, neighbourhood) values (4,3,'Belgrano')").executeUpdate();
        em.createNativeQuery("INSERT INTO nbservices (insertid,serviceid, neighbourhood) values (5,4,'Boedo')").executeUpdate();
        em.createNativeQuery("INSERT INTO nbservices (insertid,serviceid, neighbourhood) values (6,5,'Caballito')").executeUpdate();
        em.createNativeQuery("INSERT INTO nbservices (insertid,serviceid, neighbourhood) values (7,6,'Caballito')").executeUpdate();
        em.createNativeQuery("INSERT INTO nbservices (insertid,serviceid, neighbourhood) values (8,7,'Almagro')").executeUpdate();
        em.createNativeQuery("INSERT INTO nbservices (insertid,serviceid, neighbourhood) values (9,8,'Palermo')").executeUpdate();
        em.createNativeQuery("INSERT INTO nbservices (insertid,serviceid, neighbourhood) values (10,9,'Palermo')").executeUpdate();
        em.createNativeQuery("INSERT INTO nbservices (insertid,serviceid, neighbourhood) values (11,10,'Palermo')").executeUpdate();

    }
 }
