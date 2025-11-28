package ar.edu.itba.paw.webapp.jersey;


import ar.edu.itba.paw.model.ImageModel;
import ar.edu.itba.paw.model.Service;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.exceptions.ServiceNotFoundException;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
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
import java.util.Optional;

@Path("/services")
@Component
public class ServiceJerseyController {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ServiceJerseyController.class);
    private final ImageService is;
    @Value("resources/defaultImg.png")
    private Resource defaultImage;
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;
    private final ServiceService ss;
    private final ServinetAuthControl authControl;

    @Autowired
    public ServiceJerseyController(ServiceService ss,ServinetAuthControl authControl,ImageService is){
        this.authControl=authControl;
        this.ss=ss;
        this.is=is;
    }

    @GET
    @Path("/{serviceid}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getService(@PathParam("serviceid")final long serviceId){
        Service service=ss.findById(serviceId).orElseThrow(ServiceNotFoundException::new);
        return ConditionalCache.cacheResponse(request, ServiceDto.fromService(service,uriInfo)).build();
    }
    @GET
    @Path("/{serviceid}/image")
    @Produces(value = {MediaType.MULTIPART_FORM_DATA})
    public Response getServiceImage(@PathParam("serviceid") final long serviceid){
        ImageModel imageModel=is.getImageById(serviceid).orElseThrow(NotFoundException::new);
        return ConditionalCache.cacheResponse(request, ImageDto.fromImage(imageModel)).build();

    }

}
