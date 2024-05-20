package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.User;

import java.util.Optional;

public interface UserDao {
    // Data Access Object
    Optional<User> findById(long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    User create(final String username, final String name,final String surname, final String password, final String email, final String telephone, final boolean isProvider, final String locale);
    void changeEmail(long userid,String value);
    void changeUsername(long userid,String value);
    void changePassword(String email,String value);
    void changeUserType(long userid);

}
