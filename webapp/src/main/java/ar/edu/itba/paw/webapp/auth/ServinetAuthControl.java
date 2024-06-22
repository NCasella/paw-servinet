package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.AppointmentNonExistentException;
import ar.edu.itba.paw.model.exceptions.BusinessNotFoundException;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
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
    @Autowired
    public ServinetAuthControl(@Qualifier("userServiceImpl") final UserService userService,
                               @Qualifier("appointmentServiceImpl") final AppointmentService appointmentService,
                               @Qualifier("BusinessServiceImpl") final BusinessService businessService,
                               @Qualifier("serviceServiceImpl") final ServiceService ss,@Qualifier("RatingServiceImpl") RatingService ratingService){
        this.userService = userService;
        this.appointmentService = appointmentService;
        this.businessService = businessService;
        this.ss=ss;
        this.ratingService=ratingService;
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
        User user=getCurrentUser().orElseThrow(UserNotFoundException::new);
        Optional<Service> service =ss.findById(serviceId);
        if(service.isEmpty()){
            return false;
        }
        Business business = service.orElseThrow(BusinessNotFoundException::new).getBusiness();
        return business.getUserId() == user.getUserId();
    }


    @Transactional(readOnly = true)
    public boolean isUserAppointment(long appointmentId){
        User user =getCurrentUser().orElseThrow(UserNotFoundException::new);
        Appointment appointment = appointmentService.findById(appointmentId).orElseThrow(AppointmentNonExistentException::new);
        return user.getUserId() == appointment.getUserid();
    }

    @Transactional(readOnly = true)
    public boolean isAdminAppointment(long appointmentId){
        User user =getCurrentUser().orElseThrow(UserNotFoundException::new);
        BasicAppointment appointment = appointmentService.findById(appointmentId).orElseThrow(AppointmentNonExistentException::new);
        return user.getUserId() == appointment.getServiceAppointed().getBusiness().getUserId();
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
    public boolean isRatingOwner(long ratingId){
        User user=getCurrentUser().orElseThrow(UserNotFoundException::new);
        Optional<Rating> rating = ratingService.findById(ratingId);
        return rating.filter(value -> value.getUserid() == user.getUserId()).isPresent();
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
}
