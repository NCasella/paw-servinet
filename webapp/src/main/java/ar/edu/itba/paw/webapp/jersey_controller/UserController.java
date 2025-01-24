package ar.edu.itba.paw.webapp.jersey_controller;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("users")
@Component
public class UserController {

    @Autowired
    private UserService us;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listUsers(@QueryParam("page") @DefaultValue("1") final int page) {
        final List<UserDto> allUsers =new ArrayList<User>().stream()                   //! us.getAll();
                .map(UserDto.mapper(uriInfo)).collect(Collectors.toList());
        return Response.ok(new GenericEntity<List<UserDto>>(allUsers) {})
                .link(URI.create(""),"prev").link(URI.create(""),"next")
                .link(URI.create(""),"first").link(URI.create(""),"last")
            .build();
    }

    @POST
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    public Response createUser(final UserDto userDto) {
        final User newUser = us.create(userDto.getUsername(), userDto.getPassword(),"","","","");
        return Response.created( uriInfo.getAbsolutePathBuilder()
                .path("users")
                .path(String.valueOf(newUser.getUserId())).build()).build();
    }

     
    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getById(@PathParam("id") final long id) {
        final Optional<UserDto> maybeUser = us.findById(id).map(UserDto.mapper(uriInfo));
        if (maybeUser.isEmpty())
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(maybeUser.get()).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(@PathParam("id") final long id) {
        //us.deleteById(id);
        return Response.noContent().build();
    }
}