package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.services.UserDao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoJpa implements UserDao {
    @PersistenceContext
    private EntityManager em;

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
    public void changePassword(String email,String value){
        Optional<User> optUser=findByEmail(email);
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

}
