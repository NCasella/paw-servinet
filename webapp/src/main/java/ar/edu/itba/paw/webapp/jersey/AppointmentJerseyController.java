package ar.edu.itba.paw.webapp.jersey;


import ar.edu.itba.paw.model.Appointment;
import ar.edu.itba.paw.model.exceptions.AppointmentNonExistentException;
import ar.edu.itba.paw.services.AppointmentService;
import ar.edu.itba.paw.webapp.auth.ServinetAuthControl;
import ar.edu.itba.paw.webapp.dto.AppointmentDto;
import ar.edu.itba.paw.webapp.form.AppointmentForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Optional;

@Path("appointments")
@Component
public class AppointmentJerseyController {
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    private final ServinetAuthControl authControl;
    private final AppointmentService appointmentService;
    @Autowired
    public AppointmentJerseyController(ServinetAuthControl authControl,AppointmentService appointmentService){
        this.appointmentService=appointmentService;
        this.authControl=authControl;
    }

    /*
    * FIXME: ver como pasar id del servicio.
    *   Distinguir entre alterar, confirmar y cancelar appointment para patch/put
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

}
