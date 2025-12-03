package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.model.Appointment;
import ar.edu.itba.paw.model.AppointmentStatus;
import ar.edu.itba.paw.model.exceptions.InvalidAppointmentStatusException;
import ar.edu.itba.paw.webapp.jersey.PathUrls;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Data
@NoArgsConstructor
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

    //@JsonSetter("status")
    public void setStatus(String status) {
        this.status = AppointmentStatus.toEnum(status);
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

   // @JsonProperty("status")
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
}
