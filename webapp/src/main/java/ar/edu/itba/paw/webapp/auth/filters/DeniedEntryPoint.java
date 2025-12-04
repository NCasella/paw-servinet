package ar.edu.itba.paw.webapp.auth.filters;

import ar.edu.itba.paw.webapp.dto.error.ExceptionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

public class DeniedEntryPoint implements AccessDeniedHandler {

    private final ObjectMapper mapper=new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON);
        ExceptionDto dto=ExceptionDto.fromException(accessDeniedException);
        mapper.writeValue(response.getWriter(),dto);
    }
}
