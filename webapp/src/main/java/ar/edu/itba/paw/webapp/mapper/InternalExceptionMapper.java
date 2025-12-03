package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.webapp.dto.error.ExceptionDto;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InternalExceptionMapper implements ExceptionMapper<RuntimeException> {
    private final ExceptionToStatusMapper exceptionToStatusMapper = new ExceptionToStatusMapper();

    @Override
    public Response toResponse(RuntimeException exception) {
        return Response.status(exceptionToStatusMapper.map(exception))
                .entity(ExceptionDto.fromException(exception)).build();
    }
}
