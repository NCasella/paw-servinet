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
import ar.edu.itba.paw.webapp.dto.input.ReviewUpdateDTO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Map<String,Object> queryParamsForLinks=new HashMap<>();
        queryParamsForLinks.put("serviceId",serviceId);
        queryParamsForLinks.put("filter",filter);
        return PagedListResponse.generate(
                dtoList,
                page,
                pagedList.getTotalElements(),
                uriInfo,
                ReviewDto.class,
                request,
                queryParamsForLinks
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
                .header("Allow", "GET, PATCH, OPTIONS")
                .header("Accept", CustomMediaTypes.REVIEW_INFO)
                .header("Accept-Patch", CustomMediaTypes.REVIEW_UPDATE)
                .header("Access-Control-Allow-Methods", "GET, PATCH, OPTIONS")
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

    @PATCH
    @Path("/{reviewId}")
    @Consumes(value = CustomMediaTypes.REVIEW_UPDATE)
    public Response updateReview(
            @PathParam("reviewId") final long reviewId,
            @Valid final ReviewUpdateDTO reviewUpdateDTO
    ){
        ratingService.findById(reviewId).orElseThrow(RatingNotFoundException::new);
        ratingService.edit(
                reviewId,
                reviewUpdateDTO.getRating(),
                reviewUpdateDTO.getComment()
        );
        return Response.noContent().build();
    }
}
