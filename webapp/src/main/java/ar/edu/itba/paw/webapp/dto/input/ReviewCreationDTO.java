package ar.edu.itba.paw.webapp.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreationDTO {
    @NotNull
    private long serviceId;

    @Min(value=1)
    @Max(value=5)
    private int rating;

    @Size(max=255)
    private String comment;
}
