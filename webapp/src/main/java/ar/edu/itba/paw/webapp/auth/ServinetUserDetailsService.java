package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

@Component
public class ServinetUserDetailsService implements UserDetailsService {

    private final UserService us;
    private final Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2(a|y|b)?\\$\\d\\d\\$[./0-9A-Za-z]{53}");


    @Autowired
    public ServinetUserDetailsService(UserService us) {
        this.us = us;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = us.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException( "no user for email" + email));

        if(!BCRYPT_PATTERN.matcher(user.getPassword()).matches()){
            us.changePassword(user.getUserId(),user.getPassword());
            return loadUserByUsername(email);
        }

        final Collection<GrantedAuthority> authorities = new HashSet<>();
        //check if user is verified
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if(us.isProvider(user.getUserId()))
            authorities.add(new SimpleGrantedAuthority("ROLE_BUSINESS"));
        return new ServinetAuthUserDetails(user.getEmail(), user.getPassword(),user.getIsVerified(),true,true,true, authorities);
    }


}

