package ar.edu.itba.paw.webapp.jersey;


import ar.edu.itba.paw.model.Appointment;
import ar.edu.itba.paw.model.AppointmentStatus;
import ar.edu.itba.paw.model.PagedList;
import ar.edu.itba.paw.model.exceptions.*;
import ar.edu.itba.paw.services.AppointmentService;
import ar.edu.itba.paw.services.BusinessService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.auth.ServinetAuthControl;
import ar.edu.itba.paw.webapp.dto.input.AppointmentCreationDTO;
import ar.edu.itba.paw.webapp.dto.output.AppointmentDto;
import ar.edu.itba.paw.webapp.dto.input.AppointmentStatusDTO;
import ar.edu.itba.paw.webapp.mediaType.CustomMediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;


@Path("appointments")
@Component
public class AppointmentJerseyController {
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    private final ServinetAuthControl authControl;
    private final AppointmentService appointmentService;
    private final UserService userService;
    private final BusinessService businessService;
    private final Logger LOGGER = LoggerFactory.getLogger(AppointmentJerseyController.class);

    @Autowired
    public AppointmentJerseyController(ServinetAuthControl authControl,
                                       AppointmentService appointmentService,
                                       UserService userService,
                                       BusinessService businessService) {
        this.appointmentService=appointmentService;
        this.authControl=authControl;
        this.userService=userService;
        this.businessService=businessService;
    }

    /*
    * FIXME: ver como pasar id del servicio.
    *
    * @POST
    *
    */

    @OPTIONS
    public Response getSupportedAppointmentsMimeTypes() {
        return Response.ok()
                .header("Allow", "GET, POST, OPTIONS")
                .header("Accept", CustomMediaTypes.APPOINTMENT_LIST)
                .header("Accept-Post", CustomMediaTypes.APPOINTMENT_CREATION)
                .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                .build();
    }

    @GET
    @Produces(value = {CustomMediaTypes.APPOINTMENT_LIST})
    public Response getAppointments(
            @QueryParam("userId") Long userId,
            @QueryParam("businessId") Long businessId,
            @QueryParam("status") @DefaultValue(AppointmentStatus.DEFAULT_VALUE) String status,
            @QueryParam("page") @DefaultValue("1") final int page
    ){
        AppointmentStatus statusEnum = AppointmentStatus.toEnum(status);
        if ((userId == null && businessId == null) || (userId != null && businessId != null) || page<1
                || (businessId!=null && statusEnum==AppointmentStatus.FINISHED ))
            throw new InvalidFilterException();

        PagedList<Appointment> pagedList;
        if (userId != null) {
            // cuando agreguemos auth:
            // authControl.getCurrentUser().ifPresentOrElse(u -> u.getUserId(), ForbiddenOperationException::new);
            userService.findById(userId).orElseThrow(UserNotFoundException::new);
            pagedList = appointmentService.getUserAppointments(userId,statusEnum , page);
        } else {
            businessService.findById(businessId).orElseThrow(BusinessNotFoundException::new);
            pagedList = appointmentService.getBusinessAppointments(businessId, statusEnum, page);
        }
        final List<AppointmentDto> allAppointments = pagedList.getList().stream()
                .map(a -> AppointmentDto.fromAppointment(a,uriInfo) ).collect(Collectors.toList());

        return PagedListResponse.generate(allAppointments,page,pagedList.getTotalElements(),uriInfo, AppointmentDto.class,request);
    }

    @POST
    @Consumes(value = {CustomMediaTypes.APPOINTMENT_CREATION})
    public Response createAppointment(@Valid final AppointmentCreationDTO appointmentCreationDto){
        Appointment app = appointmentService.create(appointmentCreationDto.getServiceId(), appointmentCreationDto.getUserId(), appointmentCreationDto.getAddress(), appointmentCreationDto.getStartDate(),appointmentCreationDto.getDescription());
        return Response.created(
                uriInfo.getAbsolutePathBuilder().path(String.valueOf(app.getId())).build()
                ).build();
    }

    @Path("/{appointmentid}")
    @OPTIONS
    public Response getSupportedAppointmentMimeTypes() {
        return Response.ok()
                .header("Allow", "GET, PATCH, OPTIONS")
                .header("Accept", CustomMediaTypes.APPOINTMENT_INFO)
                .header("Accept-Patch", CustomMediaTypes.APPOINTMENT_STATUS)
                .header("Access-Control-Allow-Methods", "GET, PATCH, OPTIONS")
                .build();
    }

    @GET
    @Path("/{appointmentid}")
    @Produces(value = {CustomMediaTypes.APPOINTMENT_INFO})
    public Response getAppointment(@PathParam("appointmentid")final long appointmentId){
        Appointment app=appointmentService.findById(appointmentId).orElseThrow(AppointmentNonExistentException::new);
        return ConditionalCache.cacheResponse(request, AppointmentDto.fromAppointment(app,uriInfo)).build();
    }

    @PATCH
    @Path("/{appointmentid}")
    @Consumes(value = {CustomMediaTypes.APPOINTMENT_STATUS})
    public Response changeAppointmentStatus(@PathParam("appointmentid")final long appointmentId,final AppointmentStatusDTO appointmentStatusDTO) {
        if (!appointmentStatusDTO.hasValidStatus() || appointmentStatusDTO.hasFinished() || appointmentStatusDTO.isPending() )
            throw new InvalidOperationException("Invalid status change");
        appointmentService.changePendingAppointmentStatus(appointmentId,appointmentStatusDTO.getStatusEnum());
        return Response.noContent().build();
    }
}

