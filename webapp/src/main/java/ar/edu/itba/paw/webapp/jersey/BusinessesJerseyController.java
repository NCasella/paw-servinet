package ar.edu.itba.paw.webapp.jersey;


import ar.edu.itba.paw.model.Business;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.exceptions.BusinessNotFoundException;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import ar.edu.itba.paw.services.BusinessService;
import ar.edu.itba.paw.webapp.auth.ServinetAuthControl;
import ar.edu.itba.paw.webapp.dto.BusinessDto;
import ar.edu.itba.paw.webapp.form.BusinessForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Optional;

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

    @POST
    @Consumes(value={MediaType.APPLICATION_JSON})//FIXME: validacion
    public Response createBusiness(@Valid final BusinessForm businessForm){
        User currentUser=authControl.getCurrentUser().orElseThrow(UserNotFoundException::new);
        final Business business=businessService.createBusiness(businessForm.getBusinessName(),currentUser.getUserId()
                ,businessForm.getBusinessTelephone(),businessForm.getBusinessEmail(),businessForm.getBusinessLocation());
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(business.getBusinessid())).build()).build();
    }

    @GET
    @Path("/{businessid}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getBusiness(@PathParam("businessid") final long businessid) {
        Business business = businessService.findById(businessid).orElseThrow(BusinessNotFoundException::new);
        return ConditionalCache.cacheResponse(request,BusinessDto.fromBusiness(business,uriInfo)).build();
    }

}
