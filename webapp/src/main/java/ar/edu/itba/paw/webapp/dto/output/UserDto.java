package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.jersey.PathUrls;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Objects;

@Data
@NoArgsConstructor
public class UserDto {
    private long userId;
    private String fullName;
    private String username;
    private String email;
    private String language;
    private URI self;
    private URI businessesOwned;
    private URI appointmentsRequested;
    private URI profilePicture;

    public static UserDto fromUser(User user, UriInfo uriInfo){
        UserDto toReturn = new UserDto();
        toReturn.setUserId(user.getUserId());
        toReturn.setFullName(user.getFullName());
        toReturn.setUsername(user.getUsername());
        toReturn.setEmail(user.getEmail());
        toReturn.setLanguage(user.getLocale());
        toReturn.setSelf(uriInfo.getBaseUriBuilder().path(PathUrls.USERS_URL.getUrl()).path(String.valueOf(user.getUserId())).build());
        toReturn.setBusinessesOwned(uriInfo.getBaseUriBuilder().path(PathUrls.BUSINESSES_URL.getUrl()).queryParam("ownedBy", user.getUserId()).build());
        toReturn.setAppointmentsRequested(uriInfo.getBaseUriBuilder().path(PathUrls.APPOINTMENTS_URL.getUrl()).queryParam("forUser", user.getUserId()).build());
        toReturn.setProfilePicture(uriInfo.getBaseUriBuilder().path(PathUrls.IMAGES_URL.getUrl()).path(String.valueOf(user.getProfilePicId())).build());
        return toReturn;
    }
    @Override
    public int hashCode(){
        return Objects.hash(userId,fullName,username,email,language);
    }
}
