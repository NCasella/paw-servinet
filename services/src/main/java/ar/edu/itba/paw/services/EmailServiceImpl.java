package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;

@org.springframework.stereotype.Service()
public class EmailServiceImpl implements EmailService{

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    //temp
    private final Locale LOCALE = Locale.forLanguageTag("es-419"); // Locale.of("es");
    private final String APP_URL = "http://localhost:8080/webapp_war/"; //! CAMBIAR EN DEPLOY
    private final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Async // * funciona pues no invoque a un metodo dentro de la clase
    @Override
    public void requestAppointment(Appointment appointment, Service service, Business business, User client) {
        // no llama a prepareAndSendMails() pues no necesita user (en sprint1)

        final Context ctx = getContext(appointment,service,false, client, business);

        LOGGER.info("Preparing request mail for business owner.");
        try {
            sendMailToBusiness(EmailTypes.REQUEST, business.getEmail(), ctx);
        }catch(MessagingException e){
            LOGGER.warn("Error while preparing request notification email for business owner: {}", e.getMessage());
        }
        LOGGER.info("Preparing request mail for client.");
        try {
            sendMailToClient(EmailTypes.WAITING, client.getEmail(), ctx);
        }catch(MessagingException e){
            LOGGER.warn("Error while preparing request notification email for client: {}", e.getMessage());
        }
    }

    @Async
    @Override
    public void confirmedAppointment(Appointment appointment, Service service, Business business, User client) {
        prepareAndSendAppointmentMails(appointment, EmailTypes.ACCEPTED, service, business, client, false);
    }
    public void recoverPassword(User user, PasswordRecoveryCode code) {
        final Context ctx = new Context(LOCALE);
        ctx.setVariable("user", user);
        ctx.setVariable("token", code.getCode());
        LOGGER.info("Preparing password recovery mail for user.");
        try {
            sendMail(user.getEmail(), String.format("%s- Cuenta de Servinet de @%s", EmailTypes.PASSWORD_RECOVER.getSubject(""), user.getUsername()), ctx, EmailTypes.PASSWORD_RECOVER.getTemplate());
        }catch(MessagingException e){
            LOGGER.warn("Error while preparing password recovery email: {}", e.getMessage());
        }
    }
    @Async
    @Override
    public void confirmNewPassword(User user) {
        final Context ctx = new Context(LOCALE);
        ctx.setVariable("user", user);
        LOGGER.info("Preparing new password confirmation mail for user.");
        try {
            sendMail(user.getEmail(), String.format("Nueva contraseña definida con éxito para @%s - Servinet", user.getUsername()), ctx, EmailTypes.PASSWORD_RECOVER.getTemplate());
        }catch(MessagingException e){
            LOGGER.warn("Error while preparing new password confirmation email: {}", e.getMessage());
        }
    }

    @Async
    @Override
    public void cancelledAppointment(Appointment appointment, Service service, Business business, User client, boolean isServiceDeleted) {
        prepareAndSendAppointmentMails(appointment,EmailTypes.CANCELLED, service, business, client, isServiceDeleted);
    }

    @Async
    @Override
    public void deniedAppointment(Appointment appointment, Service service, Business business, User client,boolean isServiceDeleted) {
        prepareAndSendAppointmentMails(appointment,EmailTypes.DENIED, service, business, client, isServiceDeleted);
    }

    private void prepareAndSendAppointmentMails(Appointment appointment, EmailTypes emailType,  Service service, Business business, User client, boolean isServiceDeleted) {
        final Context ctx = getContext(appointment,service,isServiceDeleted, client, business);

        if (!isServiceDeleted) {
            LOGGER.info("Preparing {} mail for business owner.", emailType.getType());
            try {
                sendMailToBusiness(emailType, business.getEmail(), ctx);
            }catch(MessagingException e){
                LOGGER.warn("Error while preparing {} notification email: {}", emailType.getType(), e.getMessage());
            }
        }
        try {
            sendMailToClient(emailType, client.getEmail(), ctx);
            LOGGER.info("{} mail for client sent successfully.", emailType.getType());
        }catch (MessagingException e){
            LOGGER.warn("Error while preparing {} notification email: {}", emailType.getType(), e.getMessage());
        }
    }

    private Context getContext(Appointment appointment, Service service, boolean isServiceDeleted, User client, Business business){
        final Context ctx = new Context(LOCALE);
        ctx.setVariable("client", client);
        ctx.setVariable("business", business);
        ctx.setVariable("serviceName",service.getName());
        ctx.setVariable("serviceId",service.getId());
        ctx.setVariable("appointmentId",appointment.getId());
        ctx.setVariable("startdate",appointment.getStartDate());
        ctx.setVariable("isServiceDeleted", isServiceDeleted);
        return ctx;
    }

    @Async
    @Override
    public void createdService(Service service, Business business) {
        Context ctx = new Context(LOCALE);
        ctx.setVariable("serviceId",service.getId());
        ctx.setVariable("serviceName",service.getName());
        LOGGER.info("Preparing service creation notification mail for business owner");
        try {
            sendMailToBusiness(EmailTypes.CREATED_SERVICE, business.getEmail(), ctx);
        }catch(MessagingException e){
            LOGGER.warn("Error while preparing service creation notification email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void deletedService(Service service, Business business ) {

        Context ctx = new Context(LOCALE);
        ctx.setVariable("serviceId",service.getId());
        ctx.setVariable("serviceName",service.getName());
        LOGGER.info("Preparing service deletion notification mail for business owner");
        try {
            sendMailToBusiness(EmailTypes.DELETED_SERVICE, business.getEmail(), ctx);
        }catch(MessagingException e){
            LOGGER.warn("Error while preparing service deletion notification email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void createdBusiness(Business business) {
        Context ctx = new Context(LOCALE);
        ctx.setVariable("businessId",business.getBusinessid());
        ctx.setVariable("businessName",business.getName());
        LOGGER.info("Preparing business creation notification mail for business owner");
        try {
            sendMail(business.getEmail(), EmailTypes.CREATED_BUSINESS.getSubject(business.getBusinessName()), ctx, EmailTypes.CREATED_BUSINESS.getTemplate());
        }catch(MessagingException e){
            LOGGER.warn("Error while preparing business creation notification email: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void deletedBusiness(Business business) {

        Context ctx = new Context(LOCALE);
        ctx.setVariable("businessId",business.getBusinessid());
        ctx.setVariable("businessName",business.getName());
        LOGGER.info("Preparing business deletion notification mail for business owner");
        try {
            sendMail(business.getEmail(), EmailTypes.DELETED_BUSINESS.getSubject(business.getBusinessName()), ctx, EmailTypes.DELETED_BUSINESS.getTemplate());
        }catch(MessagingException e){
            LOGGER.warn("Error while preparing business deletion notification email: {}", e.getMessage());
        }
    }

    public void answeredQuestion(Service service, Business business, User client, String response) throws MessagingException{

    }

    public void sendMailToClient( EmailTypes emailType, String userMail, Context ctx) throws MessagingException {
        ctx.setVariable("isClient",true);
        ctx.setVariable("type", emailType.getType());
        sendMail(userMail, emailType.getSubject((Long) ctx.getVariable("appointmentId"), (String) ctx.getVariable("serviceName")),ctx, emailType.getTemplate());
    }

    private void sendMailToBusiness( EmailTypes emailType, String businessMail, Context ctx) throws MessagingException {

        ctx.setVariable("isClient",false);
        ctx.setVariable("type", emailType.getType());
        // Preparo subject
        String serviceName = (String) ctx.getVariable("serviceName");
        String subject;
        if (emailType.isAboutAppointment())
            subject =  emailType.getSubject((Long) ctx.getVariable("appointmentId"), serviceName );
        else
            subject = emailType.getSubject(serviceName);
        sendMail(businessMail, subject,ctx, emailType.getTemplate());
    }

    private void sendMail( final String recipientEmail, String subject, final Context ctx, String template) throws MessagingException {

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        message.setSubject(subject);
        message.setTo(recipientEmail);

        ctx.setVariable("url", APP_URL);

        final String htmlContent = this.templateEngine.process(template, ctx );
        message.setText(htmlContent, true); // true = isHtml

        try {
            LOGGER.info("Preparing to send mail");
            this.mailSender.send(mimeMessage);
            LOGGER.info("Mail sent successfully.");
        }
        catch (MailException ex) {
            LOGGER.warn("Error sending email: {}", ex.getMessage());
        }

    }
}
