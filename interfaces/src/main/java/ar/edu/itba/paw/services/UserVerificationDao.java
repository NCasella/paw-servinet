package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.PasswordRecoveryCode;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserVerificationCode;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserVerificationDao {
    UserVerificationCode saveCodes(long userid, String verificationCode, LocalDateTime ExpirationDate);
    void deleteCode(long userid);
    Optional<UserVerificationCode> getVerificationCodeByUserId(long userid);
}
