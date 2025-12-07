package ar.edu.itba.paw.webapp.auth.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class BasicAuthFilter extends OncePerRequestFilter {
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private AuthenticationEntryPoint authEntryPoint;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Basic ")) {
            // decode credentials
            String base64Cred = header.substring(6);
            String creds = new String(Base64.getDecoder().decode(base64Cred), StandardCharsets.UTF_8);
            String[] parts = creds.split(":", 2);

            try {
                UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(parts[0], parts[1]);
                Authentication auth = authManager.authenticate(authReq);

                if (auth.isAuthenticated()) {
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    UserDetails us=(UserDetails) auth.getPrincipal();
                    String token = jwtUtil.generateToken(us);
                    String refresToken=jwtUtil.generateRefreshToken(us);
                    res.setHeader("Authorization-Access-Token", "Bearer " + token);
                    res.setHeader("Authorization-Refresh-Token",refresToken);
                }
            }
            catch (AuthenticationException e){
                SecurityContextHolder.clearContext();
                authEntryPoint.commence(req,res,e);
                return;
            }
        }

        chain.doFilter(req, res);
    }
}

