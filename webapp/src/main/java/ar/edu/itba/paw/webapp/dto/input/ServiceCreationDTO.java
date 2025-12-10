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

    @Size(max=255,message = "Size.serviceForm.title")
    @NotEmpty(message = "NotEmpty.serviceForm.title")
    @NotNull(message = "NotNull.serviceForm.title")
    private String serviceName;

    @Size(max=255,message = "Size.serviceForm.description")
    private String description;

    @NotNull(message = "NotNull.serviceForm.homeserv")
    private boolean homeService;


    private String[] neighbourhoods;

    @Size(max=255,message = "Size.serviceForm.location")
    private String address;

    @Size(max=10,message = "Size.serviceForm.price")
    @Pattern(regexp = "[0-9]+(\\.[0-9]{1,2})?$",message = "Pattern.serviceForm.price")
    private String price;

    private boolean additionalCharges;

    @NotNull
    private String pricingType;

    @NotNull
    private String category;

    @Positive(message = "Positive.serviceForm.minimalduration")
    private int minimalDuration;
    @Positive
    private Long imageId;
}
