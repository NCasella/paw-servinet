package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
        return findById(userid).orElseThrow(UserNotFoundException::new).getProvider();
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

    @Transactional(readOnly = true)
    @Override
    public String getUserLocale(long id){
        return findById(id).orElseThrow(UserNotFoundException::new).getLocale();
    }
    @Transactional
    @Override
    public void makeProvider(User user){
        boolean isProvider = isProvider(user.getUserId());
        if ( !isProvider) {
            userDao.changeUserType(user.getUserId());
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"),new SimpleGrantedAuthority("ROLE_BUSINESS"));
            org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, authorities));
        }
    }

    @Transactional
    public void revokeProviderRole(User user){
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, authorities));
    }

    @Transactional
    @Override
    public User create(final String username,final String name, final String surname, final String password, final String email, final String telephone) {
        String locale = LocaleContextHolder.getLocale().getLanguage();

        User user= userDao.create(username,name,surname, passwordEncoder.encode(password), email, telephone,false,locale);
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


    @Transactional
    @Override
    public void changePassword(String email,String password){
        userDao.changePassword(email,passwordEncoder.encode(password));
    }

    // todo: enum de locale
    @Transactional
    @Override
    public void changeLocale(long userid) {
        String locale = getUserLocale(userid);
        locale = locale.equals("es")? "en":"es";
        userDao.changeLocale(userid,locale);
    }
}