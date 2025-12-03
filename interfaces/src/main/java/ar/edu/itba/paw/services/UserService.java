package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    List<User> getAllUsers(int page);
    int getUserCount();
    Optional<User> findById(long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    String getUserLocale(long id);
    void makeProvider(User user);
    void revokeProviderRole(User user);
    User create(String username, String name,String surname, String password, String email, String telephone);
    void changeUserInfo(long userid,String username, String email, String telephone);

    void changeUsername(long userid,String value);
    boolean isProvider(long userid);
    void changeEmail(long userid,String value);
    void changeTelephone(long userid,String value);

    void changePassword(long userid,String value);
    void changePassword(long userid,String oldPass, String newPass);

    void changeLocale(long userid);
    boolean verifyUser(UUID tokenUrl, String verificationCode);
}