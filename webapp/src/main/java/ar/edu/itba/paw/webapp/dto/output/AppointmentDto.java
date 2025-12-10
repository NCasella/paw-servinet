package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.model.Appointment;
import ar.edu.itba.paw.model.AppointmentStatus;
import ar.edu.itba.paw.model.exceptions.InvalidAppointmentStatusException;
import ar.edu.itba.paw.webapp.dto.output.links.AppointmentLinks;
import ar.edu.itba.paw.webapp.jersey.PathUrls;
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
    private long serviceId;
    private long userId;

    private AppointmentStatus status;

    private AppointmentLinks links;

    public static AppointmentDto fromAppointment(Appointment app, UriInfo uriInfo){
        URI userUri=uriInfo.getBaseUriBuilder()
                .path(PathUrls.USERS_URL.getUrl())
                .path(String.valueOf(app.getUserid()))
                .build();

        URI serviceUri=uriInfo.getBaseUriBuilder()
                .path(PathUrls.SERVICES_URL.getUrl())
                .path(String.valueOf(app.getServiceid()))
                .build();

        URI selfUri=uriInfo.getBaseUriBuilder()
                .path(PathUrls.APPOINTMENTS_URL.getUrl())
                .path(String.valueOf(app.getId()))
                .build();

        return AppointmentDto.builder()
                .appointmentId(app.getId())
                .startDate(app.getStartDate())
                .endDate(app.getEndDate())
                .address(app.getLocation())
                .userId(app.getUserid())
                .serviceId(app.getServiceid())
                .description(app.getDescription())
                .status(AppointmentStatus.getStatusFromAppointment(app))
                .links(
                    AppointmentLinks.builder()
                            .user(userUri)
                            .service(serviceUri)
                            .self(selfUri)
                            .build()
                )
                .build();
    }

    @Override
    public int hashCode(){
        return Objects.hash(appointmentId,startDate,endDate,address,description,status);
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
