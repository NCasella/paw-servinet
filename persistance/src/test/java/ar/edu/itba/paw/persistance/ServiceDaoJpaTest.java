package ar.edu.itba.paw.persistance;


import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.AppointmentNonExistentException;
import ar.edu.itba.paw.model.exceptions.ServiceNotFoundException;
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
import java.util.*;

@Sql("classpath:sql/schema.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ServiceDaoJpaTest {
    private static final String NAME = "name";
    private static final long USER_ID = 1;
    private static final long ANOTHER_USER_ID = 2;
    private static final long SERVICE_ID = 1;
    private static final long ANOTHER_SERVICE_ID = 2;
    private static final long BUSINESS_ID = 1;
    private static final long ANOTHER_BUSINESS_ID = 2;
    private static final String DESCRIPTION = "description";
    private static final Boolean HOME_SERVICE = true;
    private static final String LOCATION = "calle 123";
    private static final Neighbourhoods[] NEIGHBOURHOODS = {Neighbourhoods.PALERMO};
    private static final Categories CATEGORY = Categories.BELLEZA;
    private static final int DURATION = 30;
    private static final String PRICE = "ARS 1000";
    private static final Boolean ADDITIONAL_CHARGES = false;
    private static final PricingTypes PRICING = PricingTypes.PER_TOTAL;
    private Business BUSINESS;

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private ServiceDaoJpa serviceDao;

    @Before
    public void setup() {
        String userSql = """
            INSERT INTO users (userid, username, password, name, surname, email, telephone, isprovider, isverified, profilepic)
            VALUES (:id, 'username', 'password', 'name', 'surname', 'email', 'telephone', false, true, 1)
            """;
        em.createNativeQuery(userSql)
            .setParameter("id", USER_ID)
            .executeUpdate();

        String businessSql = """
            INSERT INTO business (businessid, userid, businessname, businessTelephone, businessEmail, businessLocation)
            VALUES (:id, :userId, 'businessname', 'businessTelephone', 'businessEmail', 'businessLocation')
            """;
        em.createNativeQuery(businessSql)
            .setParameter("id", BUSINESS_ID)
            .setParameter("userId", USER_ID)
            .executeUpdate();

        BUSINESS = em.find(Business.class, BUSINESS_ID);
    }

    private void insertService(long id, long businessId, String name, String description, boolean homeService, String location, String category, String price) {
        String sql = """
            INSERT INTO services (id, businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges, imageId)
            VALUES (:id, :businessId, :name, :description, :homeService, :location, :category, 30, 'Total', :price, false, null)
            """;
        em.createNativeQuery(sql)
            .setParameter("id", id)
            .setParameter("businessId", businessId)
            .setParameter("name", name)
            .setParameter("description", description)
            .setParameter("homeService", homeService)
            .setParameter("location", location)
            .setParameter("category", category)
            .setParameter("price", price)
            .executeUpdate();
    }

    private void insertDefaultService(long id, long businessId, String name) {
        insertService(id, businessId, name, DESCRIPTION, true, LOCATION, "Belleza", PRICE);
    }

    private void insertUser(long id, String username, String email) {
        String sql = """
            INSERT INTO users (userid, username, password, name, surname, email, telephone, isprovider, isverified, profilepic)
            VALUES (:id, :username, 'password', 'name', 'surname', :email, 'telephone', false, true, 1)
            """;
        em.createNativeQuery(sql)
            .setParameter("id", id)
            .setParameter("username", username)
            .setParameter("email", email)
            .executeUpdate();
    }

    private void insertBusiness(long id, long userId, String name) {
        String sql = """
            INSERT INTO business (businessid, userid, businessname, businessTelephone, businessEmail, businessLocation)
            VALUES (:id, :userId, :name, 'telephone', 'email', 'location')
            """;
        em.createNativeQuery(sql)
            .setParameter("id", id)
            .setParameter("userId", userId)
            .setParameter("name", name)
            .executeUpdate();
    }

    @Test
    public void testCreate() {
        Service service = serviceDao.create(BUSINESS, NAME, DESCRIPTION, HOME_SERVICE, LOCATION, NEIGHBOURHOODS, CATEGORY, DURATION, PRICING, PRICE, ADDITIONAL_CHARGES, null);
        em.flush();
        em.clear();

        Assert.assertNotNull("A new service SHOULD be returned after creation.",
                service);

        Service persisted = em.find(Service.class, service.getId());
        Assert.assertNotNull("The created service SHOULD be persisted in the database.",
                persisted);
        Assert.assertEquals("The service SHOULD belong to the specified business.",
                BUSINESS_ID, persisted.getBusinessid());
        Assert.assertEquals("The service name SHOULD be preserved after persistence.",
                NAME, persisted.getName());
        Assert.assertEquals("The service description SHOULD be preserved after persistence.",
                DESCRIPTION, persisted.getDescription());
        Assert.assertEquals("The home service option SHOULD be preserved after persistence.",
                HOME_SERVICE, persisted.getHomeService());
        Assert.assertEquals("The service location SHOULD be preserved after persistence.",
                LOCATION, persisted.getLocation());
        Assert.assertEquals("The service category SHOULD be preserved after persistence.",
                CATEGORY, persisted.getCategory());
        Assert.assertEquals("The service duration SHOULD be preserved after persistence.",
                DURATION, persisted.getDuration());
        Assert.assertEquals("The pricing type SHOULD be preserved after persistence.",
                PRICING, persisted.getPricing());
        Assert.assertEquals("The service price SHOULD be preserved after persistence.",
                PRICE, persisted.getPrice());
        Assert.assertEquals("The additional charges flag SHOULD be preserved after persistence.",
                ADDITIONAL_CHARGES, persisted.getAdditionalCharges());

        long count = em.createQuery("SELECT count(s) FROM Service s", Long.class).getSingleResult();
        Assert.assertEquals("Exactly one service SHOULD exist after a single creation.",
                1L, count);
    }

    @Test
    public void testFindById() {
        insertDefaultService(SERVICE_ID, BUSINESS_ID, NAME);
        em.createNativeQuery("INSERT INTO nbservices (insertid, serviceid, neighbourhood) VALUES (?, ?, ?)")
                .setParameter(1, 1)
                .setParameter(2, SERVICE_ID)
                .setParameter(3, NEIGHBOURHOODS[0].getValue())
                .executeUpdate();
        em.flush();
        em.clear();

        Service service = serviceDao.findById(SERVICE_ID).get();

        Assert.assertNotNull("An existing service SHOULD be retrievable by its ID.",
                service);
        Assert.assertEquals("The retrieved service SHOULD have the correct ID.",
                SERVICE_ID, service.getId());
        Assert.assertEquals("The service SHOULD belong to the correct business.",
                BUSINESS_ID, service.getBusinessid());
        Assert.assertEquals("The service name SHOULD be preserved in the database.",
                NAME, service.getName());
        Assert.assertEquals("The service description SHOULD be preserved in the database.",
                DESCRIPTION, service.getDescription());
        Assert.assertEquals("The home service option SHOULD be preserved in the database.",
                HOME_SERVICE, service.getHomeService());
        Assert.assertEquals("The service location SHOULD be preserved in the database.",
                LOCATION, service.getLocation());
        Assert.assertEquals("The service category SHOULD be preserved in the database.",
                CATEGORY.getValue(), service.getCategory().getValue());
        Assert.assertEquals("The service duration SHOULD be preserved in the database.",
                DURATION, service.getDuration());
        Assert.assertEquals("The pricing type SHOULD be preserved in the database.",
                PRICING, service.getPricing());
        Assert.assertEquals("The service price SHOULD be preserved in the database.",
                PRICE, service.getPrice());
        Assert.assertEquals("The additional charges flag SHOULD be preserved in the database.",
                ADDITIONAL_CHARGES, service.getAdditionalCharges());
    }

    @Test
    public void testFindByIdNotFound() {
        Optional<Service> result = serviceDao.findById(999);

        Assert.assertFalse("A non-existent service SHOULD NOT be retrievable.",
                result.isPresent());
    }

    @Test
    public void testDelete() {
        insertDefaultService(SERVICE_ID, BUSINESS_ID, NAME);
        em.flush();
        em.clear();

        serviceDao.delete(SERVICE_ID);
        em.flush();
        em.clear();

        long count = em.createQuery("SELECT count(s) FROM Service s", Long.class).getSingleResult();
        Assert.assertEquals("No services SHOULD remain after deleting the only one.",
                0L, count);
    }

    @Test
    public void testDeleteNotFound() {
        long nonExistentId = 999L;

        Assert.assertThrows(ServiceNotFoundException.class, () -> {
            serviceDao.delete(nonExistentId);
        });
    }

    @Test
    public void testWithQueryAndCategory() {
        populate();
        em.flush();
        em.clear();

        List<Service> services = serviceDao.getServicesFilteredBy(1, CATEGORY, null, 0, "capping", null, null, null);

        Assert.assertEquals("Only services matching both category and search query SHOULD be returned.",
                6, services.size());
    }

    @Test
    public void testUnFiltered() {
        populate();
        em.flush();
        em.clear();

        List<Service> services = serviceDao.getServicesFilteredBy(1, null, null, 0, null, null, null, null);

        Assert.assertEquals("All services SHOULD be returned when no filters are applied.",
                10, services.size());
    }

    @Test
    public void testFindBasicServiceById() {
        insertDefaultService(SERVICE_ID, BUSINESS_ID, NAME);
        em.flush();
        em.clear();

        Optional<BasicService> result = serviceDao.findBasicServiceById(SERVICE_ID);

        Assert.assertTrue("An existing service SHOULD be retrievable as BasicService.",
                result.isPresent());
        Assert.assertEquals("The basic service SHOULD have the correct ID.",
                SERVICE_ID, result.get().getId());
        Assert.assertEquals("The basic service name SHOULD be preserved.",
                NAME, result.get().getName());
    }

    @Test
    public void testFindBasicServiceByIdNotFound() {
        Optional<BasicService> result = serviceDao.findBasicServiceById(999);

        Assert.assertFalse("A non-existent service SHOULD NOT be retrievable.",
                result.isPresent());
    }

    @Test
    public void testIsServiceOwnerTrue() {
        insertDefaultService(SERVICE_ID, BUSINESS_ID, NAME);
        em.flush();
        em.clear();

        boolean isOwner = serviceDao.isServiceOwner(SERVICE_ID, USER_ID);

        Assert.assertTrue("The business owner SHOULD be recognized as the service owner.",
                isOwner);
    }

    @Test
    public void testIsServiceOwnerFalse() {
        insertUser(ANOTHER_USER_ID, "username2", "email2");
        insertDefaultService(SERVICE_ID, BUSINESS_ID, NAME);
        em.flush();
        em.clear();

        boolean isOwner = serviceDao.isServiceOwner(SERVICE_ID, ANOTHER_USER_ID);

        Assert.assertFalse("A user who does not own the business SHOULD NOT be recognized as service owner.",
                isOwner);
    }

    @Test
    public void testIsServiceOwnerServiceNotExist() {
        boolean isOwner = serviceDao.isServiceOwner(999, USER_ID);

        Assert.assertFalse("Ownership check for non-existent service SHOULD return false.",
                isOwner);
    }

    @Test
    public void testGetServicesContactInfoSingle() {
        insertDefaultService(SERVICE_ID, BUSINESS_ID, NAME);
        em.flush();
        em.clear();

        List<ServiceContactInfo> result = serviceDao.getServicesContactInfo(Collections.singletonList(SERVICE_ID));

        Assert.assertNotNull("Contact info list SHOULD never be null.",
                result);
        Assert.assertEquals("Contact info SHOULD be returned for each requested service.",
                1, result.size());
        Assert.assertEquals("The contact info SHOULD reference the correct service.",
                SERVICE_ID, result.get(0).getServiceId());
        Assert.assertEquals("The service name in contact info SHOULD be preserved.",
                NAME, result.get(0).getServiceName());
    }

    @Test
    public void testGetServicesContactInfoMultiple() {
        insertDefaultService(SERVICE_ID, BUSINESS_ID, NAME);
        insertDefaultService(ANOTHER_SERVICE_ID, BUSINESS_ID, "name2");
        em.flush();
        em.clear();

        List<ServiceContactInfo> result = serviceDao.getServicesContactInfo(Arrays.asList(SERVICE_ID, ANOTHER_SERVICE_ID));

        Assert.assertNotNull("Contact info list SHOULD never be null.",
                result);
        Assert.assertEquals("Contact info SHOULD be returned for all requested services.",
                2, result.size());
    }

    @Test
    public void testGetAllServicesWithData() {
        insertDefaultService(SERVICE_ID, BUSINESS_ID, NAME);
        insertDefaultService(ANOTHER_SERVICE_ID, BUSINESS_ID, "name2");
        em.flush();
        em.clear();

        List<Service> result = serviceDao.getAllServices();

        Assert.assertNotNull("Services list SHOULD never be null.",
                result);
        Assert.assertEquals("All existing services SHOULD be returned.",
                2, result.size());
    }

    @Test
    public void testGetAllServicesEmpty() {
        List<Service> result = serviceDao.getAllServices();

        Assert.assertNotNull("Services list SHOULD never be null even when empty.",
                result);
        Assert.assertTrue("No services SHOULD be returned when none exist.",
                result.isEmpty());
    }

    @Test
    public void testGetAllBusinessServicesWithServices() {
        insertDefaultService(SERVICE_ID, BUSINESS_ID, NAME);
        insertDefaultService(ANOTHER_SERVICE_ID, BUSINESS_ID, "name2");
        em.flush();
        em.clear();

        List<Service> result = serviceDao.getAllBusinessServices(BUSINESS_ID);

        Assert.assertNotNull("Business services list SHOULD never be null.",
                result);
        Assert.assertEquals("All services belonging to the business SHOULD be returned.",
                2, result.size());
    }

    @Test
    public void testGetAllBusinessServicesEmpty() {
        List<Service> result = serviceDao.getAllBusinessServices(BUSINESS_ID);

        Assert.assertNotNull("Business services list SHOULD never be null even when empty.",
                result);
        Assert.assertTrue("No services SHOULD be returned for a business with none.",
                result.isEmpty());
    }

    @Test
    public void testGetAllBusinessServicesOtherBusinessExcluded() {
        insertUser(ANOTHER_USER_ID, "username2", "email2");
        insertBusiness(ANOTHER_BUSINESS_ID, ANOTHER_USER_ID, "businessname2");
        insertDefaultService(SERVICE_ID, ANOTHER_BUSINESS_ID, NAME);
        em.flush();
        em.clear();

        List<Service> result = serviceDao.getAllBusinessServices(BUSINESS_ID);

        Assert.assertNotNull("Business services list SHOULD never be null.",
                result);
        Assert.assertTrue("Services from other businesses SHOULD NOT be included.",
                result.isEmpty());
    }

    @Test
    public void testGetAllBusinessBasicServicesWithServices() {
        insertDefaultService(SERVICE_ID, BUSINESS_ID, NAME);
        em.flush();
        em.clear();

        List<BasicService> result = serviceDao.getAllBusinessBasicServices(BUSINESS_ID);

        Assert.assertNotNull("Business basic services list SHOULD never be null.",
                result);
        Assert.assertEquals("All services belonging to the business SHOULD be returned.",
                1, result.size());
        Assert.assertEquals("The service name SHOULD be preserved.",
                NAME, result.get(0).getName());
    }

    @Test
    public void testGetAllBusinessBasicServicesEmpty() {
        List<BasicService> result = serviceDao.getAllBusinessBasicServices(BUSINESS_ID);

        Assert.assertNotNull("Business basic services list SHOULD never be null even when empty.",
                result);
        Assert.assertTrue("No services SHOULD be returned for a business with none.",
                result.isEmpty());
    }

    @Test
    public void testEditServiceName() {
        insertDefaultService(SERVICE_ID, BUSINESS_ID, NAME);
        em.flush();
        em.clear();

        String newName = "Updated Name";
        Service result = serviceDao.editServiceName(SERVICE_ID, newName);
        em.flush();
        em.clear();

        Assert.assertNotNull("The updated service SHOULD be returned.",
                result);

        Service persisted = em.find(Service.class, SERVICE_ID);
        Assert.assertEquals("The service name SHOULD reflect the new value.",
                newName, persisted.getName());
    }

    @Test
    public void testEditServiceNameNotFound() {
        long nonExistentId = 999L;
        String newName = "New Name";

        Assert.assertThrows(ServiceNotFoundException.class, () -> {
            serviceDao.editServiceName(nonExistentId, newName);
        });
    }

    @Test
    public void testEditService() {
        insertDefaultService(SERVICE_ID, BUSINESS_ID, NAME);
        em.flush();
        em.clear();

        String newDescription = "Updated Description";
        int newDuration = 60;
        PricingTypes newPricing = PricingTypes.PER_HOUR;
        String newPrice = "ARS 2000";
        boolean newAdditionalCharges = true;

        serviceDao.editService(SERVICE_ID, newDescription, newDuration, newPricing, newPrice, newAdditionalCharges);
        em.flush();
        em.clear();

        Service updated = em.find(Service.class, SERVICE_ID);
        Assert.assertNotNull("The service SHOULD still exist after editing.",
                updated);
        Assert.assertEquals("The service description SHOULD reflect the new value.",
                newDescription, updated.getDescription());
        Assert.assertEquals("The service duration SHOULD reflect the new value.",
                newDuration, updated.getDuration());
        Assert.assertEquals("The pricing type SHOULD reflect the new value.",
                newPricing, updated.getPricing());
        Assert.assertEquals("The service price SHOULD reflect the new value.",
                newPrice, updated.getPrice());
        Assert.assertEquals("The additional charges flag SHOULD reflect the new value.",
                newAdditionalCharges, updated.getAdditionalCharges());
    }

    @Test
    public void testEditServiceNotFound() {
        long nonExistentId = 999L;

        Assert.assertThrows(ServiceNotFoundException.class, () -> {
            serviceDao.editService(nonExistentId, "desc", 30, PricingTypes.PER_TOTAL, "1000", false);
        });
    }

    @Test
    public void testGetServiceCountNoFilters() {
        insertDefaultService(SERVICE_ID, BUSINESS_ID, NAME);
        insertDefaultService(ANOTHER_SERVICE_ID, BUSINESS_ID, "name2");
        em.flush();
        em.clear();

        int count = serviceDao.getServiceCount(null, null, 0, null, null, null);

        Assert.assertEquals("The count SHOULD reflect all existing services.",
                2, count);
    }

    @Test
    public void testGetServiceCountWithCategoryFilter() {
        insertDefaultService(SERVICE_ID, BUSINESS_ID, NAME);
        insertService(ANOTHER_SERVICE_ID, BUSINESS_ID, "name2", DESCRIPTION, true, LOCATION, "Limpieza", PRICE);
        em.flush();
        em.clear();

        int count = serviceDao.getServiceCount(Categories.BELLEZA, null, 0, null, null, null);

        Assert.assertEquals("The count SHOULD reflect only services in the specified category.",
                1, count);
    }

    @Test
    public void testGetServiceCountEmpty() {
        int count = serviceDao.getServiceCount(null, null, 0, null, null, null);

        Assert.assertEquals("The count SHOULD be zero when no services exist.",
                0, count);
    }

    @Test
    public void testGetRecommendedServicesReturnsUpTo10() {
        for (int i = 1; i <= 12; i++) {
            insertDefaultService(i, BUSINESS_ID, "service" + i);
        }
        em.flush();
        em.clear();

        List<Service> result = serviceDao.getRecommendedServices();

        Assert.assertNotNull("Recommended services list SHOULD never be null.",
                result);
        Assert.assertEquals("At most 10 services SHOULD be recommended.",
                10, result.size());
    }

    @Test
    public void testGetRecommendedServicesEmpty() {
        List<Service> result = serviceDao.getRecommendedServices();

        Assert.assertNotNull("Recommended services list SHOULD never be null even when empty.",
                result);
        Assert.assertTrue("No services SHOULD be recommended when none exist.",
                result.isEmpty());
    }

    @Test
    public void testGetAvailableNeighbourhoods() {
        insertDefaultService(SERVICE_ID, BUSINESS_ID, NAME);
        em.createNativeQuery("INSERT INTO nbservices (insertid, serviceid, neighbourhood) VALUES (1, 1, 'Palermo')").executeUpdate();
        em.createNativeQuery("INSERT INTO nbservices (insertid, serviceid, neighbourhood) VALUES (2, 1, 'Belgrano')").executeUpdate();
        em.flush();
        em.clear();

        List<String> result = serviceDao.getAvailableNeighbourhoods();

        Assert.assertNotNull("Neighbourhoods list SHOULD never be null.",
                result);
        Assert.assertEquals("All distinct neighbourhoods with services SHOULD be returned.",
                2, result.size());
        Assert.assertTrue("Palermo SHOULD be in the available neighbourhoods.",
                result.contains("Palermo"));
        Assert.assertTrue("Belgrano SHOULD be in the available neighbourhoods.",
                result.contains("Belgrano"));
    }

    @Test
    public void testGetAvailableNeighbourhoodsEmpty() {
        List<String> result = serviceDao.getAvailableNeighbourhoods();

        Assert.assertNotNull("Neighbourhoods list SHOULD never be null even when empty.",
                result);
        Assert.assertTrue("No neighbourhoods SHOULD be returned when no services exist.",
                result.isEmpty());
    }

    @Test
    public void testGetAvailableNeighbourhoodsByCategory() {
        insertDefaultService(SERVICE_ID, BUSINESS_ID, NAME);
        insertService(ANOTHER_SERVICE_ID, BUSINESS_ID, "name2", DESCRIPTION, true, LOCATION, "Limpieza", PRICE);
        em.createNativeQuery("INSERT INTO nbservices (insertid, serviceid, neighbourhood) VALUES (1, 1, 'Palermo')").executeUpdate();
        em.createNativeQuery("INSERT INTO nbservices (insertid, serviceid, neighbourhood) VALUES (2, 2, 'Belgrano')").executeUpdate();
        em.flush();
        em.clear();

        List<String> result = serviceDao.getAvailableNeighbourhoodsByCategory(Categories.BELLEZA);

        Assert.assertNotNull("Neighbourhoods by category list SHOULD never be null.",
                result);
        Assert.assertEquals("Only neighbourhoods with services in the category SHOULD be returned.",
                1, result.size());
        Assert.assertTrue("Palermo SHOULD be available for Belleza category.",
                result.contains("Palermo"));
    }

    @Test
    public void testGetAvailableNeighbourhoodsByCategoryEmpty() {
        List<String> result = serviceDao.getAvailableNeighbourhoodsByCategory(Categories.BELLEZA);

        Assert.assertNotNull("Neighbourhoods by category list SHOULD never be null even when empty.",
                result);
        Assert.assertTrue("No neighbourhoods SHOULD be returned when no services exist in the category.",
                result.isEmpty());
    }

    @Test
    public void testGetServicesFilteredByHomeService() {
        insertService(SERVICE_ID, BUSINESS_ID, NAME, DESCRIPTION, true, LOCATION, "Belleza", PRICE);
        insertService(ANOTHER_SERVICE_ID, BUSINESS_ID, "name2", DESCRIPTION, false, LOCATION, "Belleza", PRICE);
        em.flush();
        em.clear();

        List<Service> result = serviceDao.getServicesFilteredBy(1, null, null, 0, null, null, true, null);

        Assert.assertNotNull("Filtered services list SHOULD never be null.",
                result);
        Assert.assertEquals("Only services offering home service SHOULD be returned.",
                1, result.size());
        Assert.assertTrue("The returned service SHOULD have home service enabled.",
                result.get(0).getHomeService());
    }

    @Test
    public void testGetServicesFilteredBySearchQuery() {
        insertDefaultService(SERVICE_ID, BUSINESS_ID, "unique");
        insertDefaultService(ANOTHER_SERVICE_ID, BUSINESS_ID, "other");
        em.flush();
        em.clear();

        List<Service> result = serviceDao.getServicesFilteredBy(1, null, null, 0, "unique", null, null, null);

        Assert.assertNotNull("Filtered services list SHOULD never be null.",
                result);
        Assert.assertEquals("Only services matching the search query SHOULD be returned.",
                1, result.size());
        Assert.assertEquals("The returned service SHOULD be the one matching the query.",
                SERVICE_ID, result.get(0).getId());
    }

    @Test
    public void testGetServicesFilteredByBusinessId() {
        insertUser(ANOTHER_USER_ID, "username2", "email2");
        insertBusiness(ANOTHER_BUSINESS_ID, ANOTHER_USER_ID, "businessname2");
        insertDefaultService(SERVICE_ID, BUSINESS_ID, NAME);
        insertDefaultService(ANOTHER_SERVICE_ID, ANOTHER_BUSINESS_ID, "name2");
        em.flush();
        em.clear();

        List<Service> result = serviceDao.getServicesFilteredBy(1, null, null, 0, null, null, null, BUSINESS_ID);

        Assert.assertNotNull("Filtered services list SHOULD never be null.",
                result);
        Assert.assertEquals("Only services from the specified business SHOULD be returned.",
                1, result.size());
        Assert.assertEquals("The returned service SHOULD belong to the filtered business.",
                BUSINESS_ID, result.get(0).getBusinessid());
    }

    private void populate() {
        insertUser(ANOTHER_USER_ID, "username2", "email2");
        insertBusiness(ANOTHER_BUSINESS_ID, ANOTHER_USER_ID, "Sol nails shop");

        insertPopulateService(1, "Unas capping", "Servicio de unas.", false, "Palermo", "Belleza", "10000", true);
        insertPopulateService(2, "Unas francecita", "Servicio de unas francesitas.", false, "Palermo", "Belleza", "5000", true);
        insertPopulateService(3, "Limpieza de cuticula", "Servicio de unas: limpieza de cuticula express.", false, "Palermo", "Belleza", "3000", true);
        insertPopulateService(4, "Ya no se que otro servicio inventar de unas", "Servicio de unas.", true, "Palermo", "Belleza", "3000", false);
        insertPopulateService(5, "Unas capping1", "Servicio de unas.", false, "calle123", "Belleza", "10000", true);
        insertPopulateService(6, "Unas capping2", "Servicio de unas.", false, "calle123", "Limpieza", "10000", true);
        insertPopulateService(7, "Unas capping3", "Servicio de unas.", false, "calle123", "Belleza", "10000", true);
        insertPopulateService(8, "Unas capping4", "Servicio de unas.", false, "calle123", "Belleza", "10000", true);
        insertPopulateService(9, "Unas capping5", "Servicio de unas.", false, "calle123", "Belleza", "10000", true);
        insertPopulateService(10, "Unas capping6", "Servicio de unas.", false, "calle123", "Belleza", "10000", true);

        insertNeighbourhood(1, 1, "Palermo");
        insertNeighbourhood(2, 1, "Almagro");
        insertNeighbourhood(3, 2, "Barracas");
        insertNeighbourhood(4, 3, "Belgrano");
        insertNeighbourhood(5, 4, "Boedo");
        insertNeighbourhood(6, 5, "Caballito");
        insertNeighbourhood(7, 6, "Caballito");
        insertNeighbourhood(8, 7, "Almagro");
        insertNeighbourhood(9, 8, "Palermo");
        insertNeighbourhood(10, 9, "Palermo");
        insertNeighbourhood(11, 10, "Palermo");
    }

    private void insertPopulateService(long id, String name, String description, boolean homeService, String location, String category, String price, boolean additionalCharges) {
        String sql = """
            INSERT INTO services (id, businessid, servicename, servicedescription, homeservice, location, category, minimalduration, pricingtype, price, additionalcharges)
            VALUES (:id, :businessId, :name, :description, :homeService, :location, :category, 60, 'Por hora', :price, :additionalCharges)
            """;
        em.createNativeQuery(sql)
            .setParameter("id", id)
            .setParameter("businessId", ANOTHER_BUSINESS_ID)
            .setParameter("name", name)
            .setParameter("description", description)
            .setParameter("homeService", homeService)
            .setParameter("location", location)
            .setParameter("category", category)
            .setParameter("price", price)
            .setParameter("additionalCharges", additionalCharges)
            .executeUpdate();
    }

    private void insertNeighbourhood(long insertId, long serviceId, String neighbourhood) {
        String sql = """
            INSERT INTO nbservices (insertid, serviceid, neighbourhood)
            VALUES (:insertId, :serviceId, :neighbourhood)
            """;
        em.createNativeQuery(sql)
            .setParameter("insertId", insertId)
            .setParameter("serviceId", serviceId)
            .setParameter("neighbourhood", neighbourhood)
            .executeUpdate();
    }
}
