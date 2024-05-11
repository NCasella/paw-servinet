package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Appointment;

import javax.mail.MessagingException;

import ar.edu.itba.paw.model.Business;
import ar.edu.itba.paw.model.PasswordRecoveryCode;
import ar.edu.itba.paw.model.Service;
import ar.edu.itba.paw.model.User;
import org.springframework.scheduling.annotation.Async;

public interface EmailService {

    void requestAppointment(Appointment appointment, Service service, Business business, User client) ;
    void recoverPassword(User user, PasswordRecoveryCode passwordRecoveryCode) ;

    void confirmNewPassword(User user) ;

    void confirmedAppointment(Appointment appointment, Service service, Business business, User client) ;

    void cancelledAppointment(Appointment appointment, Service service, Business business, User client, boolean isServiceDeleted) ;

    void deniedAppointment(Appointment appointment, Service service, Business business, User client, boolean isServiceDeleted) ;

    void deletedService(Service service, Business business) ;

    void createdService(Service service, Business business) ;

    void createdBusiness(Business business) ;

    void deletedBusiness(Business business) ;

    void answeredQuestion(Service service, Business business, User client, String response) throws MessagingException;

}
