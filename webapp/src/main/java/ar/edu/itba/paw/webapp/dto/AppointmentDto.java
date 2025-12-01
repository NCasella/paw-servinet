package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Appointment;
import ar.edu.itba.paw.model.AppointmentStatus;
import ar.edu.itba.paw.model.exceptions.InvalidAppointmentStatusException;
import ar.edu.itba.paw.webapp.jersey.PathUrls;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    public static AppointmentDto fromAppointment(Appointment app, UriInfo uriInfo) {
        return AppointmentDto.builder()
                .appointmentId(app.getId())
                .startDate(app.getStartDate())
                .endDate(app.getStartDate()) // <-- Revisá si no es un bug
                .address(app.getLocation())
                .description(app.getDescription())
                .status(AppointmentStatus.getStatusFromAppointment(app))
                .userRequester(uriInfo.getBaseUriBuilder()
                        .path(PathUrls.USERS_URL.getUrl())
                        .path(String.valueOf(app.getUserid()))
                        .build())
                .service(uriInfo.getBaseUriBuilder()
                        .path(PathUrls.SERVICES_URL.getUrl())
                        .path(String.valueOf(app.getServiceid()))
                        .build())
                .self(uriInfo.getBaseUriBuilder()
                        .path(PathUrls.APPOINMENTS_URL.getUrl())
                        .path(String.valueOf(app.getId()))
                        .build())
                .build();
    }

    @Override
    public int hashCode(){
        return Objects.hash(appointmentId,startDate,endDate,address,description,status,service,userRequester,self);
    }

    @JsonSetter("status")
    public void setStatus(String status) {
        this.status = AppointmentStatus.toEnum(status);
    }

    @JsonProperty("status")
    public String getStatus() {
        return Optional.ofNullable(status)
                .orElseThrow(InvalidAppointmentStatusException::new)
                .getValue();
    }
}
