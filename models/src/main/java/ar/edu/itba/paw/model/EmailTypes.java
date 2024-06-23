package ar.edu.itba.paw.model;

import static ar.edu.itba.paw.model.EmailTemplates.*;

public enum EmailTypes {
    WAITING("WAITING","subject.appointment.waiting"),
    REQUEST("REQUEST", "subject.appointment.request"),
    CANCELLED("CANCELLED","subject.appointment.cancelled"),
    ACCEPTED("ACCEPTED", "subject.appointment.accepted"),
    DENIED("DENIED", "subject.appointment.denied"),
    CREATED_SERVICE("CREATED", "subject.service.created", SERVICE_TEMPLATE),
    DELETED_SERVICE("DELETED", "subject.service.deleted", SERVICE_TEMPLATE),
    CREATED_BUSINESS("CREATED", "subject.business.created", BUSINESS_TEMPLATE),
    DELETED_BUSINESS("DELETED", "subject.business.deleted", BUSINESS_TEMPLATE),
    PASSWORD_RECOVER("RECOVER_PASSWORD", "subject.password.recover", RECOVER_PASSWORD_TEMPLATE),
    CONFIRM_NEW_PASSWORD("CONFIRM_NEW_PASSWORD", "subject.password.confirm", CONFIRM_NEW_PASSWORD_TEMPLATE),
    ASKED_QUESTION("ASKED","subject.asked-question",QUESTION_TEMPLATE),
    ANSWERED_QUESTION("ANSWERED","subject.answered-question",QUESTION_TEMPLATE),
    VERIFICATION_CODE("USER_VERIFICATION","subject.user-verification", USER_VERIFICATION_TEMPLATE);

    private final String name;
    private final String subject;
    private final EmailTemplates template;
    private final boolean isAboutAppointment;

    EmailTypes(String name, String subject) {
        this.name = name;
        this.subject = subject;
        this.template = APPOINTMENT_TEMPLATE;
        this.isAboutAppointment = true;
    }

    EmailTypes(String name, String subject, EmailTemplates template) {
        this.name = name;
        this.subject = subject;
        this.template = template;
        this.isAboutAppointment = false;
    }

    public String getType(){ return name; }

    public String getSubject(){
        return  subject;
    }

    public String getTemplate(){
        return template.toString();
    }

    public boolean isAboutAppointment(){ return isAboutAppointment; }

    public boolean isRequestAnswer() { return this == ACCEPTED || this == DENIED ;}
}
