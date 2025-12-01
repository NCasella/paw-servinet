package ar.edu.itba.paw.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCreationDTO {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String address;
    private String description;

    private long userId;
    private long serviceId;

    @Override
    public int hashCode(){
        return Objects.hash(userId,serviceId,startDate,endDate,address,description);
    }
}
