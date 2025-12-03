package ar.edu.itba.paw.webapp.dto.input;

import ar.edu.itba.paw.model.Appointment;
import ar.edu.itba.paw.webapp.jersey.PathUrls;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Objects;

public class AppointmentCreationDTO {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String address;
    private String description;

    private long userId;
    private long serviceId;

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    public long getUserId() {
        return userId;
    }

    public long getServiceId() {
        return serviceId;
    }

    private AppointmentCreationDTO(){}

    @Override
    public int hashCode(){
        return Objects.hash(userId,serviceId,startDate,endDate,address,description);
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime start) {
        this.startDate = start;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
