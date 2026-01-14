package ar.edu.itba.paw.services;

import org.junit.Assert;
import org.junit.Test;

import ar.edu.itba.paw.services.emailEnums.EmailTemplates;
import ar.edu.itba.paw.services.emailEnums.EmailTypes;

/**
 * Tests for EmailServiceImpl focusing on email types and templates.
 */
public class EmailServiceImplTest {

    @Test
    public void testEmailTypesAppointmentTypes() {
        Assert.assertEquals("html/appointment.html", EmailTypes.WAITING.getTemplate());
        Assert.assertEquals("html/appointment.html", EmailTypes.REQUEST.getTemplate());
        Assert.assertEquals("html/appointment.html", EmailTypes.CANCELLED.getTemplate());
        Assert.assertEquals("html/appointment.html", EmailTypes.ACCEPTED.getTemplate());
        Assert.assertEquals("html/appointment.html", EmailTypes.DENIED.getTemplate());
    }

    @Test
    public void testEmailTypesServiceTypes() {
        Assert.assertEquals("html/service.html", EmailTypes.CREATED_SERVICE.getTemplate());
        Assert.assertEquals("html/service.html", EmailTypes.DELETED_SERVICE.getTemplate());
    }

    @Test
    public void testEmailTypesBusinessTypes() {
        Assert.assertEquals("html/business.html", EmailTypes.CREATED_BUSINESS.getTemplate());
        Assert.assertEquals("html/business.html", EmailTypes.DELETED_BUSINESS.getTemplate());
    }

    @Test
    public void testEmailTypesQuestionTypes() {
        Assert.assertEquals("html/question.html", EmailTypes.ASKED_QUESTION.getTemplate());
        Assert.assertEquals("html/question.html", EmailTypes.ANSWERED_QUESTION.getTemplate());
    }

    @Test
    public void testEmailTypesPasswordTypes() {
        Assert.assertEquals("html/recoverPassword.html", EmailTypes.PASSWORD_RECOVER.getTemplate());
        Assert.assertEquals("html/confirmNewPassword.html", EmailTypes.CONFIRM_NEW_PASSWORD.getTemplate());
    }

    @Test
    public void testEmailTypesVerificationType() {
        Assert.assertEquals("html/userVerification.html", EmailTypes.VERIFICATION_CODE.getTemplate());
    }

    @Test
    public void testEmailTypesIsAboutAppointment() {
        Assert.assertTrue(EmailTypes.WAITING.isAboutAppointment());
        Assert.assertTrue(EmailTypes.REQUEST.isAboutAppointment());
        Assert.assertTrue(EmailTypes.CANCELLED.isAboutAppointment());
        Assert.assertTrue(EmailTypes.ACCEPTED.isAboutAppointment());
        Assert.assertTrue(EmailTypes.DENIED.isAboutAppointment());

        Assert.assertFalse(EmailTypes.CREATED_SERVICE.isAboutAppointment());
        Assert.assertFalse(EmailTypes.DELETED_SERVICE.isAboutAppointment());
        Assert.assertFalse(EmailTypes.CREATED_BUSINESS.isAboutAppointment());
        Assert.assertFalse(EmailTypes.DELETED_BUSINESS.isAboutAppointment());
        Assert.assertFalse(EmailTypes.PASSWORD_RECOVER.isAboutAppointment());
        Assert.assertFalse(EmailTypes.CONFIRM_NEW_PASSWORD.isAboutAppointment());
        Assert.assertFalse(EmailTypes.ASKED_QUESTION.isAboutAppointment());
        Assert.assertFalse(EmailTypes.ANSWERED_QUESTION.isAboutAppointment());
        Assert.assertFalse(EmailTypes.VERIFICATION_CODE.isAboutAppointment());
    }

    @Test
    public void testEmailTypesIsRequestAnswer() {
        Assert.assertTrue(EmailTypes.ACCEPTED.isRequestAnswer());
        Assert.assertTrue(EmailTypes.DENIED.isRequestAnswer());

        Assert.assertFalse(EmailTypes.WAITING.isRequestAnswer());
        Assert.assertFalse(EmailTypes.REQUEST.isRequestAnswer());
        Assert.assertFalse(EmailTypes.CANCELLED.isRequestAnswer());
    }

    @Test
    public void testEmailTypesGetType() {
        Assert.assertEquals("WAITING", EmailTypes.WAITING.getType());
        Assert.assertEquals("REQUEST", EmailTypes.REQUEST.getType());
        Assert.assertEquals("CANCELLED", EmailTypes.CANCELLED.getType());
        Assert.assertEquals("ACCEPTED", EmailTypes.ACCEPTED.getType());
        Assert.assertEquals("DENIED", EmailTypes.DENIED.getType());
        Assert.assertEquals("CREATED", EmailTypes.CREATED_SERVICE.getType());
        Assert.assertEquals("DELETED", EmailTypes.DELETED_SERVICE.getType());
        Assert.assertEquals("CREATED", EmailTypes.CREATED_BUSINESS.getType());
        Assert.assertEquals("DELETED", EmailTypes.DELETED_BUSINESS.getType());
        Assert.assertEquals("RECOVER_PASSWORD", EmailTypes.PASSWORD_RECOVER.getType());
        Assert.assertEquals("CONFIRM_NEW_PASSWORD", EmailTypes.CONFIRM_NEW_PASSWORD.getType());
        Assert.assertEquals("ASKED", EmailTypes.ASKED_QUESTION.getType());
        Assert.assertEquals("ANSWERED", EmailTypes.ANSWERED_QUESTION.getType());
        Assert.assertEquals("USER_VERIFICATION", EmailTypes.VERIFICATION_CODE.getType());
    }

    @Test
    public void testEmailTypesGetSubject() {
        Assert.assertEquals("subject.appointment.waiting", EmailTypes.WAITING.getSubject());
        Assert.assertEquals("subject.appointment.request", EmailTypes.REQUEST.getSubject());
        Assert.assertEquals("subject.appointment.cancelled", EmailTypes.CANCELLED.getSubject());
        Assert.assertEquals("subject.appointment.accepted", EmailTypes.ACCEPTED.getSubject());
        Assert.assertEquals("subject.appointment.denied", EmailTypes.DENIED.getSubject());
        Assert.assertEquals("subject.service.created", EmailTypes.CREATED_SERVICE.getSubject());
        Assert.assertEquals("subject.service.deleted", EmailTypes.DELETED_SERVICE.getSubject());
        Assert.assertEquals("subject.business.created", EmailTypes.CREATED_BUSINESS.getSubject());
        Assert.assertEquals("subject.business.deleted", EmailTypes.DELETED_BUSINESS.getSubject());
        Assert.assertEquals("subject.password.recover", EmailTypes.PASSWORD_RECOVER.getSubject());
        Assert.assertEquals("subject.password.confirm", EmailTypes.CONFIRM_NEW_PASSWORD.getSubject());
        Assert.assertEquals("subject.asked-question", EmailTypes.ASKED_QUESTION.getSubject());
        Assert.assertEquals("subject.answered-question", EmailTypes.ANSWERED_QUESTION.getSubject());
        Assert.assertEquals("subject.user-verification", EmailTypes.VERIFICATION_CODE.getSubject());
    }

    @Test
    public void testEmailTemplatesValues() {
        Assert.assertEquals("html/appointment.html", EmailTemplates.APPOINTMENT_TEMPLATE.toString());
        Assert.assertEquals("html/service.html", EmailTemplates.SERVICE_TEMPLATE.toString());
        Assert.assertEquals("html/business.html", EmailTemplates.BUSINESS_TEMPLATE.toString());
        Assert.assertEquals("html/recoverPassword.html", EmailTemplates.RECOVER_PASSWORD_TEMPLATE.toString());
        Assert.assertEquals("html/confirmNewPassword.html", EmailTemplates.CONFIRM_NEW_PASSWORD_TEMPLATE.toString());
        Assert.assertEquals("html/question.html", EmailTemplates.QUESTION_TEMPLATE.toString());
        Assert.assertEquals("html/userVerification.html", EmailTemplates.USER_VERIFICATION_TEMPLATE.toString());
    }

    @Test
    public void testAllEmailTypesHaveValidTemplates() {
        for (EmailTypes type : EmailTypes.values()) {
            Assert.assertNotNull("Template should not be null for " + type, type.getTemplate());
            Assert.assertFalse("Template should not be empty for " + type, type.getTemplate().isEmpty());
        }
    }

    @Test
    public void testAllEmailTypesHaveValidSubjects() {
        for (EmailTypes type : EmailTypes.values()) {
            Assert.assertNotNull("Subject should not be null for " + type, type.getSubject());
            Assert.assertFalse("Subject should not be empty for " + type, type.getSubject().isEmpty());
        }
    }

    @Test
    public void testAllEmailTypesHaveValidTypes() {
        for (EmailTypes type : EmailTypes.values()) {
            Assert.assertNotNull("Type name should not be null for " + type, type.getType());
            Assert.assertFalse("Type name should not be empty for " + type, type.getType().isEmpty());
        }
    }
}
