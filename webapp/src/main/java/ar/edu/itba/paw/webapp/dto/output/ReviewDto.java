package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.model.Rating;
import ar.edu.itba.paw.webapp.dto.output.links.ReviewLinks;
import ar.edu.itba.paw.webapp.jersey.PathUrls;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private long ratingId;
    private long serviceId;
    private long userId;
    private int rating;
    private String comment;
    private LocalDate date;

    private ReviewLinks links;

    public static ReviewDto fromRating(Rating rating, UriInfo uriInfo) {
        URI userUri=uriInfo.getBaseUriBuilder()
                .path(PathUrls.USERS_URL.getUrl())
                .path(String.valueOf(rating.getUserid()))
                .build();

        URI serviceUri=uriInfo.getBaseUriBuilder()
                .path(PathUrls.SERVICES_URL.getUrl())
                .path(String.valueOf(rating.getServiceid()))
                .build();

        URI selfUri = uriInfo.getBaseUriBuilder()
                .path(PathUrls.RATINGS_URL.getUrl())
                .path(String.valueOf(rating.getId()))
                .build();

        return ReviewDto.builder()
                .ratingId(rating.getId())
                .serviceId(rating.getServiceid())
                .userId(rating.getUserid())
                .rating(rating.getRating())
                .comment(rating.getComment())
                .date(rating.getDate())
                .links(
                    ReviewLinks.builder()
                            .user(userUri)
                            .service(serviceUri)
                            .self(selfUri)
                            .build()
                )
                .build();
    }
}
