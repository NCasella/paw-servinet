package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.webapp.dto.error.ExceptionDto;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InternalExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException exception) {
        return Response.status(ExceptionToStatusMapper.map(exception)).type(MediaType.APPLICATION_JSON)
                .entity(ExceptionDto.fromException(exception)).build();
    }
}
