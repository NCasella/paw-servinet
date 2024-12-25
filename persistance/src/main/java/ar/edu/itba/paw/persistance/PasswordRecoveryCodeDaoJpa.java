package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.PasswordRecoveryCode;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.services.PasswordRecoveryCodeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PasswordRecoveryCodeDaoJpa implements PasswordRecoveryCodeDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public PasswordRecoveryCode saveCode(long userid, UUID code, LocalDateTime expirationDate) {
        PasswordRecoveryCode password = new PasswordRecoveryCode(em.find(User.class, userid), code, expirationDate);
        em.persist(password);
        return password;
    }

    @Override
    public Optional<PasswordRecoveryCode> getCodeByUserId(long userid) {
        return Optional.ofNullable(em.find(PasswordRecoveryCode.class, userid));
    }
    @Override
    public Optional<PasswordRecoveryCode> getCodeByUUID(UUID code) {
        return em.createQuery("from PasswordRecoveryCode as p where p.code = :code", PasswordRecoveryCode.class)
                .setParameter("code", code)
                .getResultList().stream().findFirst();
    }

    @Override
    public void deleteCode(long userid) {
        PasswordRecoveryCode passwordRecoveryCode = getCodeByUserId(userid).orElse(null);
        if (passwordRecoveryCode != null) {
            em.remove(passwordRecoveryCode);
        }
    }

}
