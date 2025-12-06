package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserVerificationCode;
import ar.edu.itba.paw.model.exceptions.InvalidPasswordModificationException;
import ar.edu.itba.paw.model.exceptions.InvalidUsernameException;
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


import java.util.*;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final UserVerificationService userVerificationService;

    @Autowired
    public UserServiceImpl(final UserDao userDao, final PasswordEncoder passwordEncoder, final UserVerificationService userVerificationService){
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.userVerificationService= userVerificationService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers(int page) {
        return userDao.getUsers(page);
    }
    @Override
    @Transactional(readOnly = true)
    public int getUserCount() {
        return userDao.getUserCount();
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isProvider(long userid) {
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
    public String getUserLocale(long id) {
        return findById(id).orElseThrow(UserNotFoundException::new).getLocale();
    }
    @Transactional
    @Override
    public void makeProvider(User user) {
        boolean isProvider = isProvider(user.getUserId());
        if ( !isProvider) {
            userDao.changeUserType(user.getUserId());
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"),new SimpleGrantedAuthority("ROLE_BUSINESS"));
            org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, authorities));
        }
    }

    @Transactional
    public void revokeProviderRole(User user) {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, authorities));
    }
    @Transactional(readOnly = true)
    @Override
    public boolean isUserProvidee(long providerUserId, long requestUserId){
    return userDao.isUserProvidee(providerUserId,requestUserId);
    }
    @Transactional
    @Override
    public User create(final String username,final String name, final String surname, final String password, final String email, final String telephone) {
        String locale = LocaleContextHolder.getLocale().getLanguage();

        User user= userDao.create(username,name,surname, passwordEncoder.encode(password), email, telephone,false,locale);
        //userVerificationService.sendVerificationCode(user);

        return user;

    }

    @Transactional
    @Override
    public void changeUsername(long userid, String value) {
        userDao.findById(userid).orElseThrow(UserNotFoundException::new);
        if (userDao.findByUsername(value).isPresent()){
            throw new InvalidUsernameException();
        }
        userDao.changeUsername(userid,value);
    }

    @Transactional
    @Override
    public void changeEmail(long userid, String value) {
        userDao.findById(userid).orElseThrow(UserNotFoundException::new);
        userDao.changeEmail(userid,value);
    }

    @Transactional
    @Override
    public void changeTelephone(long userid, String value) {
        userDao.findById(userid).orElseThrow(UserNotFoundException::new);
        userDao.changeTelephone(userid,value);
    }


    @Transactional
    @Override
    public void changePassword(long userid, String password) {
        userDao.findById(userid).orElseThrow(UserNotFoundException::new);
        userDao.changePassword(userid ,passwordEncoder.encode(password));
    }

    @Transactional
    @Override
    public void changeUserInfo(long userid, String username, String email, String telephone) {
        changeUsername(userid, username);
        changeEmail(userid, email);
        changeTelephone(userid, telephone);
    }

    @Transactional
    @Override
    public void changePassword(long userid,String oldPassword, String newPassword){
        User user = userDao.findById(userid).orElseThrow(UserNotFoundException::new);
        String storedPassword = user.getPassword();
        boolean verified = passwordEncoder.matches(oldPassword, storedPassword);
        if (!verified){
            throw new InvalidPasswordModificationException();
        }
        userDao.changePassword(userid,passwordEncoder.encode(newPassword));
    }


    // todo: enum de locale
    @Transactional
    @Override
    public void changeLocale(long userid) {
        userDao.findById(userid).orElseThrow(UserNotFoundException::new);
        String locale = getUserLocale(userid);
        locale = locale.equals("es")? "en":"es";
        userDao.changeLocale(userid,locale);
    }

    @Transactional
    @Override
    public boolean verifyUser(UUID tokenUrl, String verificationCode){
        if(userVerificationService.verifyUser(tokenUrl, verificationCode)) {
            Optional<UserVerificationCode> userVerificationCode = userVerificationService.getUserVerificationCodeByTokenUrl(tokenUrl);
            if (userVerificationCode.isEmpty()){
                return false;
            }
            User user =userVerificationCode.get().getRequestedBy();
            long userid = user.getUserId();
            userDao.verifyUser(userid);
            userVerificationService.deleteCode(userid);
            Set<GrantedAuthority> authorities= Set.of(new SimpleGrantedAuthority("ROLE_USER"));
            org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, authorities));return true;
        }
        //agregar cambio de roles
        return false;
    }
}