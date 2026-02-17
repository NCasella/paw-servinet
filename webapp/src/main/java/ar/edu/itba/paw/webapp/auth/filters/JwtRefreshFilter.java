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
        String authToken=request.getHeader(HttpHeaders.AUTHORIZATION);

        if(refreshToken==null ||  (authToken != null && (authToken.startsWith("Bearer ") || authToken.startsWith("Basic ")) || refreshToken.length()<"Bearer ".length() )){
            filterChain.doFilter(request,response);
            return;
        }
        refreshToken=refreshToken.substring("Bearer ".length());

        try{
            UserDetails us=jwtUtil.loginUser(refreshToken);
            if(us!=null){
            String newToken=jwtUtil.generateToken(us);
            String newRefreshToken= jwtUtil.generateRefreshToken(us);
            response.setHeader("Authorization-Access-Token","Bearer "+newToken);
            response.setHeader("Authorization-Refresh-Token","Bearer "+newRefreshToken);
            }
        }catch (JwtException | IllegalArgumentException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        filterChain.doFilter(request,response);

    }
}
