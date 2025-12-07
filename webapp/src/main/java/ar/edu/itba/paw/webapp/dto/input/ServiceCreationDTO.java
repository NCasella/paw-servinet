package ar.edu.itba.paw.webapp.dto.input;

import ar.edu.itba.paw.model.Categories;
import ar.edu.itba.paw.model.Neighbourhoods;
import ar.edu.itba.paw.model.PricingTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCreationDTO {

    @NotNull
    private Long businessId;

    @Size(max=255)
    @NotEmpty
    @NotNull
    private String serviceName;

    @Size(max=255)
    private String description;

    @NotNull
    private boolean homeService;


    private String[] neighbourhoods;

    @Size(max=255)
    private String address;

    @Size(max=10)
    @Pattern(regexp = "[0-9]+(\\.[0-9]{1,2})?$")
    private String price;

    private boolean additionalCharges;

    @NotNull
    private String pricingType;

    @NotNull
    private String category;

    @Positive
    private int minimalDuration;
    @Positive
    private long imageId;
}
