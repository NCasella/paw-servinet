package ar.edu.itba.paw.webapp.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class QuestionResponseDTO {
    @NotEmpty(message = "NotEmpty.responseForm.response")
    @NotNull(message = "NotEmpty.responseForm.response")
    @Size(max=255,message = "Size.responseForm.response")
    private String response;
}
