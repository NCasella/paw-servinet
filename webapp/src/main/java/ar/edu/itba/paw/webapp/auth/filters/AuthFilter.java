package ar.edu.itba.paw.webapp.auth.filters;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.services.PasswordRecoveryCodeService;
import ar.edu.itba.paw.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Component
public class AuthFilter extends OncePerRequestFilter {
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private AuthenticationEntryPoint authEntryPoint;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService us;
    @Autowired
    private PasswordRecoveryCodeService passRecoveryService;
    @Autowired
    private UserDetailsService userDetailsService;

    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_SCHEME = "Basic";
    private static final String CREDENTIALS_SEPARATOR = ":";
    private static final int CREDENTIALS_NUMBER = 2;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
                throws ServletException, IOException {
            String header = req.getHeader(AUTH_HEADER);

            if (header != null && header.startsWith(AUTH_SCHEME)) {
                String base64Cred = header.substring(AUTH_SCHEME.length() + 1).trim();
                String creds = new String(Base64.getDecoder().decode(base64Cred), StandardCharsets.UTF_8);
                String[] parts = creds.split(CREDENTIALS_SEPARATOR, CREDENTIALS_NUMBER);

                if (parts.length != CREDENTIALS_NUMBER) {
                    chain.doFilter(req, res);
                    return;
                }

                final String identity = parts[0]; // email
                final String credential = parts[1]; // password o token

                Authentication auth = null;
                boolean isPasswordRecovery = true;
                UUID recoveryCode = UUID.randomUUID();

                try {
                    UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(identity, credential);
                    auth = authManager.authenticate(authReq);

                } catch (AuthenticationException standardAuthException) {

                    try {
                        Optional<User> maybeUser = us.findByEmail(identity);
                        if (maybeUser.isPresent()) {
                            final User user = maybeUser.get();
                            boolean verificationSuccess = false;
                            try {
                                recoveryCode = UUID.fromString(credential);
                            } catch (IllegalArgumentException e){
                                isPasswordRecovery= false;
                            }

                            if (!isPasswordRecovery && us.verifyUser(user.getUserId(), credential)){
                                verificationSuccess = true;
                            }

                            else if ( isPasswordRecovery && passRecoveryService.validateCode(identity, recoveryCode)) {
                                verificationSuccess = true;
                            }

                            if (verificationSuccess) {
                                final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
                                Collection<GrantedAuthority> authorities;
                                if (isPasswordRecovery){
                                    authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_PASSWORD_RESET"));
                                    passRecoveryService.deleteCode(user.getUserId());
                                }else{
                                    authorities = new HashSet<>(userDetails.getAuthorities());
                                }
                                auth = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                            } else {
                                // Si falla la autenticación estándar y los flujos especiales
                                throw standardAuthException;
                            }
                        } else {
                            // El usuario no existe por username/email, relanzar excepción original
                            throw standardAuthException;
                        }
                    } catch (AuthenticationException e) {
                        // Manejo del error final
                        SecurityContextHolder.clearContext();
                        authEntryPoint.commence(req, res, e);
                        return;
                    }
                }

                if (auth != null && auth.isAuthenticated()) {
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    final UserDetails userDetails = (UserDetails) auth.getPrincipal();

                    String accessToken = jwtUtil.generateToken(userDetails);
                    String refreshToken = jwtUtil.generateRefreshToken(userDetails);

                    res.setHeader("Authorization-Access-Token", "Bearer " + accessToken);
                    res.setHeader("Authorization-Refresh-Token", "Bearer " + refreshToken);

                    res.setStatus(HttpServletResponse.SC_OK);
                    chain.doFilter(req,res);
                    return;
                }
            }

            //Continuo si no se encontró el header Basic o si el flujo de autenticación terminó sin éxito
            chain.doFilter(req, res);
        }
}

