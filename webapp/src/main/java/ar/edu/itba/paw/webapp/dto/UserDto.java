package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public static UserDto fromUser(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .language(user.getLocale())
                .build();
    }

    @Override
    public int hashCode(){
        return Objects.hash(userId,fullName,username,email,language);
    }
}
