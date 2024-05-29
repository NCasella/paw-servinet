package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.ServiceNotFoundException;
import ar.edu.itba.paw.services.ServiceDao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ServiceDaoJpa implements ServiceDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Service> findById(long id) {
        return Optional.ofNullable(em.find(Service.class, id));
    }

    @Override
    public Optional<BasicService> findBasicServiceById(long id) {
        TypedQuery<BasicService> query = em.createQuery("from Service as s where s.id = :id", BasicService.class);
        query.setParameter("id", id);
        final List<BasicService> list = query.getResultList();
        return list.stream().findFirst();
    }

    @Override
    public Service create(long businessid, String name, String description, boolean homeservice, String location, Neighbourhoods[] neighbourhoods, Categories category, int minimalduration, PricingTypes pricing, String price, boolean additionalCharges, Long imageId) {
        Service service = new Service(businessid, name, description, homeservice, location, category, minimalduration, pricing, price, additionalCharges, imageId);
        em.persist(service);
        for (Neighbourhoods n : neighbourhoods) {
            Nbservices nbservices = new Nbservices(service, n);
            em.persist(nbservices);
        }
        return service;
    }

    @Override
    public List<Service> getAllServices() {
        TypedQuery<Service> query = em.createQuery("from Service", Service.class);
        return query.getResultList();
    }

    @Override
    public List<Service> getAllBusinessServices(long businessId) {
        TypedQuery<Service> query = em.createQuery("from Service as s where s.businessid = :businessId", Service.class);
        query.setParameter("businessId", businessId);
        return query.getResultList();
    }

    @Override
    public List<BasicService> getAllBusinessBasicServices(long businessId) {
        TypedQuery<BasicService> query = em.createQuery("from Service as s WHERE s.businessid = :businessId", BasicService.class);
        query.setParameter("businessId", businessId);
        return query.getResultList();
    }

    @Override
    public Service editServiceName(long serviceid, String newvalue) {
        Service service = findById(serviceid).orElseThrow(ServiceNotFoundException::new);
        service.setName(newvalue);
        em.merge(service);
        return service;
    }

    @Override
    public void delete(long serviceid) {
        Service service = findById(serviceid).orElseThrow(ServiceNotFoundException::new);
        em.remove(service);
    }

    @Override
    public List<Service> getServices(int page) {
        // TODO implementar 1+1
        TypedQuery<Service> query = em.createQuery("from Service s order by s.id ASC", Service.class);
        query.setFirstResult(page * 10);
        query.setMaxResults(10);
        return query.getResultList();
    }

    @Override
    public List<Service> getServicesFilteredBy(int page, String category, String[] location, int rating, String searchQuery) {
        FilterArgument filterArgument = new FilterArgument().addCategory(category).addLocation(location).addSearch(searchQuery).addPage(page).addRating(rating);
        Query nativeQuery =em.createNativeQuery("select id from services");
        nativeQuery.setMaxResults(filterArgument.getPageSize());
        nativeQuery.setFirstResult((page)*filterArgument.getPageSize());

        final List<Long> idList = (List<Long>) nativeQuery.getResultList()
                .stream().map(n -> ((Number)n).longValue()).collect(Collectors.toList());

        final TypedQuery<Service> query= em.createQuery("from Service as s where id in :ids and "+filterArgument.formSqlSentence(), Service.class);
        query.setParameter("ids",idList);
        filterArgument.setQueryParams(query);
        return  query.getResultList();
    }


    @Override
    public int getServiceCount(String category, String[] location, int rating, String searchQuery) {
        FilterArgument filterArgument = new FilterArgument().addCategory(category).addLocation(location).addSearch(searchQuery).addRating(rating);
        final TypedQuery<Service> query= em.createQuery("from Service as s where "+filterArgument.formSqlSentence(), Service.class);
        filterArgument.setQueryParams(query);
        return  query.getResultList().size();

    }

    @Override
    public List<Service> getRecommendedServices() {
        // TODO implementar 1+1
        TypedQuery<Service> query = em.createQuery("from Service as s order by s.id desc", Service.class);
        query.setMaxResults(10);
        return query.getResultList();
    }

    @Override
    public void editService(long serviceId, String newDescription, int newDuration, PricingTypes newPricingType, String newPrice, boolean newAdditionalCharges) {
        Service service = findById(serviceId).orElseThrow(ServiceNotFoundException::new);
        service.setDescription(newDescription);
        service.setDuration(newDuration);
        service.setPricing(newPricingType.getValue());
        service.setPrice(newPrice);
        service.setAdditionalCharges(newAdditionalCharges);
        em.merge(service);
    }
}
