package ar.edu.itba.paw.webapp.jersey;


import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.dto.UserCreationDTO;
import ar.edu.itba.paw.webapp.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("users")
@Component
public class UsersJerseyController {
    private final UserService us;

    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;
    @Autowired
    public UsersJerseyController(UserService userService){
        this.us = userService;
    }

    @POST
    @Consumes(value = { MediaType.APPLICATION_JSON })
    public Response createUser(final UserCreationDTO userForm) {
        final User newUser = us.create(userForm.getUsername(), userForm.getName(),userForm.getSurname(), userForm.getPassword(), userForm.getEmail(), userForm.getTelephone());
        return Response.created( uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(newUser.getUserId())).build()).build();
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response listUsers(@QueryParam("page") @DefaultValue("1") final int page) {
        final List<UserDto> allUsers = us.getAllUsers(page).stream()
                .map(UserDto::fromUser).collect(Collectors.toList());
        int prev = page-1 > 0 ? page-1 : page;
        int total = us.getUserCount();
        int max =  total % 10 == 0 ? total / 10 : (total / 10) + 1;
        int next = page + 1 <= max ? page + 1 : page;
        return Response.ok(new GenericEntity<List<UserDto>>(allUsers) {})
                .link(String.valueOf(total), "total")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", prev).build(),"prev")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", next).build(),"next")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", 1).build(),"first")
                .link(uriInfo.getAbsolutePathBuilder().queryParam("page", max).build(),"last")
                .build();
    }

    @GET
    @Path("/{userid}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("userid") final long userid){
        final Optional<User> maybeUser = us.findById(userid);
        if (maybeUser.isEmpty()){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return ConditionalCache.cacheResponse(request,UserDto.fromUser(maybeUser.get())).build();
    }
}
