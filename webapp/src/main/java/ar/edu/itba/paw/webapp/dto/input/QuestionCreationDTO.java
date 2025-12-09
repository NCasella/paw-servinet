package ar.edu.itba.paw.webapp.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCreationDTO {
    @NotNull
    private long serviceId;

    @NotEmpty
    @Size(max=255)
    private String question;
}
