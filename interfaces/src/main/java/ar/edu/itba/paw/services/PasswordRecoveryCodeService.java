package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.PasswordRecoveryCode;
import ar.edu.itba.paw.model.User;

import javax.mail.MessagingException;
import java.util.Optional;
import java.util.UUID;

public interface PasswordRecoveryCodeService {

    Optional<User> getUserFromRecoveryCode(UUID code);
    void sendCode(String email);
    PasswordRecoveryCode generateCode(long userid);
    void changePassword(UUID code, String newPassword);
    void deleteCode(long userid);
    boolean validateCode(UUID code);
}
