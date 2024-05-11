package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.PasswordRecoveryCode;

import javax.mail.MessagingException;
import java.util.UUID;

public interface PasswordRecoveryCodeService {

    void sendCode(String email) ;
    PasswordRecoveryCode generateCode(long userid);
    void changePassword(UUID code, String newPassword) ;
    void deleteCode(long userid);
    boolean validateCode(UUID code);
}
