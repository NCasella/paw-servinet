package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Rating;
import ar.edu.itba.paw.model.Service;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.services.RatingDao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

@Repository
public class RatingDaoJpa implements RatingDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Rating> getAllRatings(long serviceid, int page, int pageSize) {
        Query nativeQuery = em.createNativeQuery("SELECT ratingid FROM ratings WHERE serviceid = :serviceid").setParameter("serviceid", serviceid);
        nativeQuery.setFirstResult((page - 1) * pageSize);
        nativeQuery.setMaxResults(pageSize);

        @SuppressWarnings("unchecked")
        List<Long> idList = ((Stream<Integer>) nativeQuery.getResultStream()).map(Integer::longValue).toList();

        TypedQuery<Rating> query = em.createQuery("SELECT r FROM Rating r WHERE r.id IN :idList", Rating.class);
        query.setParameter("idList", idList);
        return query.getResultList();
    }

    @Override
    public List<Rating> getAllRatingsFiltered(long serviceid, int page, int pageSize, String filter, boolean isDateType) {
        Query nativeQuery = em.createNativeQuery("SELECT ratingid FROM ratings WHERE serviceid = :serviceid ORDER BY date " + filter);
        nativeQuery.setParameter("serviceid", serviceid);
        nativeQuery.setFirstResult((page - 1) * pageSize);
        nativeQuery.setMaxResults(pageSize);
        @SuppressWarnings("unchecked")
        List<Long> idList = ((Stream<Integer>) nativeQuery.getResultStream()).map(Integer::longValue).toList();

        TypedQuery<Rating> query;
        if(isDateType) {
            query = em.createQuery("SELECT r FROM Rating r WHERE r.id IN :idList ORDER BY r.date " + filter, Rating.class);
        } else {
            query = em.createQuery("SELECT r FROM Rating r WHERE r.id IN :idList ORDER BY r.rating " + filter, Rating.class);
        }
        query.setParameter("idList", idList);
        return query.getResultList();
    }

    @Override
    public Optional<Rating> findById(long id) {
        return Optional.of(em.find(Rating.class, id));
    }

    @Override
    public Rating create(long serviceid, long userid, int rating, String comment) {
        Rating newRating = new Rating(em.find(Service.class, serviceid), em.find(User.class, userid), rating, comment, LocalDate.now());
        em.persist(newRating);
        return newRating;
    }



    @Override
    public Optional<Rating> hasAlreadyRated(long userid, long serviceid) {
        TypedQuery<Rating> query = em.createQuery("SELECT r FROM Rating r WHERE r.user.userId= :userid AND r.service.id = :serviceid", Rating.class);
        query.setParameter("userid", userid);
        query.setParameter("serviceid", serviceid);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public void edit(long ratingid, int rating, String comment) {
        Rating ratingToEdit = em.find(Rating.class, ratingid);
        ratingToEdit.setRating(rating);
        ratingToEdit.setComment(comment);
        em.persist(ratingToEdit);
    }

    @Override
    public List<Rating> getAllBusinessRatings(long businessId) {
        String jpql = "select r " +
                "from Rating r join r.service s " +
                "where s.business.businessid = :businessId";
        TypedQuery<Rating> query = em.createQuery(jpql, Rating.class);
        query.setParameter("businessId", businessId);
        return query.getResultList();
    }

    @Override
    public List<Object[]> getRatingsAvgByRate(long serviceId) {
        String jpql = "select r.rating, count(r.rating) from Rating r where r.service.id = :serviceId group by r.rating";
        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
        query.setParameter("serviceId", serviceId);
        return query.getResultList();
    }

    @Override
    public List<Object[]> getBusinessRatingsAvgByRate(long businessId) {
        String jpql = "select r.rating, count(r.rating) from Rating r where r.service.business.businessid = :businessId group by r.rating";
        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
        query.setParameter("businessId", businessId);
        return query.getResultList();
    }
}
