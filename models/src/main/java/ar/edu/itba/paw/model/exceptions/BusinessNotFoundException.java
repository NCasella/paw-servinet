package ar.edu.itba.paw.model.exceptions;

public class BusinessNotFoundException extends NotFoundException {

    private static final String message = "The business was not found";
    private static final String BUSINESS_NON_EXISTENT = "negocionoexiste";

    public BusinessNotFoundException() {
        super(BUSINESS_NON_EXISTENT);
    }


    @Override
    public String getMessage() {
        return message;
    }
}
