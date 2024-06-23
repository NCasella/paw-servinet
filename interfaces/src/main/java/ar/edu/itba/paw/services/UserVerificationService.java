package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.PasswordRecoveryCode;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserVerificationCode;

import java.util.Optional;
import java.util.UUID;

public interface UserVerificationService {
    void sendVerificationCode(User user);
    UserVerificationCode generateVerificationCode(long userid);
    Optional<UserVerificationCode> getUserVerificationCodeByTokenUrl(UUID tokenUrl);
    boolean verifyUser(UUID tokenUrl, String verificationCode);
    void deleteCode(long userid);
    boolean validateTokenUrl(UUID tokenUrl);

}
