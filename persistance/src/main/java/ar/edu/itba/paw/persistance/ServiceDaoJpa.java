package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.ServiceNotFoundException;
import ar.edu.itba.paw.services.ServiceDao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;
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
    public boolean isServiceOwner(long serviceId,long userId){
        Query query = em.createQuery("select count(s) from Service as s where s.business.ownedBy.userId = :userId and s.id= :serviceId");
        query.setParameter("serviceId",serviceId);
       query.setParameter("userId",userId);
       return ((Number)query.getSingleResult()).intValue()>0;
    }
    @Override
    public Service create(Business business, String name, String description, boolean homeservice, String location, Neighbourhoods[] neighbourhoods, Categories category, int minimalduration, PricingTypes pricing, String price, boolean additionalCharges, Long imageId) {
        Service service = new Service(business, name, description, homeservice, location, category, minimalduration, pricing, price, additionalCharges, imageId);
        em.persist(service);
        for (Neighbourhoods n : neighbourhoods) {
            Nbservices nbservices = new Nbservices(service, n);
            em.persist(nbservices);
        }
        return service;
    }

    @Override
    public List<ServiceContactInfo> getServicesContactInfo(Collection<Long> serviceids){
        TypedQuery<ServiceContactInfo> query = em.createQuery("select new ar.edu.itba.paw.model.ServiceContactInfo(s.id, s.name, b.email, b.telephone) from Service as s JOIN s.business as b where s.id in :serviceids", ServiceContactInfo.class);
        query.setParameter("serviceids", new ArrayList<>(serviceids));
        return query.getResultList();
    }

    @Override
    public List<Service> getAllServices() {
        TypedQuery<Service> query = em.createQuery("from Service", Service.class);
        return query.getResultList();
    }

    @Override
    public List<Service> getAllBusinessServices(long businessId) {
        TypedQuery<Service> query = em.createQuery("from Service as s where s.business.businessid = :businessId", Service.class);
        query.setParameter("businessId", businessId);
        return query.getResultList();
    }

    @Override
    public List<BasicService> getAllBusinessBasicServices(long businessId) {
        TypedQuery<BasicService> query = em.createQuery("from Service as s WHERE s.business.businessid = :businessId", BasicService.class);
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
    public List<Service> getServicesFilteredBy(int page, Categories category, Neighbourhoods[] neighbourhoods, Integer rating, String searchQuery, ServicesOrderFilters orderFilter, Boolean homeServiceFilter, Long businessId) {
        FilterArgument filterArgument = new FilterArgument()
                .addCategory(category)
                .addLocation(neighbourhoods)
                .addSearch(searchQuery)
                .addPage(page)
                .addRating(rating)
                .addOrder(orderFilter)
                .addHomeServiceFilter(homeServiceFilter)
                .addBusinessId(businessId);
        Query nativeQuery;
        if(orderFilter!=null)
            nativeQuery = em.createNativeQuery("select s.id from services s full outer join ratings r on s.id = r.serviceid where "+filterArgument.formSqlSentence());
        else
            nativeQuery = em.createNativeQuery("select s.id from services s where "+filterArgument.formSqlSentence());
        filterArgument.setQueryParams(nativeQuery);

        nativeQuery.setMaxResults(filterArgument.getPageSize());
        nativeQuery.setFirstResult((page-1)*filterArgument.getPageSize());

        final List<Long> idList = (List<Long>) nativeQuery.getResultList()
                .stream().map(n -> ((Number)n).longValue()).collect(Collectors.toList());

        final TypedQuery<Service> query;

        query = em.createQuery(
                "from Service as s where id in :ids",
                Service.class
        );

        query.setParameter("ids",idList);

        List<Service> services = query.getResultList();

        Map<Long, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < idList.size(); i++) {
            orderMap.put(idList.get(i), i);
        }

        services.sort(Comparator.comparingInt(s -> orderMap.get(s.getId())));

        return services;

    }


    @Override
    public int getServiceCount(Categories category, Neighbourhoods[] neighbourhoods, Integer rating, String searchQuery,Boolean homeServiceFilter, Long businessId) {
        FilterArgument filterArgument = new FilterArgument()
                .addCategory(category)
                .addLocation(neighbourhoods)
                .addSearch(searchQuery)
                .addRating(rating)
                .addHomeServiceFilter(homeServiceFilter)
                .addBusinessId(businessId);
        final Query query= em.createNativeQuery("select count(s.id) from services s where "+filterArgument.formSqlSentence());
        filterArgument.setQueryParams(query);
        return ((Number)query.getSingleResult()).intValue();

    }

    @Override
    public List<Service> getRecommendedServices() {
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

    @Override
    public List<String> getAvailableNeighbourhoods() {
        TypedQuery<String> query = em.createQuery("SELECT DISTINCT nb.neighbourhood FROM Service s JOIN s.neighbourhoodAvailable nb", String.class);
        return query.getResultList();
    }

    @Override
    public List<String> getAvailableNeighbourhoodsByCategory(Categories category) {
        TypedQuery<String> query = em.createQuery("SELECT DISTINCT nb.neighbourhood FROM Service s JOIN s.neighbourhoodAvailable nb WHERE s.category = :category", String.class);
        query.setParameter("category", category.getValue());
        return query.getResultList();
    }
}
