package ar.edu.itba.paw.webapp.dto.input;

import ar.edu.itba.paw.model.Service;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class QuestionCreationDTO {
    @NotEmpty
    @NotNull
    private long serviceId;

    @NotEmpty
    @NotNull
    @Size(max=255)
    private String question;
}
