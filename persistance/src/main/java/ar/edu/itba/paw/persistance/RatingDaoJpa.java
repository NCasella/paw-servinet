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
    public double getRatingsAvg(long serviceid) {
        Service service = em.find(Service.class, serviceid);
        return service.getRatingAvg();
    }

    @Override
    public int getRatingsCount(long serviceid) {
        Service service = em.find(Service.class, serviceid);
        return service.getRatingsCount();
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
    public double getBussinessRatingsAvg(long businessId) {
        String jpql = "select coalesce(round(avg(r.rating), 1), 0) " +
                "from Service s join s.ratings r " +
                "where s.business.businessid = :businessId";
        TypedQuery<Double> query = em.createQuery(jpql, Double.class);
        query.setParameter("businessId", businessId);
        return query.getSingleResult();
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
}
