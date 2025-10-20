package ar.edu.itba.paw.webapp.jersey;


import ar.edu.itba.paw.model.Appointment;
import ar.edu.itba.paw.model.AppointmentStatus;
import ar.edu.itba.paw.model.PagedList;
import ar.edu.itba.paw.model.exceptions.*;
import ar.edu.itba.paw.services.AppointmentService;
import ar.edu.itba.paw.services.BusinessService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.auth.ServinetAuthControl;
import ar.edu.itba.paw.webapp.dto.AppointmentCreationDTO;
import ar.edu.itba.paw.webapp.dto.AppointmentDto;
import ar.edu.itba.paw.webapp.dto.AppointmentStatusDTO;
import ar.edu.itba.paw.webapp.mapper.ExceptionToStatusMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("appointments")
@Component
public class AppointmentJerseyController {
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    private ExceptionToStatusMapper exceptionToStatusMapper;

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

    @GET
    @Path("/{appointmentid}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getAppointment(@PathParam("appointmentid")final long appointmentId){
        Optional<Appointment> app=appointmentService.findById(appointmentId);
        if(app.isEmpty()){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return ConditionalCache.cacheResponse(request, AppointmentDto.fromAppointment(app.get(),uriInfo)).build();
    }

    @POST
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response createAppointment(final AppointmentCreationDTO appointmentCreationDto){
        Appointment app = appointmentService.create(appointmentCreationDto.getServiceId(), appointmentCreationDto.getUserId(), appointmentCreationDto.getAddress(), appointmentCreationDto.getStartDate(),appointmentCreationDto.getDescription());
        return Response.created(
                uriInfo.getAbsolutePathBuilder().path(String.valueOf(app.getId())).build()
                ).build();
    }

    @PATCH
    @Path("/{appointmentid}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response changeAppointmentStatus(@PathParam("appointmentid")final long appointmentId,final AppointmentStatusDTO appointmentStatusDTO) {
        if (!appointmentStatusDTO.hasValidStatus() || appointmentStatusDTO.isPending() || appointmentStatusDTO.hasFinished())
            return Response.status(Response.Status.BAD_REQUEST ).build();
        appointmentService.changePendingAppointmentStatus(appointmentId,appointmentStatusDTO.getStatusEnum());
        return Response.noContent().build();
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
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

        return PagedListResponse.generate(allAppointments,page,pagedList.getTotalElements(),uriInfo, AppointmentDto.class);
    }



}

