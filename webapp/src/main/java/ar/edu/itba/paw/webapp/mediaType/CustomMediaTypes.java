package ar.edu.itba.paw.webapp.mediaType;

public class CustomMediaTypes {
    private CustomMediaTypes(){}

    public static final String USER_REGISTRATION= "application/vnd.users.user-registration.v1+json";
    public static final String USER_INFO = "application/vnd.users.user-info.v1+json";
    public static final String USER_PATCH = "application/vnd.users.user-patch.v1+json";
    public static final String PASSWORD_RECOVERY = "application/vnd.users.password-recovery-request.v1+json";
    public static final String PASSWORD_RESET = "application/vnd.users.password-reset.v1+json";
    public static final String PASSWORD_MODIFICATION = "application/vnd.users.password-modification.v1+json";
    public static final String APPOINTMENT = "application/vnd.servinet.appointment.v1+json";

}
