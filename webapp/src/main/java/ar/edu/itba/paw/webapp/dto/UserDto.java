package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.User;

import java.util.Objects;

public class UserDto {
    private long userId;
    private String fullName;
    private String username;
    private String email;
    private String language;

    public static UserDto fromUser(User user){
        UserDto toReturn = new UserDto();
        toReturn.setUserId(user.getUserId());
        toReturn.setFullName(user.getFullName());
        toReturn.setUsername(user.getUsername());
        toReturn.setEmail(user.getEmail());
        toReturn.setLanguage(user.getLocale());
        return toReturn;
    }
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public int hashCode(){
        return Objects.hash(userId,fullName,username,email,language);
    }
}
