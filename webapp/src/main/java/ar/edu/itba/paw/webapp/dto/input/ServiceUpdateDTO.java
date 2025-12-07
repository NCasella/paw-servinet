package ar.edu.itba.paw.webapp.dto.input;

import ar.edu.itba.paw.model.PricingTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceUpdateDTO {

    @Size(max=255)
    private String description;

    private String pricingType;

    @Size(max=10)
    @Pattern(regexp = "[0-9]+(\\.[0-9]{1,2})?$")
    private String price;

    private Boolean additionalCharges;

    @Positive
    private int minimalDuration;
}
