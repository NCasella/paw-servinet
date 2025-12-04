package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.PasswordRecoveryCode;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.exceptions.InvalidEmailException;
import ar.edu.itba.paw.model.exceptions.InvalidRecoveryCodeException;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordRecoveryCodeServiceImpl implements PasswordRecoveryCodeService{
    private final PasswordRecoveryCodeDao passwordRecoveryCodeDao;
    private final EmailService emailService;
    private final UserService userService;
    private final Logger LOGGER = LoggerFactory.getLogger(PasswordRecoveryCodeServiceImpl.class);

    @Autowired
    public PasswordRecoveryCodeServiceImpl(PasswordRecoveryCodeDao passwordRecoveryCodeDao, EmailService emailService, UserService userService){
        this.passwordRecoveryCodeDao = passwordRecoveryCodeDao;
        this.emailService = emailService;
        this.userService = userService;
    }

    @Transactional
    @Override
    public void sendCode(String email) {
        User user = userService.findByEmail(email).orElseThrow(InvalidEmailException::new);

        PasswordRecoveryCode passwordRecoveryCode = generateCode(user.getUserId());
        emailService.recoverPassword(user, passwordRecoveryCode);
    }

    @Transactional
    @Override
    public PasswordRecoveryCode generateCode(long userid) {
        passwordRecoveryCodeDao.deleteCode(userid);
        UUID newCode = UUID.randomUUID();
        PasswordRecoveryCode code = passwordRecoveryCodeDao.saveCode(userid, newCode, LocalDateTime.now().plusHours(1));
        LOGGER.info("Recovery code succesfully generated");
        return code;
    }

    @Transactional
    @Override
    public boolean validateCode(UUID code) {
        Optional<PasswordRecoveryCode> possiblepasswordRecoveryCode = passwordRecoveryCodeDao.getCodeByUUID(code);
        if (possiblepasswordRecoveryCode.isEmpty()){
            LOGGER.warn("The code provided is not valid, it has expired or it has been used already");
            return false;
        }
        PasswordRecoveryCode passwordRecoveryCode = possiblepasswordRecoveryCode.get();
        return passwordRecoveryCode.getCode().equals(code);
    }

    @Transactional
    public void changePassword(UUID code, String newPassword) {
        PasswordRecoveryCode passwordRecoveryCode = passwordRecoveryCodeDao.getCodeByUUID(code).orElseThrow(InvalidRecoveryCodeException::new);
        User user = userService.findById(passwordRecoveryCode.getUserId()).orElseThrow(UserNotFoundException::new);
        userService.changePassword(user.getUserId(), newPassword);
        LOGGER.info("Password changed successfully");
        deleteCode(passwordRecoveryCode.getUserId());
        emailService.confirmNewPassword(user);
    }

    @Transactional
    @Override
    public void deleteCode(long userid) {
        passwordRecoveryCodeDao.deleteCode(userid);
    }

}
