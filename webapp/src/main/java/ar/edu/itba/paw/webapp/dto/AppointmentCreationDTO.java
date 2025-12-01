package ar.edu.itba.paw.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentCreationDTO {
    @NotNull
    private LocalDateTime startDate;

    private LocalDateTime endDate;
    private String address;
    private String description;

    @NotNull
    private long userId;

    @NotNull
    private long serviceId;

    @Override
    public int hashCode(){
        return Objects.hash(userId,serviceId,startDate,endDate,address,description);
    }
}
