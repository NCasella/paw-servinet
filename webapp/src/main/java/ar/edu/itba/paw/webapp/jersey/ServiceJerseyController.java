package ar.edu.itba.paw.webapp.jersey;

import ar.edu.itba.paw.model.ImageModel;
import ar.edu.itba.paw.model.Service;
import ar.edu.itba.paw.model.exceptions.*;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.auth.ServinetAuthControl;
import ar.edu.itba.paw.webapp.dto.input.*;
import ar.edu.itba.paw.webapp.dto.output.ImageDto;
import ar.edu.itba.paw.webapp.dto.output.QuestionDto;
import ar.edu.itba.paw.webapp.dto.output.ReviewDto;
import ar.edu.itba.paw.webapp.dto.output.ServiceDto;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.*;
import java.util.*;

@Path("/services")
@Component
public class ServiceJerseyController {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ServiceJerseyController.class);
    private final ImageService imageService;
    @Value("resources/defaultImg.png")
    private Resource defaultImage;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;
    private final ServiceService serviceService;
    private final QuestionService questionService;
    private final RatingService ratingService;
    private final BusinessService businessService;

    private final ServinetAuthControl authControl;

    @Autowired
    public ServiceJerseyController(
            ServiceService serviceService,
            QuestionService questionService,
            RatingService ratingService,
            BusinessService businessService,
            ServinetAuthControl authControl,
            ImageService imageService
    ){
        this.authControl=authControl;
        this.serviceService=serviceService;
        this.questionService=questionService;
        this.ratingService=ratingService;
        this.businessService=businessService;
        this.imageService=imageService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServices(
            @QueryParam("businessId") Long businessId,
            @QueryParam("category") String category,
            @QueryParam("neighbourhoods") String neighbourhoods,
            @QueryParam("rating") Integer rating,
            @QueryParam("searchQuery") String searchQuery,
            @QueryParam("orderFilters") String orderFilters,
            @QueryParam("homeServiceFilter") Boolean homeServiceFilter,
            @QueryParam("page") @DefaultValue("1") final int page
    ) {

            Neighbourhoods[] neighbourhoodsEnum = QueryParamsMapper.mapNeighbourhoods(neighbourhoods);
            Categories categoryEnum = QueryParamsMapper.mapCategory(category);
            ServicesOrderFilters orderFiltersEnum = QueryParamsMapper.mapOrderFilter(orderFilters);

            if (businessId != null) businessService.findById(businessId).orElseThrow(BusinessNotFoundException::new);

            PagedList<Service> pagedList = serviceService.getServices(
                    page,
                    categoryEnum,
                    neighbourhoodsEnum,
                    rating,
                    searchQuery,
                    orderFiltersEnum,
                    homeServiceFilter,
                    businessId
            );

            List<ServiceDto> dtoList = pagedList.getList().stream()
                    .map(s -> ServiceDto.fromService(s, uriInfo))
                    .toList();

            return PagedListResponse.generate(
                    dtoList,
                    page,
                    pagedList.getTotalElements(),
                    uriInfo,
                    ServiceDto.class
            );
    }


    @POST
    @Consumes(value={MediaType.APPLICATION_JSON})
    public Response createService(final ServiceCreationDTO serviceCreationDto) {

        businessService.findById(serviceCreationDto.getBusinessId()).orElseThrow(BusinessNotFoundException::new);

        final Service service = serviceService.create(
                serviceCreationDto.getBusinessId(),
                serviceCreationDto.getServiceName(),
                serviceCreationDto.getDescription(),
                serviceCreationDto.isHomeService(),
                serviceCreationDto.getNeighbourhoods(),
                serviceCreationDto.getAddress(),
                serviceCreationDto.getCategory(),
                serviceCreationDto.getMinimalDuration(),
                serviceCreationDto.getPricingType(),
                serviceCreationDto.getPrice(),
                serviceCreationDto.isAdditionalCharges(),
                null
        );

        return Response.created(
                uriInfo.getAbsolutePathBuilder()
                        .path(String.valueOf(service.getId()))
                        .build()
        ).build();
    }

    @GET
    @Path("/{serviceid}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getService(@PathParam("serviceid") final long serviceId){
        Service service = serviceService.findById(serviceId).orElseThrow(ServiceNotFoundException::new);
        return ConditionalCache.cacheResponse(request, ServiceDto.fromService(service,uriInfo)).build();
    }

    @PATCH
    @Path("/{serviceid}")
    @Consumes(value={MediaType.APPLICATION_JSON})
    public Response changeService(
            @PathParam("serviceid") final long serviceId,
            final ServiceUpdateDTO serviceUpdateDTO
            ) {
        serviceService.editService(
                serviceId,
                serviceUpdateDTO.getDescription(),
                serviceUpdateDTO.getMinimalDuration(),
                serviceUpdateDTO.getPricingType(),
                serviceUpdateDTO.getPrice(),
                serviceUpdateDTO.getAdditionalCharges()
        );
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{serviceId}")
    public Response deleteService(@PathParam("serviceId") final long serviceId){
        serviceService.findById(serviceId).orElseThrow(ServiceNotFoundException::new);
        serviceService.delete(serviceId);
        return Response.noContent().build();
    }

    @GET
    @Path("/{serviceId}/questions")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getServiceQuestions(
            @PathParam("serviceId") final long serviceId,
            @QueryParam("page") @DefaultValue("1") final int page
    ){
        PagedList<Question> pagedList = questionService.getAllQuestions(serviceId, page);
        List<QuestionDto> dtoList = pagedList.getList().stream()
                .map(q -> QuestionDto.fromQuestion(q, uriInfo))
                .toList();

        return PagedListResponse.generate(
                dtoList,
                page,
                pagedList.getTotalElements(),
                uriInfo,
                QuestionDto.class
        );
    }

    @POST
    @Path("/{serviceId}/questions")
    @Consumes(value={MediaType.APPLICATION_JSON})
    public Response createServiceQuestion(
            @PathParam("serviceId") final long serviceId,
            final QuestionCreationDTO questionCreationDto
    ){
        User currentUser = authControl.getCurrentUser().orElseThrow(UserNotFoundException::new);
        Question question = questionService.create(
                serviceId,
                currentUser.getUserId(),
                questionCreationDto.getQuestion()
        );
        return Response.created(
                uriInfo.getAbsolutePathBuilder()
                        .path(String.valueOf(question.getId()))
                        .build()
        ).build();
    }

    @GET
    @Path("/{serviceId}/questions/{questionId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getServiceQuestionById(
            @PathParam("serviceId") final long serviceId,
            @PathParam("questionId") final long questionId
    ){
        Question question = questionService.findById(questionId).orElseThrow(QuestionNotFoundException::new);
        if (question.getServiceid() != serviceId) throw new QuestionNotFoundException();
        return ConditionalCache.cacheResponse(request, QuestionDto.fromQuestion(question, uriInfo)).build();
    }

    @PATCH
    @Path("/{serviceId}/questions/{questionId}")
    @Consumes(value={MediaType.APPLICATION_JSON})
    public Response updateQuestionResponse(
            @PathParam("serviceId") final long serviceId,
            @PathParam("questionId") final long questionId,
            final QuestionResponseDTO questionResponseDTO
            ){
        Question question = questionService.findById(questionId).orElseThrow(QuestionNotFoundException::new);
        if (question.getServiceid() != serviceId) throw new QuestionNotFoundException();
        questionService.addResponse(questionId, questionResponseDTO.getResponse());
        return Response.noContent().build();
    }

    @GET
    @Path("/{serviceId}/reviews")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getServiceReviews(
            @PathParam("serviceId") final long serviceId,
            @QueryParam("filter") String filter,
            @QueryParam("page") @DefaultValue("1") final int page
    ){
        RatingsFilters filterParsed = null;
        if (filter != null && !filter.isBlank()) filterParsed= RatingsFilters.fromValue(filter);

        PagedList<Rating> pagedList = ratingService.getAllRatings(serviceId, page, filterParsed);
        List<ReviewDto> dtoList = pagedList.getList().stream()
                .map(r -> ReviewDto.fromRating(r, uriInfo))
                .toList();

        return PagedListResponse.generate(
                dtoList,
                page,
                pagedList.getTotalElements(),
                uriInfo,
                ReviewDto.class
        );
    }

    @POST
    @Path("/{serviceId}/reviews")
    @Consumes(value={MediaType.APPLICATION_JSON})
    public Response createServiceReview(
            @PathParam("serviceId") final long serviceId,
            final ReviewCreationDTO reviewCreationDTO
    ){
        User currentUser = authControl.getCurrentUser().orElseThrow(UserNotFoundException::new);
        Rating review = ratingService.create(
                serviceId,
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

    @GET
    @Path("/{serviceId}/reviews/{reviewId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getReviewById(
            @PathParam("serviceId") final long serviceId,
            @PathParam("reviewId") final long reviewId
    ){
        Rating rating = ratingService.findById(reviewId).orElseThrow(RatingNotFoundException::new);
        if (rating.getServiceid() != serviceId) throw new RatingNotFoundException();
        return ConditionalCache.cacheResponse(request, ReviewDto.fromRating(rating, uriInfo)).build();
    }


    // TODO IMAGES

    @GET
    @Path("/{serviceid}/image")
    @Produces(value = {MediaType.MULTIPART_FORM_DATA})
    public Response getServiceImage(@PathParam("serviceid") final long serviceid){
        ImageModel imageModel = imageService.getImageById(serviceid).orElseThrow(NotFoundException::new);
        return ConditionalCache.cacheResponse(request, ImageDto.fromImage(imageModel)).build();

    }

}
