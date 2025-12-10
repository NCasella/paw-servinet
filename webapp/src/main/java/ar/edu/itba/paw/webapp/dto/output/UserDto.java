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
    private String language;

    private UserLinks links;

    public static UserDto fromUser(User user, UriInfo uriInfo){
        URI businessesOwnedUri = uriInfo.getBaseUriBuilder()
                .path(PathUrls.BUSINESSES_URL.getUrl())
                .queryParam("ownedBy", user.getUserId())
                .build();

        URI appointmentsRequestedUri = uriInfo.getBaseUriBuilder()
                .path(PathUrls.APPOINTMENTS_URL.getUrl())
                .queryParam("userId", user.getUserId())
                .build();

        URI questionsToRespondUri = uriInfo.getBaseUriBuilder()
                .path(PathUrls.QUESTIONS_URL.getUrl())
                .queryParam("respondentId", user.getUserId())
                .build();

        URI self = uriInfo.getBaseUriBuilder()
                .path(PathUrls.USERS_URL.getUrl())
                .path(String.valueOf(user.getUserId()))
                .build();

        URI profilePicUri = uriInfo.getBaseUriBuilder()
                .path(PathUrls.IMAGES_URL.getUrl())
                .path(String.valueOf(user.getProfilePicId().orElse(null)))
                .build();

        UserLinks.UserLinksBuilder linksBuilder = UserLinks.builder()
                .businessesOwned(businessesOwnedUri)
                .appointmentsRequested(appointmentsRequestedUri)
                .questionsToRespond(questionsToRespondUri)
                .self(self);

        if (user.getProfilePicId().isPresent()){
            linksBuilder.profilePic(profilePicUri);
        }

        return UserDto.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .language(user.getLocale())
                .links(linksBuilder.build())
                .build();
    }
    @Override
    public int hashCode(){
        return Objects.hash(userId,fullName,username,language);
    }
}
