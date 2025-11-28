package ar.edu.itba.paw.webapp.jersey;


import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
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
        int total = us.getUserCount();
        return PagedListResponse.generate(allUsers,page,total,uriInfo,UserDto.class);
    }

    @GET
    @Path("/{userid}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("userid") final long userid){
        final User maybeUser = us.findById(userid).orElseThrow(UserNotFoundException::new);
        return ConditionalCache.cacheResponse(request,UserDto.fromUser(maybeUser)).build();
    }
}
