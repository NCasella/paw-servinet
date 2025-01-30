package ar.edu.itba.paw.webapp.jersey;


import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/users")
@Component
public class UsersJerseyController {
    private UserService us;

    @Autowired
    public UsersJerseyController(UserService userService){
        this.us = userService;
    }

    @GET
    public void listAllUsers(){
    }
    @GET
    @Path("/{userid}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("userid") final long userid){
        final Optional<User> maybeUser = us.findById(userid);
        if (maybeUser.isEmpty()){
            return Response.status(Response.Status.NOT_FOUND).build();
        }else{
            return Response.ok(UserDto.fromUser(maybeUser.get())).build();
        }

    }
}
