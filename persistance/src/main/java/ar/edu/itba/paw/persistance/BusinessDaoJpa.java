package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Business;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.services.BusinessDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public List<Business> findByUser(User user){
        return em.createQuery("from Business as b where b.user = :user", Business.class)
                .setParameter("user", user)
                .getResultList();
    }

    @Override
    public Optional<String> getBusinessEmail(long businessid) {
        return Optional.ofNullable(em.find(Business.class, businessid).getEmail());
    }

    @Override
    public boolean deleteBusiness(long businessid) {
        final Business business = em.find(Business.class, businessid);
        if (business != null) {
            em.remove(business);
            return true;
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
