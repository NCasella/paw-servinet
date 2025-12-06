package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.exceptions.InvalidUsernameException;
import ar.edu.itba.paw.services.UserDao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoJpa implements UserDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<User> getUsers(int page){
        final TypedQuery<User> query= em.createQuery("from User", User.class);
        query.setMaxResults(10);
        query.setFirstResult((page-1)*10);
        return query.getResultList();
    }
    @Override
    public int getUserCount(){
        final Query query = em.createNativeQuery("select count(u.userid) from users as u ");
        return ((Number) query.getSingleResult()).intValue();
    }
    @Override
    public boolean isUserProvidee(long providerUserId,long requestUserId){
        Query query=em.createQuery("select count(a) from Appointment a JOIN a.serviceAppointed s join s.business b where b.ownedBy.userId= :providerUserId and a.appointedBy.userId=:requestUserId");
        query.setParameter("providerUserId",providerUserId);
        query.setParameter("requestUserId",requestUserId);
        return ((Number)query.getSingleResult()).intValue()>0;
    }
    @Override
    public User create(final String username, final String name,final String surname, final String password, final String email, final String telephone, final boolean isProvider, final String locale){
        final User user =new User(username,password,name,surname,email,telephone,false,locale);
        em.persist(user);
        return user;
    }
    @Override
    public Optional<User> findById(long id){
        return Optional.ofNullable(em.find(User.class,id));
    }
    @Override
    public Optional<User> findByEmail(String email){
        final TypedQuery<User> query= em.createQuery("from User as u where u.email = :email", User.class);
        query.setParameter("email",email);
        final List<User> list= query.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }
    @Override
    public Optional<User> findByUsername(String username){
        final TypedQuery<User> query= em.createQuery("from User as u where u.username = :username", User.class);
        query.setParameter("username",username);
        final List<User> list= query.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }
    @Override
    public void changeEmail(long userid,String value){
     Optional<User> optUser=findById(userid);
     if(optUser.isPresent()){
        final User user=optUser.get();
        user.setEmail(value);
        em.persist(user);
     }
    }
    @Override
    public void changeTelephone(long userid,String value){
        Optional<User> optUser=findById(userid);
        if(optUser.isPresent()){
            final User user=optUser.get();
            user.setTelephone(value);
            em.persist(user);
        }
    }

    @Override
    public void changePassword(long userId,String value){
        Optional<User> optUser= findById(userId);
        if(optUser.isPresent()){
            final User user=optUser.get();
            user.setPassword(value);
            em.persist(user);
        }
    }
    @Override
    public void changeUsername(long userid,String value){
        Optional<User> optUser=findById(userid);
        if(optUser.isPresent()){
            final User user=optUser.get();
            user.setUsername(value);
            em.persist(user);
        }
    }
    @Override
    public void changeUserType(long userid){
        Optional<User> optUser=findById(userid);
        if(optUser.isPresent()){
            final User user=optUser.get();
            user.setProvider(true);
            em.persist(user);
        }
    }

    @Override
    public void changeLocale(long userid,String locale){
        Optional<User> optUser=findById(userid);
        if(optUser.isPresent()){
            final User user=optUser.get();
            user.setLocale(locale);
            em.persist(user);
        }
    }

    public void verifyUser(long userid){
        Optional<User> maybeUser= findById(userid);
        if(maybeUser.isPresent()){
            final User user= maybeUser.get();
            user.setIsVerified(true);
            em.persist(user);
        }
    }
}
