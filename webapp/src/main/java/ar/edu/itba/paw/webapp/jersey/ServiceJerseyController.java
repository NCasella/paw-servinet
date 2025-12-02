package ar.edu.itba.paw.webapp.jersey;


import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.BusinessNotFoundException;
import ar.edu.itba.paw.model.exceptions.ServiceNotFoundException;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import ar.edu.itba.paw.services.BusinessService;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.services.ServiceService;
import ar.edu.itba.paw.webapp.auth.ServinetAuthControl;
import ar.edu.itba.paw.webapp.controller.ImageController;
import ar.edu.itba.paw.webapp.dto.ImageDto;
import ar.edu.itba.paw.webapp.dto.ServiceDto;
import ar.edu.itba.paw.webapp.form.ServiceForm;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final BusinessService businessService;

    private final ServinetAuthControl authControl;

    @Autowired
    public ServiceJerseyController(ServiceService serviceService, BusinessService businessService, ServinetAuthControl authControl,ImageService imageService){
        this.authControl=authControl;
        this.serviceService=serviceService;
        this.businessService=businessService;
        this.imageService=imageService;
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getServices(
            @QueryParam("businessId") Long businessId,
            @QueryParam("category") String category,
            @QueryParam("location") String location,
            @QueryParam("rating") String rating,
            @QueryParam("searchQuery") String searchQuery,
            @QueryParam("orderFilters") String orderFilters,
            @QueryParam("homeServiceFilter") Boolean homeServiceFilter,
            @QueryParam("page") @DefaultValue("1") final int page
    ) {

        String[] locations = (location == null || location.isBlank()) ? new String[0] : location.split(",");

        ServicesOrderFilters orderFilterParsed = null;
        if (orderFilters != null) {
            try {
                orderFilterParsed = ServicesOrderFilters.from(orderFilters);
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
            }
        }

        if(businessId != null) {
            businessService.findById(businessId).orElseThrow(BusinessNotFoundException::new);
        }

        PagedList<Service> pagedList = serviceService.getServices(page, category, locations, rating, searchQuery, orderFilterParsed, homeServiceFilter, businessId);

        final List<ServiceDto> dtoList = pagedList.getList().stream()
                .map(s -> ServiceDto.fromService(s, uriInfo))
                .collect(Collectors.toList());

        return PagedListResponse.generate(
                dtoList,
                page,
                pagedList.getTotalElements(),
                uriInfo,
                ServiceDto.class
        );

    }

    @GET
    @Path("/{serviceid}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getService(@PathParam("serviceid")final long serviceId){
        Service service = serviceService.findById(serviceId).orElseThrow(ServiceNotFoundException::new);
        return ConditionalCache.cacheResponse(request, ServiceDto.fromService(service,uriInfo)).build();
    }

    @GET
    @Path("/{serviceid}/image")
    @Produces(value = {MediaType.MULTIPART_FORM_DATA})
    public Response getServiceImage(@PathParam("serviceid") final long serviceid){
        ImageModel imageModel = imageService.getImageById(serviceid).orElseThrow(NotFoundException::new);
        return ConditionalCache.cacheResponse(request, ImageDto.fromImage(imageModel)).build();

    }

}
