package ar.edu.itba.paw.webapp.dto.input;

import ar.edu.itba.paw.model.Neighbourhoods;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
public class AppointmentCreationDTO {
    @Future
    @NotNull
    private LocalDateTime startDate;

    @Size(max = 255)
    private String address;

    @Size(max = 255)
    private String description;

    private String neighborhood;

    @NotNull
    private long userId;

    @NotNull
    private long serviceId;

    @Override
    public int hashCode(){
        return Objects.hash(userId,serviceId,startDate,address,description,neighborhood);
    }
}
