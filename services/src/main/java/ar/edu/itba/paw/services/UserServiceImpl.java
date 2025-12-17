package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.AvailableLanguages;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserVerificationCode;
import ar.edu.itba.paw.model.exceptions.InvalidOperationException;
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
    private final static String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$" ;
    private final static String TELEPHONE_REGEX = "^\\+(\\d{1,3})?\\s?9?\\s?(\\d{1,4})?\\s?(\\d{6,8})$";
    private final static int MAX_SIZE = 256;

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
    public boolean isVerified(long userid){
        return findById(userid).orElseThrow(UserNotFoundException::new).getIsVerified();
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
        userVerificationService.sendVerificationCode(user);
        Set<GrantedAuthority> authorities= Set.of(new SimpleGrantedAuthority("ROLE_UNVERIFIED_USER"));
        org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, authorities));

        return user;
    }

    @Transactional
    @Override
    public void changeUsername(long userid, String value) {
        userDao.findById(userid).orElseThrow(UserNotFoundException::new);
        findByUsername(value).ifPresent(existingUser -> {
            if(existingUser.getUserId() != userid){
                throw new InvalidUsernameException();
            }
        });
        userDao.changeUsername(userid,value);
    }

    @Transactional
    public void changeProfilePic(long userid, long profilePicId) {
        userDao.findById(userid).orElseThrow(UserNotFoundException::new);
        userDao.changeProfilePicId(userid,profilePicId);
    }

    @Transactional
    @Override
    public void changeEmail(long userid, String value) {
        User user = userDao.findById(userid).orElseThrow(UserNotFoundException::new);
        findByEmail(value).ifPresent(existingUser -> {
            if(existingUser.getUserId() != userid){
                throw new InvalidOperationException("Email already in use");
            }
        });
        if(!user.getEmail().equals(value)){
            userDao.changeEmail(userid, value);
        }
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
    public void changeUserInfo(long userid, String username, String email, String telephone, AvailableLanguages locale, Long profilePicId) {
        if ( profilePicId != null ) {
            changeProfilePic(userid, profilePicId);
            return;
        }
        if (!username.isEmpty() && username.length() < MAX_SIZE){
            changeUsername(userid, username);
        }
        if (!email.isEmpty() && email.length() < MAX_SIZE && email.matches(EMAIL_REGEX) && findByEmail(email).isEmpty()){
            changeEmail(userid, email);
        }
        if (!telephone.isEmpty() && telephone.length() < MAX_SIZE && telephone.matches(TELEPHONE_REGEX)){
            changeTelephone(userid, telephone);
        }
        if (locale != null) {
            changeLocale(userid, locale);
        }
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
    public void changeLocale(long userid, AvailableLanguages locale) {
        userDao.findById(userid).orElseThrow(UserNotFoundException::new);
        userDao.changeLocale(userid, locale);
    }

    @Transactional
    @Override
    public boolean verifyUser(long userId, String verificationCode){
        if(userVerificationService.verifyUser(userId, verificationCode)) {
            Optional<UserVerificationCode> userVerificationCode = userVerificationService.getUserVerificationCodeByUserId(userId);
            if (userVerificationCode.isEmpty()){
                return false;
            }
            User user =userVerificationCode.get().getRequestedBy();
            long userid = user.getUserId();
            userDao.verifyUser(userid);
            userVerificationService.deleteCode(userid);
            Set<GrantedAuthority> authorities= Set.of(new SimpleGrantedAuthority("ROLE_USER"));
            org.springframework.security.core.userdetails.User userDetails =
                    new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, authorities));
            return true;
        }
        return false;
    }
}