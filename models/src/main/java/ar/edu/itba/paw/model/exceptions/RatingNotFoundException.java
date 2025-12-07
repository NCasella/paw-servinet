package ar.edu.itba.paw.model.exceptions;

public class RatingNotFoundException extends NotFoundException {

    private static final String message = "The review was not found";
    private static final String RATING_NON_EXISTENT = "reseñanoexiste";

    public RatingNotFoundException() {
        super(RATING_NON_EXISTENT);
    }


    @Override
    public String getMessage() {
        return message;
    }
}
