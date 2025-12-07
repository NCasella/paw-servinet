package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.model.Business;
import ar.edu.itba.paw.webapp.dto.output.links.BusinessLinks;
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
    private long businessId;
    private String businessName;
    private String email;
    private String telephone;
    private String address;
    private double rating;

    private BusinessLinks links;

    public static BusinessDto fromBusiness(Business business, UriInfo uriInfo) {
        URI userOwnerUri = uriInfo.getBaseUriBuilder()
                .path("users")
                .path(String.valueOf(business.getUserId()))
                .build();

        URI businessStatisticsUri = uriInfo.getBaseUriBuilder()
                .path(PathUrls.BUSINESSES_URL.getUrl())
                .path(String.valueOf(business.getBusinessid()))
                .path(PathUrls.BUSINESSES_STATISTICS_URL.getUrl())
                .build();

        URI selfUri = uriInfo.getBaseUriBuilder()
                .path(PathUrls.BUSINESSES_URL.getUrl())
                .path(String.valueOf(business.getBusinessid()))
                .build();
        return BusinessDto.builder()
                .businessId(business.getBusinessid())
                .address(business.getLocation())
                .businessName(business.getName())
                .email(business.getEmail())
                .telephone(business.getTelephone())
                .rating(business.getBusinessRatingAvg())
                .links(
                    BusinessLinks.builder()
                            .userOwner(userOwnerUri)
                            .businessStatistics(businessStatisticsUri)
                            .self(selfUri)
                            .build()
                )
                .build();
    }

    @Override
    public int hashCode(){
        return Objects.hash(businessName,email,telephone,address,rating);
    }
}
