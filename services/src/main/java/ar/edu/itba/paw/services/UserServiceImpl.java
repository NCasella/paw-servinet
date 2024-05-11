package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(final UserDao userDao, final PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isProvider(long userid){
        return userDao.isProvider(userid);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }


    @Transactional
    @Override
    public void makeProvider(User user){
        boolean isProvider = isProvider(user.getUserId());
        if ( !isProvider) {
            changeUserType(user.getUserId());
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"),new SimpleGrantedAuthority("ROLE_BUSINESS"));
            org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, authorities));
        }
    }

    @Transactional
    @Override
    public User create(final String username,final String name, final String surname, final String password, final String email, final String telephone) {
        User user = userDao.findByEmail(email).orElse(null);
        /*if (user!= null){
            throw new IllegalArgumentException("User already exists");
        }
        */

        user= userDao.create(username,name,surname, passwordEncoder.encode(password), email, telephone,false);
        Set<GrantedAuthority> authorities= Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, authorities));
        return user;

    }

    @Transactional
    @Override
    public void changeUsername(long userid,String value){
        userDao.changeUsername(userid,value);
    }

    @Transactional
    @Override
    public void changeEmail(long userid,String value){
        userDao.changeEmail(userid,value);
    }

    public List<User> getUsers(List<Long> ids) {
        List<User> users = new ArrayList<>();
        for (long id : ids) {
            if (findById(id).isPresent()) {
                users.add(findById(id).get());
            }
            else users.add(null);
        }
        return users;
    }

    @Transactional
    @Override
    public void changePassword(String email,String password){
        userDao.changePassword(email,passwordEncoder.encode(password));
    }

    @Transactional
    @Override
    public void changeUserType(long userid){
        userDao.changeUserType(userid);
    }

}