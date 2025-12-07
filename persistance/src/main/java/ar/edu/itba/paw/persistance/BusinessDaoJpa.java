package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Business;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.services.BusinessDao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class BusinessDaoJpa implements BusinessDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Business> findById(long id) {
        return Optional.ofNullable(em.find(Business.class, id));
    }

    @Override
    public Optional<Business> findByBusinessName(String businessName) {
        return em.createQuery("from Business as b where b.businessName = :businessName", Business.class)
                .setParameter("businessName", businessName)
                .getResultList().stream().findFirst();
    }

    @Override
    public List<Business> getAllBusinesses(int page, int pageSize) {
        Query nativeQuery = em.createNativeQuery("SELECT b.businessid FROM business b");

        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setFirstResult((page - 1) * pageSize);

        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) nativeQuery.getResultList()
                .stream()
                .map(n -> ((Number) n).longValue())
                .collect(Collectors.toList());

        if (ids.isEmpty())
            return Collections.emptyList();

        TypedQuery<Business> query = em.createQuery(
                "FROM Business b WHERE b.businessid IN :ids",
                Business.class
        );
        query.setParameter("ids", ids);

        return query.getResultList();
    }

    @Override
    public List<Business> getBusinessesByUser(long userId, int page, int pageSize) {
        Query nativeQuery = em.createNativeQuery(
                "SELECT b.businessid FROM business b WHERE b.userid = :userId"
        );
        nativeQuery.setParameter("userId", userId);

        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setFirstResult((page - 1) * pageSize);

        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) nativeQuery.getResultList()
                .stream()
                .map(n -> ((Number) n).longValue())
                .collect(Collectors.toList());

        if (ids.isEmpty())
            return Collections.emptyList();

        TypedQuery<Business> query = em.createQuery(
                "FROM Business b WHERE b.businessid IN :ids",
                Business.class
        );
        query.setParameter("ids", ids);

        return query.getResultList();
    }

    @Override
    public int getBusinessesCount() {
        final Query query= em.createNativeQuery("select count(b.businessid) from business b");
        return ((Number)query.getSingleResult()).intValue();

    }

    @Override
    public int getBusinessesCountByUser(long userId) {
        final Query query= em.createNativeQuery("select count(b.businessid) from business b where b.userid = :userId")
                .setParameter("userId", userId);
        return ((Number)query.getSingleResult()).intValue();
    }

    @Override
    public Optional<String> getBusinessEmail(long businessId) {
        return Optional.ofNullable(em.find(Business.class, businessId).getEmail());
    }

    @Override
    public boolean deleteBusiness(long businessId) {
        final Business business = em.find(Business.class, businessId);
        if (business != null) {
            em.remove(business);
            return !business.getOwnedBy().getBusinessOwned().isEmpty();
        }
        return false;
    }
    private void changeField(final String field, long businessId, String value) {
        final Business business = em.find(Business.class, businessId);
        if (business != null) {
            switch (field) {
                case "businessemail":
                    business.setEmail(value);
                    break;
                case "businesstelephone":
                    business.setTelephone(value);
                    break;
                case "businesslocation":
                    business.setLocation(value);
                    break;
            }
            em.persist(business);
        }
    }

    @Override
    public void changeBusinessEmail(long businessId, String value) {
        changeField("businessemail", businessId, value);
    }
    @Override
    public void changeBusinessTelephone(long businessId,String value){
        changeField("businesstelephone",businessId,value);
    }
    @Override
    public void changeBusinessLocation(long businessId,String value){
        changeField("businesslocation", businessId, value);
    }

    @Override
    public Business createBusiness(String businessName, long userId, String telephone, String email, String location) {
        final Business business = new Business(businessName, em.find(User.class, userId), telephone, email, location);
        em.persist(business);
        return business;
    }

}
