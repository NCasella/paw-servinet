package ar.edu.itba.paw.webapp.jersey;

import ar.edu.itba.paw.webapp.dto.output.IndexDto;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
@Component
public class ServinetJerseyController {
    public ServinetJerseyController(){}

    @GET
    public Response listAllPaths(){
        IndexDto index = new IndexDto();
        index.setUsers("http://localhost:8080/webapp_war/api/users");
        index.setBusinesses("http://localhost:8080/webapp_war/api/businesses");
        index.setServices("http://localhost:8080/webapp_war/api/services");
        index.setAppointments("http://localhost:8080/webapp_war/api/appointments");
        index.setImages("http://localhost:8080/webapp_war/api/images");
        return Response.ok(index).build();
    }

}
