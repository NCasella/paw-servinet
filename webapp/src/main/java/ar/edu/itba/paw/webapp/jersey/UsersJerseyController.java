package ar.edu.itba.paw.webapp.jersey;


import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import ar.edu.itba.paw.services.PasswordRecoveryCodeService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.dto.output.UserContactDto;
import ar.edu.itba.paw.webapp.mediaType.CustomMediaTypes;
import ar.edu.itba.paw.webapp.dto.input.*;
import ar.edu.itba.paw.webapp.dto.output.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("users")
@Component
public class UsersJerseyController {
    private final UserService us;
    private final PasswordRecoveryCodeService passRecoveryService;

    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;
    @Autowired
    public UsersJerseyController(UserService userService, PasswordRecoveryCodeService passRecoveryService){
        this.us = userService;
        this.passRecoveryService = passRecoveryService;
    }

    @OPTIONS
    public Response getSupportedMimeTypes() {
        return Response.ok()
                .header("Allow", "POST, OPTIONS")
                .header("Accept-Post",
                        String.join(", ",
                                CustomMediaTypes.USER_REGISTRATION,
                                CustomMediaTypes.PASSWORD_RECOVERY,
                                CustomMediaTypes.PASSWORD_RESET))
                .header("Access-Control-Allow-Methods", "POST, OPTIONS")
                .build();
    }

    @POST
    @Consumes(value = {CustomMediaTypes.USER_REGISTRATION})
    public Response createUser(@NotNull @Valid final UserCreationDTO userForm) {
        final User newUser = us.create(userForm.getUsername(), userForm.getName(),userForm.getSurname(), userForm.getPassword(), userForm.getEmail(), userForm.getTelephone());
        return Response.created( uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(newUser.getUserId())).build()).build();
    }

    //password recovery
    @POST
    @Consumes(value = {CustomMediaTypes.PASSWORD_RECOVERY} )
    public Response requestPasswordRecovery(@NotNull @Valid PasswordRecoveryRequestDTO recoveryEmail){
        passRecoveryService.sendCode(recoveryEmail.getEmail());
        return Response.ok().build();
    }

    @POST
    @Consumes(value= {CustomMediaTypes.PASSWORD_RESET})
    public Response resetPassword(@NotNull @Valid PasswordResetDTO passwordEdit){
        passRecoveryService.changePassword(passwordEdit.getCodeAsUuid(), passwordEdit.getNewPassword());
        return Response.ok().build();
    }

    @Path("/{userid}")
    @OPTIONS
    public Response getSupportedMimeTypesForUser() {
        return Response.ok()
                .header("Accept", "application/vnd.users.user-info.v1+json")
                .header("Accept-Patch",
                        String.join(", ",
                                CustomMediaTypes.USER_PATCH,
                                CustomMediaTypes.PASSWORD_MODIFICATION))
                .header("Access-Control-Allow-Methods", "GET, POST, PATCH, OPTIONS")
                .build();
    }

    @GET
    @Path("/{userid}")
    @Produces(value = CustomMediaTypes.USER_CONTACT_INFO)
    public Response getUserWithContact(@PathParam("userid") final long userid){
        final User user = us.findById(userid).orElseThrow(UserNotFoundException::new);
        return ConditionalCache.cacheResponse(request, UserContactDto.fromUser(user,uriInfo)).build();
    }
    @GET
    @Path("/{userid}")
    @Produces(value = CustomMediaTypes.USER_INFO)
    public Response getUser(@PathParam("userid") final long userid){
        final User user = us.findById(userid).orElseThrow(UserNotFoundException::new);
        return ConditionalCache.cacheResponse(request,UserDto.fromUser(user, uriInfo)).build();
    }

    //patches durante la sesion (TODO: agregar desde security)
    @PATCH
    @Path("/{userid}")
    @Consumes(value = CustomMediaTypes.USER_PATCH)
    public Response changeUserInfo(@PathParam("userid") @NotNull final long userid, @NotNull @Valid UserPatchDTO profilePatch){
        final User user = us.findById(userid).orElseThrow(UserNotFoundException::new);
        us.changeUserInfo(userid, profilePatch.getUsername(), profilePatch.getEmail(), profilePatch.getTelephone());
        User modifiedUser = us.findById(userid).orElseThrow(UserNotFoundException::new);
        return Response.ok(UserDto.fromUser(modifiedUser, uriInfo)).build();
    }

    @PATCH
    @Path("/{userid}")
    @Consumes(value = CustomMediaTypes.PASSWORD_MODIFICATION)
    public Response changePassword(@PathParam("userid") @NotNull final long userid, @NotNull @Valid UserPasswordModificationDTO passwordModification){
        final User user = us.findById(userid).orElseThrow(UserNotFoundException::new);
        us.changePassword(userid,passwordModification.getOldPassword(), passwordModification.getNewPassword());
        return Response.ok().build();
    }

    //todo: add profilepic
}
