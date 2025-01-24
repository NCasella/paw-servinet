package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.User;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.function.Function;

public class UserDto {
    private String username;
    private String password; // WRITE-ONLY -> setter
    private URI self;

    public UserDto() {}

    public UserDto(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
    }

    // funcion util para simplificar codigo en el Controller (curri)
    public static Function<User, UserDto> mapper(final UriInfo uriInfo) {
        return u -> fromUser(uriInfo,u);
    }

    public static UserDto fromUser(final UriInfo uriInfo, User user) {
        final UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.self = uriInfo.getBaseUriBuilder()
                .path("users").path(String.valueOf(user.getUserId())).build();
        return userDto;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public URI getSelf() {
        return self;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}
