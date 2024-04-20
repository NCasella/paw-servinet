package ar.edu.itba.paw.model;

import java.time.LocalDateTime;


public class AppointmentInfo {
    private final long appointmentid;
    private final long serviceid;
    private final String serviceName;
    private final String businessName;
    private final String appointmentLocation;
    private final LocalDateTime appointmentDate;
    private final boolean confirmed;
    private final String businessEmail;
    private final String businessTelephone;

    public AppointmentInfo(long appointmentid, long serviceid, String serviceName, String businessName, String appointmentLocation, LocalDateTime appointmentDate, boolean confirmed, String businessEmail, String businessTelephone) {
        this.appointmentid = appointmentid;
        this.serviceid = serviceid;
        this.serviceName = serviceName;
        this.businessName = businessName;
        this.appointmentLocation = appointmentLocation;
        this.appointmentDate = appointmentDate;
        this.confirmed = confirmed;
        this.businessEmail = businessEmail;
        this.businessTelephone = businessTelephone;
    }

    public long getAppointmentid() {
        return appointmentid;
    }

    public long getServiceid() {
        return serviceid;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getAppointmentLocation() {
        return appointmentLocation;
    }

    public String getBusinessName() {
        return businessName;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public String getBusinessTelephone() {
        return businessTelephone;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
