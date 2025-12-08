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
import java.util.stream.Collectors;
@Repository
public class QuestionDaoJpa implements QuestionDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Question> getAllQuestions(int page, int pageSize){
        Query nativeQuery = em.createNativeQuery("SELECT q.questionid FROM questions q");
        nativeQuery.setFirstResult((page - 1) * pageSize);
        nativeQuery.setMaxResults(pageSize);

        @SuppressWarnings("unchecked")
        final List<Long> idList = (List<Long>) nativeQuery.getResultList()
                .stream().map(n -> ((Number)n).longValue()).collect(Collectors.toList());

        final TypedQuery<Question> query = em.createQuery(" from Question as q where q.questionid in :idList ", Question.class);
        query.setParameter("idList", idList);
        return query.getResultList();
    }

    @Override
    public List<Question> getServiceQuestions(long serviceId, int page, int pageSize){
        StringBuilder sql = new StringBuilder("""
        SELECT q.questionid
        FROM questions q
        WHERE q.serviceid = :serviceid
        """);

        Query nativeQuery = em.createNativeQuery(sql.toString()).setParameter("serviceid", serviceId);
        nativeQuery.setFirstResult((page - 1) * pageSize);
        nativeQuery.setMaxResults(pageSize);

        @SuppressWarnings("unchecked")
        final List<Long> idList = (List<Long>) nativeQuery.getResultList()
                .stream().map(n -> ((Number)n).longValue()).collect(Collectors.toList());

        final TypedQuery<Question> query = em.createQuery(" from Question as q where q.questionid in :idList ", Question.class);
        query.setParameter("idList", idList);
        return query.getResultList();
    }

    @Override
    public Optional<Question> findById(long id) {
        return Optional.of(em.find(Question.class, id));
    }

    @Override
    public Question create(long serviceId, long userId, String question) {
        Question newQuestion = new Question(em.find(Service.class, serviceId), em.find(User.class, userId), question, null, LocalDate.now() );
        em.persist(newQuestion);
        return newQuestion;
    }

    @Override
    public void addResponse(long id, String response) {
        Question question = em.find(Question.class, id);
        question.setResponse(response);
        em.persist(question);
    }

    @Override
    public int getQuestionsCount() {
        return em.createQuery("SELECT COUNT(q) FROM Question q", Long.class)
                .getSingleResult()
                .intValue();
    }

    @Override
    public int getQuestionsCountByService(long serviceId) {
        return em.createQuery("SELECT COUNT(q) FROM Question q WHERE q.service.id = :serviceid", Long.class)
                .setParameter("serviceid", serviceId)
                .getSingleResult()
                .intValue();
    }

    @Override
    public List<Question> getQuestionsToRespond(long userId, int page, int pageSize) {

        StringBuilder sql = new StringBuilder("""
        SELECT q.questionid 
        FROM questions q 
        WHERE q.response is null 
        AND q.serviceid IN (SELECT s.id FROM services s WHERE s.businessid IN (SELECT b.businessid FROM business b WHERE b.userid = :userid)) 
        ORDER BY q.date DESC
        """);

        Query nativeQuery = em.createNativeQuery(sql.toString()).setParameter("userid", userId);
        nativeQuery.setFirstResult((page - 1) * pageSize);
        nativeQuery.setMaxResults(pageSize);

        @SuppressWarnings("unchecked")
        final List<Long> idList = (List<Long>) nativeQuery.getResultList()
                .stream().map(n -> ((Number)n).longValue()).collect(Collectors.toList());

        final TypedQuery<Question> questions = em.createQuery(" from Question as q where q.questionid in :idList ", Question.class);
        questions.setParameter("idList", idList);

        return questions.getResultList();
    }

    @Override
    public int getQuestionsToRespondCount(long userId) {
        final TypedQuery<Long> query = em.createQuery("select COUNT(q) from Question as q where q.response is null and q.service.business.ownedBy.id = :userid", Long.class);
        query.setParameter("userid", userId);
        return query.getSingleResult().intValue();
    }

    private String getServiceNameForQuestion(long serviceId) {
        return em.find(Service.class, serviceId).getName();
    }
}
