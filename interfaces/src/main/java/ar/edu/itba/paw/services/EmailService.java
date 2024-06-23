package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;

import javax.mail.MessagingException;

import org.springframework.scheduling.annotation.Async;

public interface EmailService {

    void sendVerificationCode(User user, UserVerificationCode userVerificationCode);

    void recoverPassword(User user, PasswordRecoveryCode passwordRecoveryCode);

    void confirmNewPassword(User user);

    void requestAppointment(Appointment appointment, Service service, Business business, User client, String businessLocale);

    void confirmedAppointment(Appointment appointment, Service service, Business business, User client, String businessLocale);

    void cancelledAppointment(Appointment appointment, Service service, Business business, User client, boolean isServiceDeleted, String businessLocale);

    void deniedAppointment(Appointment appointment, Service service, Business business, User client, boolean isServiceDeleted, String businessLocale);

    void deletedService(Service service, Business business, String businessLocale);

    void createdService(Service service, Business business, String businessLocale);

    void createdBusiness(Business business, String businessLocale);

    void deletedBusiness(Business business, String businessLocale);

    void askedQuestion(BasicService service, String businessEmail, User client, String question, String businessLocale);
    void answeredQuestion(BasicService service, User client, String question, String response);

}
