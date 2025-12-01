package ar.edu.itba.paw.model.exceptions;

public class UserNotFoundException extends NotFoundException {

    private static final String message = "The user was not found";
    private static final String USER_NON_EXISTENT = "usuarionoexiste";

    public UserNotFoundException() {
        super(USER_NON_EXISTENT);
    }


    @Override
    public String getMessage() {
        return message;
    }
}
