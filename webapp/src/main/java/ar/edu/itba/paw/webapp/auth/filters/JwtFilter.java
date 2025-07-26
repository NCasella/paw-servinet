package ar.edu.itba.paw.webapp.auth.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private final JwtUtil jwtUtil;
    @Autowired
    private UserDetailsService userDetailsService;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader==null || !authorizationHeader.startsWith("Bearer ")){
            chain.doFilter(request,response);
            return;
        }
        String token = authorizationHeader.substring("Bearer ".length());


        UserDetails userDetails=extractUserDetails(token);
        if(userDetails!=null && jwtUtil.validateToken(token,userDetails) && userDetails.isEnabled()){
            UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(userDetails.getUsername(),userDetails.getPassword(),userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        chain.doFilter(request,response);

    }

    private UserDetails extractUserDetails(String token){
        String username= jwtUtil.extractUsername(token);
        if(username==null){
            return null;
        }
        try{
            return userDetailsService.loadUserByUsername(username);
        }catch (UsernameNotFoundException e){
            return null;
        }
    }
}
