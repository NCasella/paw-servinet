package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.PasswordRecoveryCode;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserVerificationCode;
import ar.edu.itba.paw.services.UserVerificationDao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserVerificationDaoJpa implements UserVerificationDao {

    @PersistenceContext
    private EntityManager em;


    @Override
    public UserVerificationCode saveCodes(long userid, UUID tokenUrl, String verificationCode, LocalDateTime expirationDate) {
         UserVerificationCode user= new UserVerificationCode(em.find(User.class, userid), tokenUrl, verificationCode, expirationDate);
         em.persist(user);
         return user;
    }

    @Override
    public Optional<UserVerificationCode> getVerificationCodeByUserId(long userid) {
        return em.createQuery("from UserVerificationCode u where u.requestedBy.userId = :userid",UserVerificationCode.class).setParameter("userid", userid).getResultStream().findFirst();
    }
    @Override
    public Optional<UserVerificationCode> getVerificationCodeByTokenUrl(UUID tokenUrl) {
        return em.createQuery("from UserVerificationCode as p where p.tokenUrl= :code", UserVerificationCode.class)
                .setParameter("code", tokenUrl)
                .getResultList().stream().findFirst();
    }

    @Override
    public void deleteCode(long userid) {
        final UserVerificationCode userVerificationCode = getVerificationCodeByUserId(userid).orElse(null);
        if (userVerificationCode!= null) {
            em.remove(userVerificationCode);
        }
    }

}
