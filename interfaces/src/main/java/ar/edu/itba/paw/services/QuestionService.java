package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Question;
import ar.edu.itba.paw.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface QuestionService {
    List<Question> getAllQuestions(long serviceid, int page);
    Optional<Question> findById(long id);
    Question create(long serviceid, long userid, String question);
    void addResponse(long id, String response);
    int getQuestionsCount(long serviceid);
    Map<Question, String> getQuestionsToRespond(User user, int page);
    int getQuestionsToRespondCount(User user);
    int getQuestionsToRespondPageCount(User user);
}
