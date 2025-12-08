package ar.edu.itba.paw.webapp.jersey;

import ar.edu.itba.paw.model.PagedList;
import ar.edu.itba.paw.model.Rating;
import ar.edu.itba.paw.model.RatingsFilters;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.exceptions.RatingNotFoundException;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.auth.ServinetAuthControl;
import ar.edu.itba.paw.webapp.dto.input.ReviewCreationDTO;
import ar.edu.itba.paw.webapp.dto.output.ReviewDto;
import ar.edu.itba.paw.webapp.mediaType.CustomMediaTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/reviews")
@Component
public class RatingsController {

    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;
    private final RatingService ratingService;
    private final ServinetAuthControl authControl;

    @Autowired
    public RatingsController(
            RatingService ratingService,
            ServinetAuthControl authControl
    ) {
        this.authControl = authControl;
        this.ratingService = ratingService;
    }

    @OPTIONS
    public Response getSupportedMimeTypesForReviews() {
        return Response.ok()
                .header("Allow", "GET, POST, OPTIONS")
                .header("Accept", CustomMediaTypes.REVIEW_LIST)
                .header("Accept-Post", CustomMediaTypes.REVIEW_CREATION)
                .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                .build();
    }

    @GET
    @Produces(value = CustomMediaTypes.REVIEW_LIST)
    public Response getReviews(
            @QueryParam("serviceId") final Long serviceId,
            @QueryParam("filter") String filter,
            @QueryParam("page") @DefaultValue("1") final int page
    ){
        RatingsFilters filterParsed = null;
        if (filter != null && !filter.isBlank()) filterParsed= RatingsFilters.fromValue(filter);

        PagedList<Rating> pagedList;
        if(serviceId != null) {
            pagedList = ratingService.getRatingsByService(serviceId, page, filterParsed);
        } else {
            pagedList = ratingService.getAllRatings(page, filterParsed);
        }
        List<ReviewDto> dtoList = pagedList.getList().stream()
                .map(r -> ReviewDto.fromRating(r, uriInfo))
                .toList();

        return PagedListResponse.generate(
                dtoList,
                page,
                pagedList.getTotalElements(),
                uriInfo,
                ReviewDto.class,
                request
        );
    }

    @POST
    @Consumes(value = CustomMediaTypes.REVIEW_CREATION)
    public Response createReview(@Valid final ReviewCreationDTO reviewCreationDTO) {
        User currentUser = authControl.getCurrentUser().orElseThrow(UserNotFoundException::new);
        Rating review = ratingService.create(
                reviewCreationDTO.getServiceId(),
                currentUser.getUserId(),
                reviewCreationDTO.getRating(),
                reviewCreationDTO.getComment()
        );
        return Response.created(
                uriInfo.getAbsolutePathBuilder()
                        .path(String.valueOf(review.getId()))
                        .build()
        ).build();
    }

    @Path("/{reviewId}")
    @OPTIONS
    public Response getSupportedMimeTypesForReview() {
        return Response.ok()
                .header("Allow", "GET, OPTIONS")
                .header("Accept", CustomMediaTypes.REVIEW_INFO)
                .header("Access-Control-Allow-Methods", "GET, OPTIONS")
                .build();
    }


    @GET
    @Path("/{reviewId}")
    @Produces(value = CustomMediaTypes.REVIEW_INFO)
    public Response getReviewById(
            @PathParam("reviewId") final long reviewId
    ){
        Rating rating = ratingService.findById(reviewId).orElseThrow(RatingNotFoundException::new);
        return ConditionalCache.cacheResponse(request, ReviewDto.fromRating(rating, uriInfo)).build();
    }
}
