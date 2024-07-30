package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> findById(long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    String getUserLocale(long id);
    void makeProvider(User user);
    void revokeProviderRole(User user);
    User create(String username, String name,String surname, String password, String email, String telephone);
    void changeUsername(long userid,String value);
    boolean isProvider(long userid);
    boolean isVerified(long userid);
    void changeEmail(long userid,String value);
    void changePassword(String email,String value);
    void changeLocale(long userid);
    boolean verifyUser(long userid, String verificationCode);
}