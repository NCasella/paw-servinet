package ar.edu.itba.paw.webapp.jersey;


import ar.edu.itba.paw.model.AvailableLanguages;
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
                                CustomMediaTypes.PASSWORD_RECOVERY))
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

    @Path("/{userid}")
    @OPTIONS
    public Response getSupportedMimeTypesForUser() {
        return Response.ok()
                .header("Accept", String.join(", ",CustomMediaTypes.USER_INFO,CustomMediaTypes.USER_CONTACT_INFO))
                .header("Accept-Patch",
                        String.join(", ",
                                CustomMediaTypes.USER_UPDATE
                        ))
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

    @PATCH
    @Path("/{userid}")
    @Consumes(value = CustomMediaTypes.USER_UPDATE)
    public Response changeUserInfo(@PathParam("userid") @NotNull final long userid, @NotNull UserPatchDTO profilePatch){
        AvailableLanguages localeParsed;
        if ( profilePatch.getLocale()!=null && !profilePatch.getLocale().isEmpty()) {
            localeParsed = AvailableLanguages.fromLanguageCode(profilePatch.getLocale());
        }else{
            localeParsed = null;
        }
        us.changeUserInfo(userid, profilePatch.getUsername(), profilePatch.getEmail(), profilePatch.getTelephone(), localeParsed, profilePatch.getProfilePicId());
        if (profilePatch.getPassword()!=null && !profilePatch.getPassword().isEmpty()) {
            us.changePassword(userid, profilePatch.getPassword());
            passRecoveryService.deleteCode(userid);
        }
        User modifiedUser = us.findById(userid).orElseThrow(UserNotFoundException::new);
        return Response.ok(UserDto.fromUser(modifiedUser, uriInfo)).build();
    }

}
