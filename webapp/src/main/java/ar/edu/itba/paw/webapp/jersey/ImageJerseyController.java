package ar.edu.itba.paw.webapp.jersey;

import ar.edu.itba.paw.model.ImageModel;
import ar.edu.itba.paw.services.ImageService;
import ar.edu.itba.paw.model.exceptions.NotFoundException;
import ar.edu.itba.paw.webapp.validation.ValidImageFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;


import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
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

    @OPTIONS
    public Response getSupportedMimeTypesForServices() {
        return Response.ok()
                .header("Allow", "GET, POST, OPTIONS")
                .header("Accept", MediaType.MULTIPART_FORM_DATA)
                .header("Accept-Post", MediaType.MULTIPART_FORM_DATA)
                .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                .build();
    }

    @GET
    @Path("/{imageId}")
    @Produces(value={MediaType.IMAGE_PNG_VALUE,MediaType.IMAGE_JPEG_VALUE})
    public Response getImage(@PathParam("imageId") long imageId){
        ImageModel image=imageService.getImageById(imageId).orElseThrow(NotFoundException::new);
        byte[] imageContent=image.getImageBytes();
        return ConditionalCache.cacheResponseFromHashCode(request,imageContent, Arrays.hashCode(imageContent)).build();
    }

    @POST
    @Consumes(value={MediaType.IMAGE_JPEG_VALUE,MediaType.IMAGE_PNG_VALUE})
    public Response createImage(@ValidImageFile byte[] imageBytes){
        ImageModel createdImage=imageService.addImage(imageBytes);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(createdImage.getImageId())).build()).build();
    }
}
