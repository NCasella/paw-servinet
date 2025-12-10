package ar.edu.itba.paw.webapp.dto.input;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class BusinessUpdateDTO {
    @NotEmpty(message = "NotEmpty.BusinessForm.businessEmail")
    @Email(message = "Email.BusinessForm.businessEmail")
    @Size(max=255,message = "Size.BusinessForm.businessEmail")
    private String businessEmail;

    @NotEmpty(message = "NotEmpty.BusinessForm.businessTelephone")
    @Pattern(regexp = "^\\+(\\d{1,3})?\\s?9?\\s?(\\d{1,4})?\\s?(\\d{6,8})$",message = "Pattern.BusinessForm.businessTelephone")
    @Size(max=255,message = "Size.BusinessForm.businessTelephone")
    private String businessTelephone;

    @NotEmpty(message = "NotEmpty.BusinessForm.businessLocation")
    @Size(max=255,message = "Size.BusinessForm.businessLocation")
    private String businessLocation;
}
