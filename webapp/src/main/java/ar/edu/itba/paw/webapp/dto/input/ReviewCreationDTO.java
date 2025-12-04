package ar.edu.itba.paw.webapp.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class ReviewCreationDTO {
    @Min(value=1)
    @Max(value=5)
    private int rating;

    @Size(max=255)
    private String comment;
}
