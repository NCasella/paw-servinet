package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.dto.output.links.UserLinks;
import ar.edu.itba.paw.webapp.jersey.PathUrls;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private long userId;
    private String fullName;
    private String username;
    private String email;
    private String language;

    private UserLinks links;

    public static UserDto fromUser(User user, UriInfo uriInfo){
        URI businessesOwnedUri = uriInfo.getBaseUriBuilder()
                .path(PathUrls.BUSINESSES_URL.getUrl())
                .queryParam("ownedBy", user.getUserId())
                .build();

        URI appointmentsRequestedUri = uriInfo.getBaseUriBuilder()
                .path(PathUrls.APPOINTMENTS_URL.getUrl())
                .queryParam("forUser", user.getUserId())
                .build();

        URI profilePictureUri = uriInfo.getBaseUriBuilder()
                .path(PathUrls.IMAGES_URL.getUrl())
                .path(String.valueOf(user.getProfilePicId()))
                .build();

        URI self = uriInfo.getBaseUriBuilder()
                .path(PathUrls.USERS_URL.getUrl())
                .path(String.valueOf(user.getUserId()))
                .build();

        return UserDto.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .language(user.getLocale())
                .links(
                    UserLinks.builder()
                            .businessesOwned(businessesOwnedUri)
                            .appointmentsRequested(appointmentsRequestedUri)
                            .profilePicture(profilePictureUri)
                            .self(self)
                            .build()

                )
                .build();
    }
    @Override
    public int hashCode(){
        return Objects.hash(userId,fullName,username,email,language);
    }
}
