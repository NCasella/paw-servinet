package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.model.Service;
import ar.edu.itba.paw.webapp.dto.output.links.ServiceLinks;
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
    private long businessId;

    private String serviceName;
    private boolean homeService;

    private String[] neighbourhoods;
    private String address;
    private double rating;
    private String description;
    private int duration;
    private boolean additionalCosts;
    private String price;
    private String category;
    private String pricingType;

    private ServiceLinks links;

    public static ServiceDto fromService(Service service, UriInfo uriInfo){
        URI self = uriInfo.getBaseUriBuilder()
                .path(PathUrls.SERVICES_URL.getUrl())
                .path(String.valueOf(service.getId()))
                .build();

        URI business = uriInfo.getBaseUriBuilder()
                .path(PathUrls.BUSINESSES_URL.getUrl())
                .path(String.valueOf(service.getBusinessid()))
                .build();

        URI questions = uriInfo.getBaseUriBuilder()
                .path(PathUrls.QUESTIONS_URL.getUrl())
                .queryParam("serviceId", service.getId())
                .build();

        URI reviews = uriInfo.getBaseUriBuilder()
                .path(PathUrls.RATINGS_URL.getUrl())
                .queryParam("serviceId", service.getId())
                .build();

        ServiceLinks links = ServiceLinks.builder()
                .self(self)
                .business(business)
                .questions(questions)
                .reviews(reviews)
                .build();

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
                .neighbourhoods(service.getNeighbourhoodAvailable().toArray(String[]::new))
                .links(links)
                .build();
    }

    @Override
    public int hashCode(){
        return Objects.hash(serviceName,homeService, Arrays.hashCode(neighbourhoods),address,rating,description,duration,additionalCosts,price,pricingType);
    }
}
