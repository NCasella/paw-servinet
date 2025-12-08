package ar.edu.itba.paw.webapp.dto.input;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class BusinessCreationDTO {
    @NotEmpty
    @Size(max=255)
    private String businessName;

    @NotEmpty
    @Email
    @Size(max=255)
    private String businessEmail;

    @NotEmpty
    @Pattern(regexp = "^\\+(\\d{1,3})?\\s?9?\\s?(\\d{1,4})?\\s?(\\d{6,8})$")
    @Size(max=255)
    private String businessTelephone;

    @NotEmpty
    @Size(max=255)
    private String businessLocation;
}
