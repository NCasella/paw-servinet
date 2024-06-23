package ar.edu.itba.paw.model;

public enum EmailTemplates {
    APPOINTMENT_TEMPLATE("html/appointment.html"),
    SERVICE_TEMPLATE("html/service.html"),
    BUSINESS_TEMPLATE("html/business.html"),
    RECOVER_PASSWORD_TEMPLATE("html/recoverPassword.html"),
    CONFIRM_NEW_PASSWORD_TEMPLATE("html/confirmNewPassword.html"),
    QUESTION_TEMPLATE("html/question.html"),
    USER_VERIFICATION_TEMPLATE("html/userVerification.html");
    private final String template;

    EmailTemplates(String template) {
        this.template = template;
    }

    @Override
    public String toString() {
        return template;
    }
}
