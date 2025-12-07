package ar.edu.itba.paw.webapp.jersey;

import ar.edu.itba.paw.model.ImageModel;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.model.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.ValidImageFile;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;

@Path("/images")
@Component
public class ImageJerseyController {
    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;

    private final ImageService imageService;

    @Autowired
    public ImageJerseyController(ImageService imageService){
        this.imageService=imageService;
    }

    @GET
    @Path("/{imageId}")
    @Produces(value={MediaType.MULTIPART_FORM_DATA})
    public Response getImage(@PathParam("imageId") long imageId){
        ImageModel image=imageService.getImageById(imageId).orElseThrow(NotFoundException::new);
        byte[] imageContent=image.getImageBytes();
        return ConditionalCache.cacheResponseFromHashCode(request,imageContent, Arrays.hashCode(imageContent)).build();
    }

    @POST
    @Consumes(value = MediaType.MULTIPART_FORM_DATA)
    public Response createImage(@ValidImageFile @FormDataParam("image") FormDataBodyPart image){
        ImageModel createdImage=imageService.addImage(image.getEntityAs(byte[].class));
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(createdImage.getImageId())).build()).build();
    }
}
