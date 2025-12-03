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
        PasswordRecoveryCode recoveryCode = new PasswordRecoveryCode(em.find(User.class, userid), code, expirationDate);
        em.persist(recoveryCode);
        return recoveryCode;
    }

    @Override
    public Optional<PasswordRecoveryCode> getCodeByUserId(long userid) {
        try {
            PasswordRecoveryCode code = em.createQuery(
                   "FROM PasswordRecoveryCode as p WHERE p.requestedBy.userId  = :userId", PasswordRecoveryCode.class)
                    .setParameter("userId", userid)
                    .getSingleResult();

            return Optional.of(code);

        } catch (javax.persistence.NoResultException e) {
            return Optional.empty();
        }
    }
    @Override
    public Optional<PasswordRecoveryCode> getCodeByUUID(UUID code) {
        return em.createQuery("from PasswordRecoveryCode as p where p.code = :code", PasswordRecoveryCode.class)
                .setParameter("code", code)
                .getResultList().stream().findFirst();
    }

    @Override
    public void deleteCode(long userid) {
        final PasswordRecoveryCode passwordRecoveryCode = getCodeByUserId(userid).orElse(null);
        if (passwordRecoveryCode != null) {
            em.remove(passwordRecoveryCode);
        }
    }

}
