package ar.edu.itba.paw.webapp.mapper;

import ar.edu.itba.paw.webapp.dto.error.ValidationErrorDto;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;

@Provider
public class ConstraintValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<ValidationErrorDto> errors = exception.getConstraintViolations()
                .stream()
                .map(constraintViolation -> {
                    String fullPath = constraintViolation.getPropertyPath().toString();
                    String cleanPath = extractCleanFieldPath(fullPath);

                    return ValidationErrorDto.fromError(
                            constraintViolation.getMessage(),
                            cleanPath
                    );
                })
                .toList();

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new GenericEntity<>(errors) {})
                .build();
    }

    private String extractCleanFieldPath(String fullPath) {

        if (fullPath.endsWith(".arg0")) {
            return "body";
        }

        int lastDotIndex = fullPath.lastIndexOf('.');

        if (lastDotIndex != -1) {
            return fullPath.substring(lastDotIndex + 1);
        }

        return fullPath;
    }
}