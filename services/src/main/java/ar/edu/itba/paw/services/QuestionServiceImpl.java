package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.BusinessNotFoundException;
import ar.edu.itba.paw.model.exceptions.QuestionNotFoundException;
import ar.edu.itba.paw.model.exceptions.ServiceNotFoundException;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service("QuestionServiceImpl")
public class QuestionServiceImpl implements  QuestionService {

    private final QuestionDao questionDao;
    private final EmailService emailService;
    private final UserService userService;
    private final ServiceService serviceService;
    private final BusinessService businessService;
    private final Logger LOGGER = LoggerFactory.getLogger(QuestionServiceImpl.class);
    private static final int PAGE_SIZE=10;
    @Autowired
    public QuestionServiceImpl(final QuestionDao questionDao, final EmailService emailService,
                               final UserService userService, final ServiceService serviceService,
                               final BusinessService businessService) {
        this.questionDao = questionDao;
        this.emailService = emailService;
        this.userService = userService;
        this.serviceService = serviceService;
        this.businessService = businessService;
    }

    @Transactional(readOnly = true)
    @Override
    public PagedList<Question> getAllQuestions(int page) {
        List<Question> questions = questionDao.getAllQuestions(page, PAGE_SIZE);
        int questionsCount = getAllQuestionsCount();
        return PagedList.of(questions, questionsCount);
    }

    @Transactional(readOnly = true)
    @Override
    public PagedList<Question> getQuestionsByService(long serviceId, int page) {
        serviceService.findById(serviceId).orElseThrow(ServiceNotFoundException::new);
        List<Question> questions = questionDao.getServiceQuestions(serviceId, page, PAGE_SIZE);
        int questionsCount = getQuestionsCountByService(serviceId);
        return PagedList.of(questions, questionsCount);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Question> findById(long id) {
        return questionDao.findById(id);
    }

    @Transactional
    @Override
    public Question create(long serviceId, long userid, String questionString) {
        BasicService service = serviceService.findBasicServiceById(serviceId).orElseThrow(ServiceNotFoundException::new);
        Business business = businessService.findById(service.getBusinessid()).orElseThrow(BusinessNotFoundException::new);
        User user = userService.findById(userid).orElseThrow(UserNotFoundException::new);
        Question question = questionDao.create(serviceId, userid, questionString);
        emailService.askedQuestion( service, business.getEmail(), user, questionString, userService.getUserLocale(business.getUserId()));
        return question;
    }

    @Transactional
    @Override
    public void addResponse(long id, String response) {
        Question question = questionDao.findById(id).orElseThrow(QuestionNotFoundException::new);
        BasicService service = serviceService.findBasicServiceById(question.getServiceid()).orElseThrow(ServiceNotFoundException::new);
        User user = userService.findById(question.getUserid()).orElseThrow(UserNotFoundException::new);
        questionDao.addResponse(id, response);
        emailService.answeredQuestion(service, user, question.getQuestion(), response );
    }

    @Transactional(readOnly = true)
    @Override
    public int getAllQuestionsCount() {
        return questionDao.getQuestionsCount();
    }

    @Transactional(readOnly = true)
    @Override
    public int getQuestionsCountByService(long serviceId) {
        serviceService.findById(serviceId).orElseThrow(ServiceNotFoundException::new);
        return questionDao.getQuestionsCountByService(serviceId);
    }

    @Transactional(readOnly = true)
    @Override
    public PagedList<Question> getQuestionsToRespond(long userId, int page) {
        userService.findById(userId).orElseThrow(UserNotFoundException::new);
        List<Question> questions = questionDao.getQuestionsToRespond(userId, page, PAGE_SIZE);
        int questionsCount = getQuestionsToRespondCount(userId);
        return PagedList.of(questions, questionsCount);
    }

    @Transactional(readOnly = true)
    @Override
    public int getQuestionsToRespondCount(long userId) {
        userService.findById(userId).orElseThrow(UserNotFoundException::new);
        return questionDao.getQuestionsToRespondCount(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public int getQuestionsToRespondPageCount(long userId) {
        userService.findById(userId).orElseThrow(UserNotFoundException::new);
        int count = getQuestionsToRespondCount(userId);
        int pageCount = count / PAGE_SIZE;
        if(count % PAGE_SIZE != 0) pageCount++;
        return pageCount;
    }
}
