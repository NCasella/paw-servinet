package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.webapp.dto.ExceptionDto;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InternalExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ExceptionDto.fromException(exception)).build();
    }
}
