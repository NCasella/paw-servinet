package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.model.Service;
import ar.edu.itba.paw.webapp.jersey.PathUrls;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Arrays;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceDto {
    private long serviceId;
    private String serviceName;
    private boolean homeService;
    private URI businessUri;

    private URI self;
    private String[] neighbourhoods;
    private String address;
    private double rating;
    private String description;
    private int duration;
    private boolean additionalCosts;
    private String price;
    private String category;
    private String pricingType;

    public static ServiceDto fromService(Service service, UriInfo uriInfo){
        return ServiceDto.builder()
                .serviceId(service.getId())
                .serviceName(service.getName())
                .homeService(service.getHomeService())
                .address(service.getLocation())
                .description(service.getDescription())
                .duration(service.getDuration())
                .price(service.getPrice())
                .category(service.getCategory().getValue())
                .pricingType(service.getPricing().getValue())
                .businessUri(uriInfo.getBaseUriBuilder()
                        .path(PathUrls.BUSINESSES_URL.getUrl())
                        .path(String.valueOf(service.getBusinessid()))
                        .build())
                .neighbourhoods(service.getNeighbourhoodAvailable().toArray(String[]::new))
                .self(uriInfo.getAbsolutePathBuilder()
                        .path(PathUrls.SERVICES_URL.getUrl())
                        .path(String.valueOf(service.getId()))
                        .build())
                .build();
    }

    @Override
    public int hashCode(){
        return Objects.hash(serviceName,homeService, Arrays.hashCode(neighbourhoods),address,rating,description,duration,additionalCosts,price,pricingType);
    }
}
