package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.time.LocalDateTime;

// sino q sea solo sea usado por services, osea no un entity


public class AppointmentInfo extends BasicAppointment {


    private String serviceName;

    // ! como sabe de que tabla hablo?

    private String businessEmail;


    private String businessTelephone;


    public AppointmentInfo(long serviceid, LocalDateTime startDate, LocalDateTime endDate, String location, boolean confirmed,
                           String serviceName, String businessEmail, String businessTelephone ) {
        super(serviceid, startDate, endDate, location, confirmed);
        this.serviceName = serviceName;
        this.businessEmail = businessEmail;
        this.businessTelephone = businessTelephone;
    }

    public AppointmentInfo() {
        super();
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public String getBusinessTelephone() {
        return businessTelephone;
    }

}
