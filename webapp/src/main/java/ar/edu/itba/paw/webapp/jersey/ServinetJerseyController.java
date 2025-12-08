package ar.edu.itba.paw.webapp.jersey;

import ar.edu.itba.paw.webapp.dto.output.IndexDto;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/")
@Component
public class ServinetJerseyController {
    @Context
    private UriInfo uriInfo;

    public ServinetJerseyController(){}

    @GET
    public Response listAllPaths(){
        String base = uriInfo.getBaseUri().toString();

        IndexDto index = new IndexDto();
        index.setUsers(base + "users");
        index.setBusinesses(base + "businesses");
        index.setServices(base + "services");
        index.setAppointments(base + "appointments");
        index.setQuestions(base + "questions");
        index.setReviews(base + "reviews");
        index.setImages(base + "images");
        return Response.ok(index).build();
    }

}
