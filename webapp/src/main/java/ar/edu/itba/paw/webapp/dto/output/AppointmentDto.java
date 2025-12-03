package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.model.Appointment;
import ar.edu.itba.paw.model.AppointmentStatus;
import ar.edu.itba.paw.model.exceptions.InvalidAppointmentStatusException;
import ar.edu.itba.paw.webapp.jersey.PathUrls;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class AppointmentDto {
    private long appointmentId;


    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String address;
    private String description;

    private AppointmentStatus status;

    private URI service;
    private URI userRequester;
    private URI self;

    public static AppointmentDto fromAppointment(Appointment app, UriInfo uriInfo){
        AppointmentDto toRet=new AppointmentDto();
        toRet.setAddress(app.getLocation());
        toRet.setStartDate(app.getStartDate());
        toRet.setEndDate(app.getStartDate());
        toRet.setStatus(AppointmentStatus.getStatusFromAppointment(app));
        toRet.setDescription(app.getDescription());
        toRet.setAppointmentId(app.getId());

        toRet.setUserRequester(uriInfo.getBaseUriBuilder().path(PathUrls.USERS_URL.getUrl()).path(String.valueOf(app.getUserid())).build());
        toRet.setService(uriInfo.getBaseUriBuilder().path(PathUrls.SERVICES_URL.getUrl()).path(String.valueOf(app.getServiceid())).build());
        toRet.setSelf(uriInfo.getBaseUriBuilder().path(PathUrls.APPOINTMENTS_URL.getUrl()).path(String.valueOf(app.getId())).build());

        return toRet;
    }


    @Override
    public int hashCode(){
        return Objects.hash(appointmentId,startDate,endDate,address,description,status,service,userRequester,self);
    }
    private AppointmentDto(){}

    public long getAppointmentId() {return appointmentId;}
    public void setAppointmentId(long appointmentId) {this.appointmentId = appointmentId;}

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

    @JsonSetter("status")
    public void setStatus(String status) {
        this.status = AppointmentStatus.toEnum(status);
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    @JsonProperty("status")
    public String getStatus() {
        return Optional.ofNullable(status)
                .orElseThrow(InvalidAppointmentStatusException::new)
                .getValue();
    }

    public AppointmentStatus getStatusEnum() {
        return status;
    }

    public boolean hasValidStatus() {
        return status != null;
    }

    public URI getService() {
        return service;
    }

    public void setService(URI service) {
        this.service = service;
    }

    public URI getUserRequester() {
        return userRequester;
    }

    public void setUserRequester(URI userRequester) {
        this.userRequester = userRequester;
    }

    public URI getSelf(){return this.self;}
    public void setSelf(URI uri){this.self=uri;}
}
