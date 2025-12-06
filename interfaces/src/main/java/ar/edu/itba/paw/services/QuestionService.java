package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.PagedList;
import ar.edu.itba.paw.model.Question;
import ar.edu.itba.paw.model.User;

import java.util.Map;
import java.util.Optional;

public interface QuestionService {
    PagedList<Question> getAllQuestions(int page);
    PagedList<Question> getQuestionsByService(long serviceId, int page);

    Optional<Question> findById(long id);
    Question create(long serviceid, long userid, String question);
    void addResponse(long id, String response);

    int getAllQuestionsCount();
    int getQuestionsCountByService(long serviceId);

    PagedList<Question> getQuestionsToRespond(long userId, int page);
    int getQuestionsToRespondCount(long userId);
    int getQuestionsToRespondPageCount(long userId);
}
