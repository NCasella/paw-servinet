package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.model.Business;
import ar.edu.itba.paw.webapp.jersey.PathUrls;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessDto {
    private String businessName;
    private String email;
    private String telephone;
    private String address;
    private double rating;
    private URI userOwnerPath;
    private URI businessStatistics;
    private URI reviewsPath;
    private URI questionsPath;
    private URI self;

    public static BusinessDto fromBusiness(Business business, UriInfo uriInfo) {
        return BusinessDto.builder()
                .address(business.getLocation())
                .businessName(business.getName())
                .email(business.getEmail())
                .telephone(business.getTelephone())
                .questionsPath(uriInfo.getBaseUriBuilder().path(PathUrls.SERVICES_QUESTIONS_URL.getUrl()).queryParam("forBusiness",business.getUserId()).build())
                .reviewsPath(uriInfo.getBaseUriBuilder().path(PathUrls.SERVICES_URL.getUrl()).queryParam("providedBy",business.getUserId()).build())
                .rating(business.getBusinessRatingAvg())
                .userOwnerPath(uriInfo.getBaseUriBuilder().path("users").path(String.valueOf(business.getUserId())).build())
                .businessStatistics(uriInfo.getBaseUriBuilder().path(PathUrls.BUSINESSES_URL.getUrl()).path(String.valueOf(business.getBusinessid())).path(PathUrls.BUSINESSES_STATISTICS_URL.getUrl()).build())
                .self(uriInfo.getBaseUriBuilder().path(PathUrls.BUSINESSES_STATISTICS_URL.getUrl()).path(String.valueOf(business.getBusinessid())).build())
                .build();
    }

    @Override
    public int hashCode(){
        return Objects.hash(businessName,email,telephone,address,rating);
    }
}
