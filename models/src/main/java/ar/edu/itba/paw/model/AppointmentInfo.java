package ar.edu.itba.paw.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.SecondaryTable;
import java.time.LocalDateTime;

// sino q sea solo sea usado por services, osea no un entity

@Entity
/*
@SecondaryTable(name = "business")
@SecondaryTable(name = "services") */
public class AppointmentInfo extends BasicAppointment {

    @JoinColumn(name = "servicename", nullable = false)
    private String serviceName;

    // ! como sabe de que tabla hablo?
    @JoinColumn(name = "businessEmail")
    private String businessEmail;

    @JoinColumn(name = "businessTelephone")
    private String businessTelephone;


    public AppointmentInfo(long id, long serviceid, LocalDateTime startDate, LocalDateTime endDate, String location, boolean confirmed,
                           String serviceName, String businessEmail, String businessTelephone ) {
        super(id, serviceid, startDate, endDate, location, confirmed);
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
