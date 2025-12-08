package ar.edu.itba.paw.webapp.jersey;


import ar.edu.itba.paw.model.Business;
import ar.edu.itba.paw.model.PagedList;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.exceptions.BusinessNotFoundException;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import ar.edu.itba.paw.services.BusinessService;
import ar.edu.itba.paw.webapp.auth.ServinetAuthControl;
import ar.edu.itba.paw.webapp.dto.input.BusinessCreationDTO;
import ar.edu.itba.paw.webapp.dto.input.BusinessUpdateDTO;
import ar.edu.itba.paw.webapp.dto.output.BusinessDto;
import ar.edu.itba.paw.webapp.mediaType.CustomMediaTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/businesses")
@Component
public class BusinessesJerseyController {

    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;
    private final BusinessService businessService;
    private final ServinetAuthControl authControl;

    @Autowired
    public BusinessesJerseyController(BusinessService businessService,ServinetAuthControl authControl) {
        this.businessService = businessService;
        this.authControl=authControl;
    }

    @OPTIONS
    public Response getSupportedMimeTypesForBusinesses() {
        return Response.ok()
                .header("Allow", "GET, POST, OPTIONS")
                .header("Accept-Post", CustomMediaTypes.BUSINESS_CREATION)
                .header("Accept", CustomMediaTypes.BUSINESS_LIST)
                .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                .build();
    }

    @GET
    @Produces(value = {CustomMediaTypes.BUSINESS_LIST})
    public Response getAllBusinesses(
            @QueryParam("ownerId") final Long ownerId,
            @QueryParam("page") @DefaultValue("1") final int page
    ) {
        PagedList<Business> pagedList;
        if(ownerId != null) {
            pagedList = businessService.getBusinessesByUser(ownerId, page);
        } else {
            pagedList =  businessService.getAllBusinesses(page);
        }

        List<BusinessDto> dtoList = pagedList.getList().stream()
                .map(b -> BusinessDto.fromBusiness(b, uriInfo))
                .toList();

        return PagedListResponse.generate(
                dtoList,
                page,
                pagedList.getTotalElements(),
                uriInfo,
                BusinessDto.class,
                request
        );
    }

    @POST
    @Consumes(value={CustomMediaTypes.BUSINESS_CREATION})
    public Response createBusiness(@Valid final BusinessCreationDTO businessCreationDTO){
        User currentUser = authControl.getCurrentUser().orElseThrow(UserNotFoundException::new);
        final Business business = businessService.createBusiness(
                businessCreationDTO.getBusinessName(),
                currentUser.getUserId(),
                businessCreationDTO.getBusinessTelephone(),
                businessCreationDTO.getBusinessEmail(),
                businessCreationDTO.getBusinessLocation()
        );
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(business.getBusinessid())).build()).build();
    }

    @Path("/{businessId}")
    @OPTIONS
    public Response getSupportedMimeTypesForBusiness() {
        return Response.ok()
                .header("Allow", "GET, PATCH, DELETE, OPTIONS")
                .header("Accept", CustomMediaTypes.BUSINESS_INFO)
                .header("Accept-Patch", CustomMediaTypes.BUSINESS_UPDATE)
                .header("Access-Control-Allow-Methods", "GET, PATCH, DELETE, OPTIONS")
                .build();
    }

    @GET
    @Path("/{businessId}")
    @Produces(value = {CustomMediaTypes.BUSINESS_INFO})
    public Response getBusiness(@PathParam("businessId") final long businessId) {
        Business business = businessService.findById(businessId).orElseThrow(BusinessNotFoundException::new);
        return ConditionalCache.cacheResponse(request,BusinessDto.fromBusiness(business,uriInfo)).build();
    }

    @PATCH
    @Path("/{businessId}")
    @Consumes(value = {CustomMediaTypes.BUSINESS_UPDATE})
    public Response editBusiness(
            @PathParam("businessId") final long businessId,
            @Valid final BusinessUpdateDTO businessUpdateDTO
    ) {
        businessService.editBusiness(
                businessId,
                businessUpdateDTO.getBusinessTelephone(),
                businessUpdateDTO.getBusinessEmail(),
                businessUpdateDTO.getBusinessLocation()
        );
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{businessId}")
    public Response deleteBusiness(@PathParam("businessId") final long businessId) {
        businessService.deleteBusiness(businessId);
        return Response.noContent().build();
    }
}
