package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Question;
import ar.edu.itba.paw.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface QuestionDao {
    List<Question> getAllQuestions(int page, int pageSize);
    List<Question> getServiceQuestions(long serviceId, int page, int pageSize);

    int getQuestionsCount();
    int getQuestionsCountByService(long serviceId);

    Optional<Question> findById(long id);
    Question create(long serviceId, long userid, String question);
    void addResponse(long id, String response);
    List<Question> getQuestionsToRespond(long userId, int page, int pageSize);
    int getQuestionsToRespondCount(long userId);
}
