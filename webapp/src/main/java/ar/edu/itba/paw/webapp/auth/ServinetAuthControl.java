package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class ServinetAuthControl {

    private final UserService userService;
    private final AppointmentService appointmentService;
    private final BusinessService businessService;
    private final ServiceService ss;
    private final RatingService ratingService;
    private final QuestionService questionService;
    @Autowired
    public ServinetAuthControl(@Qualifier("userServiceImpl") final UserService userService,
                               @Qualifier("appointmentServiceImpl") final AppointmentService appointmentService,
                               @Qualifier("BusinessServiceImpl") final BusinessService businessService,
                               @Qualifier("serviceServiceImpl") final ServiceService ss,@Qualifier("RatingServiceImpl") RatingService ratingService,@Qualifier("QuestionServiceImpl")QuestionService questionService){
        this.userService = userService;
        this.appointmentService = appointmentService;
        this.businessService = businessService;
        this.ss=ss;
        this.ratingService=ratingService;
        this.questionService=questionService;
    }

    @Transactional
    public Optional<String> getCurrentUserEmail() {
        final SecurityContext context = SecurityContextHolder.getContext();
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated()){
            return Optional.of(context.getAuthentication().getName());
        }
        return Optional.empty();
    }
    @Transactional(readOnly = true)
    public boolean isServiceOwner(long serviceId){
        Optional<User> user=getCurrentUser();
        if(user.isEmpty()){
            return false;
        }
        Optional<Service> service =ss.findById(serviceId);
        if(service.isEmpty()){
            return false;
        }
        Business business = service.get().getBusiness();
        return business.getUserId() == user.get().getUserId();
    }

    @Transactional(readOnly = true)
    public boolean isCurrentUser(long userId){
        Optional<User> userOptional= getCurrentUser();
        return userOptional.isPresent() && userOptional.get().getUserId()==userId;
    }

    @Transactional(readOnly = true)
    public boolean isUserAppointment(long appointmentId){
        Optional<User> user =getCurrentUser();
        if(user.isEmpty()){
            return false;
        }
        Optional<Appointment> appointment = appointmentService.findById(appointmentId);
        return appointment.filter(value -> user.get().getUserId() == value.getUserid()).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean isAdminAppointment(long appointmentId){
        Optional<Appointment> appointment = appointmentService.findById(appointmentId);
        Optional<User> optUser=getCurrentUser();
        long currentUserId= optUser.map(User::getUserId).orElse(-1L);
        return appointment.filter(value -> currentUserId == value.getServiceAppointed().getBusiness().getUserId()).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean isBusinessOwner(long businessId, long userId){
        Business business = businessService.findById(businessId).orElse(null);
        if(business == null){
            return false;
        }
        return business.getUserId() == userId;
    }
    @Transactional(readOnly = true)
    public boolean isBusinessOwner(long businessId){
        Optional<User> user =getCurrentUser();
        return user.filter(value -> this.isBusinessOwner(businessId, value.getUserId())).isPresent();
    }
    @Transactional(readOnly = true)
    public boolean isRatingOwner(long ratingId){
        Optional<User> currentUser=getCurrentUser();
        if(currentUser.isEmpty()){
            return false;
        }
        Optional<Rating> rating = ratingService.findById(ratingId);
        return rating.filter(value -> value.getUserid() == currentUser.get().getUserId()).isPresent();
    }

    @Transactional(readOnly = true)
    public Optional<User> getCurrentUser() {
        final Optional<String> mayBeEmail = getCurrentUserEmail();
        if (mayBeEmail.isEmpty()){
            return Optional.empty();
        }
        return userService.findByEmail(mayBeEmail.get());
    }


    @Transactional(readOnly = true)
    public boolean isLoggedIn(){
        return getCurrentUser().isPresent();
    }

    @Transactional(readOnly = true)
    public boolean isProvider(){
        Optional<User> user = getCurrentUser();
        return user.map(User::isProvider).orElse(false);
    }
    @Transactional
    public boolean isUserProvidee(long providerUserId,long requestUserId){
        return userService.isUserProvidee(providerUserId, requestUserId);
    }
    @Transactional(readOnly = true)
    public boolean isQuestionResponseServiceOwner(long questionId){
        Optional<Question> mayQuestion=questionService.findById(questionId);
        return mayQuestion.filter(question -> this.isServiceOwner(question.getServiceid())).isPresent();
    }

}
