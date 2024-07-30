package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.PasswordRecoveryCode;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserVerificationCode;

import java.util.Optional;
import java.util.UUID;

public interface UserVerificationService {
    void sendVerificationCode(User user);
    UserVerificationCode generateVerificationCode(long userid);
    Optional<UserVerificationCode> getUserVerificationCodeByUserId(long userid);
    boolean isVerificationExpired(long userid);

    boolean verifyUser(long userid, String verificationCode);
    void deleteCode(long userid);

}
