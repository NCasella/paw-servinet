package ar.edu.itba.paw.webapp.auth.filters;

import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRefreshFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String refreshToken=request.getHeader("Authorization-Refresh-Token");
        String authHeader=request.getHeader(HttpHeaders.AUTHORIZATION);

        if(refreshToken==null || (authHeader != null && (authHeader.startsWith("Bearer ") || authHeader.startsWith("Basic ")) ) ){
            filterChain.doFilter(request,response);
            return;
        }
        if(refreshToken.length() <"Bearer ".length()){
            filterChain.doFilter(request,response);
            return;
        }
        refreshToken=refreshToken.substring("Bearer ".length());
        try{
            UserDetails us=jwtUtil.loginUser(refreshToken);
            if(us!=null){
            String newToken=jwtUtil.generateToken(us);
            response.setHeader(HttpHeaders.AUTHORIZATION,"Bearer "+newToken);
            }
        }catch (JwtException | IllegalArgumentException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        filterChain.doFilter(request,response);

    }
}
