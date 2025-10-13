package ar.edu.itba.paw.model.exceptions;

public class QuestionNotFoundException extends NotFoundException {

    private static final String message = "The business was not found";
    private static final String QUESTION_NON_EXISTENT = "preguntanoexiste";

    public QuestionNotFoundException() {
        super(QUESTION_NON_EXISTENT);
    }


    @Override
    public String getMessage() {
        return message;
    }
}
