package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.model.exceptions.ForbiddenOperationException;
import ar.edu.itba.paw.model.exceptions.InvalidFilterException;
import ar.edu.itba.paw.model.exceptions.InvalidOperationException;
import ar.edu.itba.paw.model.exceptions.NotFoundException;
import org.springframework.security.access.AccessDeniedException;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Map;

public class ExceptionToStatusMapper {
    private static final Map<Class<? extends Exception>, Response.Status> exceptionStatusMap = Map.of(
            InvalidOperationException.class , Response.Status.BAD_REQUEST,
            InvalidFilterException.class, Response.Status.BAD_REQUEST,
            NotFoundException.class, Response.Status.NOT_FOUND,
            ForbiddenOperationException.class, Response.Status.FORBIDDEN,
            NotAllowedException.class, Response.Status.METHOD_NOT_ALLOWED,
            AccessDeniedException.class, Response.Status.FORBIDDEN

            //AuthenticationException.class, Response.Status.UNAUTHORIZED

    );

    public static Response.Status map(final Exception e) {
        if ( e instanceof WebApplicationException w) {
            return Response.Status.fromStatusCode( w.getResponse().getStatus() );
        }
        Class<?> clazz = e.getClass();
        while (clazz != null) {
            Response.Status status = exceptionStatusMap.get(clazz);
            if (status != null)
                return status;
            clazz = clazz.getSuperclass();
        }
         return Response.Status.INTERNAL_SERVER_ERROR;
    }
}

