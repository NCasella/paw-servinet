package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Question;
import ar.edu.itba.paw.model.Service;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.services.QuestionDao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;
@Repository
public class QuestionDaoJpa implements QuestionDao {
    @PersistenceContext
    private EntityManager em;
    @Override
    public List<Question> getAllQuestions(long serviceid, int page, int pageSize){
        Query nativeQuery = em.createNativeQuery("SELECT questionid FROM questions WHERE serviceid = :serviceid", Question.class).setParameter("serviceid", serviceid);
        nativeQuery.setFirstResult((page - 1) * pageSize);
        nativeQuery.setMaxResults(pageSize);

        @SuppressWarnings("unchecked")
        List<Long> idList = ((Stream<Integer>) nativeQuery.getResultStream()).map(Integer::longValue).toList();

        TypedQuery<Question> query = em.createQuery("SELECT q FROM Question q WHERE q.id IN :idList", Question.class);
        query.setParameter("idList", idList);
        return query.getResultList();
    }

    @Override
    public Optional<Question> findById(long id) {
        return Optional.of(em.find(Question.class, id));
    }

    @Override
    public Question create(long serviceid, long userid, String question) {
        Question newQuestion = new Question(em.find(Service.class, serviceid), em.find(User.class, userid), question, null, LocalDate.now() );
        em.persist(question);
        return newQuestion;
    }

    @Override
    public void addResponse(long id, String response) {
        Question question = em.find(Question.class, id);
        question.setResponse(response);
        em.persist(question);
    }

    @Override
    public int getQuestionsCount(long serviceid) {
        return em.createQuery("SELECT COUNT(q) FROM Question q WHERE q.service.id = :serviceid", Long.class)
                .setParameter("serviceid", serviceid).getSingleResult().intValue();
    }

    @Override
    public Optional<Map<Question, String>> getQuestionsToRespond(long userid) {
        //agrego business en BasicService en vez del id
        List<Question> questions = em.createQuery("SELECT q FROM Question q JOIN q.service s JOIN s.business b WHERE b.user.id = :userid AND q.response IS NULL ORDER BY q.date DESC", Question.class)
                .setParameter("userid", userid).getResultList();

        Map<Question, String> questionServiceMap = new HashMap<>();

        for (Question question : questions) {
            String serviceName = getServiceNameForQuestion(question.getServiceid());
            questionServiceMap.put(question, serviceName);
        }
        return Optional.of(questionServiceMap);
    }

    private String getServiceNameForQuestion(long serviceId) {
        return em.find(Service.class, serviceId).getName();
    }
}
