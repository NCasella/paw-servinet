package ar.edu.itba.paw.webapp.config;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;

public class UnconditionalCacheFilter extends OncePerRequestFilter {

    private static final int MAX_TIME=31536000;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setHeader(HttpHeaders.CACHE_CONTROL,String.format("public, max-age=%d, immutable", MAX_TIME));
        filterChain.doFilter(request,response);
    }
}
