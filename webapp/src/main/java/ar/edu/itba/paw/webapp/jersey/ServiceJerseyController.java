package ar.edu.itba.paw.webapp.jersey;

import ar.edu.itba.paw.model.Service;
import ar.edu.itba.paw.model.exceptions.*;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.auth.ServinetAuthControl;
import ar.edu.itba.paw.webapp.dto.input.*;
import ar.edu.itba.paw.webapp.dto.output.ServiceDto;
import ar.edu.itba.paw.webapp.mediaType.CustomMediaTypes;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

@Path("/services")
@Component
public class ServiceJerseyController {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ServiceJerseyController.class);

    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;
    private final ServiceService serviceService;

    private final ServinetAuthControl authControl;

    @Autowired
    public ServiceJerseyController(
            ServiceService serviceService,
            ServinetAuthControl authControl
    ){
        this.authControl=authControl;
        this.serviceService=serviceService;
    }

    @OPTIONS
    public Response getSupportedMimeTypesForServices() {
        return Response.ok()
                .header("Allow", "GET, POST, OPTIONS")
                .header("Accept", CustomMediaTypes.SERVICE_LIST)
                .header("Accept-Post", CustomMediaTypes.SERVICE_CREATION)
                .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                .build();
    }


    @GET
    @Produces(value = CustomMediaTypes.SERVICE_LIST)
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
        Map<String, Object> queryParams=new HashMap<>();
        queryParams.put("category",category);
        queryParams.put("neighbourhoods",neighbourhoods);
        queryParams.put("rating",rating);
        queryParams.put("searchQuery",searchQuery);
        queryParams.put("orderFilters",orderFilters);
        queryParams.put("homeServiceFilter", homeServiceFilter);

            List<ServiceDto> dtoList = pagedList.getList().stream()
                    .map(s -> ServiceDto.fromService(s, uriInfo))
                    .toList();


            return PagedListResponse.generate(
                    dtoList,
                    page,
                    pagedList.getTotalElements(),
                    uriInfo,
                    ServiceDto.class,
                    request,
                    queryParams);
    }


    @POST
    @Consumes(value = CustomMediaTypes.SERVICE_CREATION)
    public Response createService(@Valid final ServiceCreationDTO serviceCreationDto) {
        Categories categoryParsed = Categories.fromName(serviceCreationDto.getCategory());
        PricingTypes pricingTypeParsed = PricingTypes.fromName(serviceCreationDto.getPricingType());
        Neighbourhoods[] neighbourhoodsParsed = Arrays.stream(serviceCreationDto.getNeighbourhoods())
                .map(Neighbourhoods::fromName)
                .toArray(Neighbourhoods[]::new);

        final Service service = serviceService.create(
                serviceCreationDto.getBusinessId(),
                serviceCreationDto.getServiceName(),
                serviceCreationDto.getDescription(),
                serviceCreationDto.isHomeService(),
                neighbourhoodsParsed,
                serviceCreationDto.getAddress(),
                categoryParsed,
                serviceCreationDto.getMinimalDuration(),
                pricingTypeParsed,
                serviceCreationDto.getPrice(),
                serviceCreationDto.isAdditionalCosts(),
                serviceCreationDto.getImageId()
        );

        return Response.created(
                uriInfo.getAbsolutePathBuilder()
                        .path(String.valueOf(service.getId()))
                        .build()
        ).build();
    }

    @Path("/{serviceId}")
    @OPTIONS
    public Response getSupportedMimeTypesForService() {
        return Response.ok()
                .header("Allow", "GET, PATCH, OPTIONS")
                .header("Accept", CustomMediaTypes.SERVICE_INFO)
                .header("Accept-Patch", CustomMediaTypes.SERVICE_UPDATE)
                .header("Access-Control-Allow-Methods", "GET, PATCH, OPTIONS")
                .build();
    }

    @GET
    @Path("/{serviceid}")
    @Produces(value = CustomMediaTypes.SERVICE_INFO)
    public Response getService(@PathParam("serviceid") final long serviceId){
        Service service = serviceService.findById(serviceId).orElseThrow(ServiceNotFoundException::new);
        return ConditionalCache.cacheResponse(request, ServiceDto.fromService(service,uriInfo)).build();
    }

    @PATCH
    @Path("/{serviceid}")
    @Consumes(value = CustomMediaTypes.SERVICE_UPDATE)
    public Response changeService(
            @PathParam("serviceid") final long serviceId,
            @Valid final ServiceUpdateDTO serviceUpdateDTO
    ) {
        PricingTypes pricingTypeParsed = PricingTypes.fromName(serviceUpdateDTO.getPricingType());
        serviceService.editService(
                serviceId,
                serviceUpdateDTO.getDescription(),
                serviceUpdateDTO.getMinimalDuration(),
                pricingTypeParsed,
                serviceUpdateDTO.getPrice(),
                serviceUpdateDTO.getAdditionalCosts()
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
}
