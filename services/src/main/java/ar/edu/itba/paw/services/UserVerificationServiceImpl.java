package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.PasswordRecoveryCode;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserVerificationCode;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service("userVerificationServiceImpl")
public class UserVerificationServiceImpl implements UserVerificationService{
    private final UserVerificationDao userVerificationDao;
    private final EmailService emailService;
    private final Logger LOGGER = LoggerFactory.getLogger(UserVerificationServiceImpl.class);

    @Autowired
    public UserVerificationServiceImpl(UserVerificationDao userVerificationDao, EmailService emailService){
        this.userVerificationDao = userVerificationDao;
        this.emailService = emailService;
    }

    @Transactional
    @Override
    public void sendVerificationCode(User user) {
        UserVerificationCode verificationCode = generateVerificationCode(user.getUserId());
        emailService.sendVerificationCode(user, verificationCode);
    }

    @Transactional
    @Override
    public UserVerificationCode generateVerificationCode(long userid) {
        userVerificationDao.deleteCode(userid);
        UUID newVerificationToken = UUID.randomUUID();
        int randomInt=  new Random().nextInt(1000000);
        String newVerificationCode = String.format("%d",randomInt);
        UserVerificationCode code = userVerificationDao.saveCodes(userid, newVerificationToken, newVerificationCode, LocalDateTime.now().plusDays(1));
        LOGGER.info("Verification code succesfully generated");
        return code;
    }

    @Transactional
    @Override
    public boolean validateTokenUrl(UUID tokenUrl) {
        Optional<UserVerificationCode> possibleUserVerificationCode = userVerificationDao.getVerificationCodeByTokenUrl(tokenUrl);
        if (possibleUserVerificationCode.isEmpty()){
            LOGGER.warn("The code provided is not valid, it has expired or it has been used already");
            return false;
        }
        return true;
    }

    @Transactional
    @Override
    public Optional<UserVerificationCode> getUserVerificationCodeByTokenUrl(UUID tokenUrl){
        return userVerificationDao.getVerificationCodeByTokenUrl(tokenUrl);
    }

    @Transactional
    @Override
    public boolean verifyUser(UUID tokenUrl, String verificationCode) {
        Optional<UserVerificationCode> possibleUserVerificationCode = userVerificationDao.getVerificationCodeByTokenUrl(tokenUrl);
        if (possibleUserVerificationCode.isEmpty()){
            LOGGER.warn("The code provided is not valid, it has expired or it has been used already");
            return false;
        }
        UserVerificationCode userVerificationCode = possibleUserVerificationCode.get();
        if (userVerificationCode.getVerificationCode().equals(verificationCode)){
            return true;
        }
        else{
            LOGGER.warn("The code provided is not valid, it has expired or it has been used already");
            return false;
        }
    }

    @Transactional
    @Override
    public void deleteCode(long userid) {
        userVerificationDao.deleteCode(userid);
    }

}
