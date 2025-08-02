package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Appointment;
import ar.edu.itba.paw.webapp.jersey.PathUrls;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Objects;

public class AppointmentDto {
    private LocalDateTime start;
    private LocalDateTime endDate;
    private String address;
    private String description;

    private boolean confirmed;

    private URI service;
    private URI userRequester;

    public static AppointmentDto fromAppointment(Appointment app, UriInfo uriInfo){
        AppointmentDto toRet=new AppointmentDto();
        toRet.setAddress(app.getLocation());
        toRet.setStart(app.getStartDate());
        toRet.setConfirmed(app.getConfirmed());
        toRet.setDescription(app.getDescription());

        toRet.setUserRequester(uriInfo.getBaseUriBuilder().path(PathUrls.USERS_URL.getUrl()).path(String.valueOf(app.getUserid())).build());
        toRet.setService(uriInfo.getBaseUriBuilder().path(PathUrls.SERVICES_URL.getUrl()).path(String.valueOf(app.getServiceid())).build());

        return toRet;
    }

    @Override
    public int hashCode(){
        return Objects.hash(start,endDate,address,description,confirmed);
    }
    private AppointmentDto(){}
    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
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

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
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

}
