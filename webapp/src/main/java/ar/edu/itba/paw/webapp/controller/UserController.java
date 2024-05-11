package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.auth.ServinetAuthControl;
import ar.edu.itba.paw.webapp.form.ResponseForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
public class UserController {
    private final BusinessService businessService;
    private final UserService userService;
    private final QuestionService questionService;
    private final ServinetAuthControl authControl;
    private final AppointmentService appointmentService;

    @Autowired
    public UserController (@Qualifier("BusinessServiceImpl") final BusinessService businessService,
                           @Qualifier("userServiceImpl") final UserService userService,
                           @Qualifier("QuestionServiceImpl") final QuestionService questionService,
                           @Qualifier("servinetAuthControl") final ServinetAuthControl authControl,
                           @Qualifier("appointmentServiceImpl") final AppointmentService appointmentService){

        this.businessService = businessService;
        this.userService = userService;
        this.questionService = questionService;
        this.authControl= authControl;
        this.appointmentService = appointmentService;
    }

//    @RequestMapping(method = RequestMethod.GET, path = "/{userId:\\d+}/perfil")
    @RequestMapping(method = RequestMethod.GET, path = "/perfil")
    public ModelAndView profile() {
        final ModelAndView mav = new ModelAndView("profile");
        User user = authControl.getCurrentUser().orElseThrow(UserNotFoundException::new);

        List<Business> businessList = Collections.emptyList();
        if ( user.isProvider() )
            businessList = businessService.findByAdminId(user.getUserId());
        mav.addObject("businessList", businessList);
        mav.addObject("user", user);
        return mav;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/negocios")
    public ModelAndView business() {
        final ModelAndView mav = new ModelAndView("userBusiness");

        User currentUser = authControl.getCurrentUser().orElseThrow(UserNotFoundException::new);
        List<Business> businessList= businessService.findByAdminId(currentUser.getUserId());

        mav.addObject("user",currentUser);
        mav.addObject("businessList", businessList);
        return mav;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/negocios/consultas")
    public ModelAndView userServicesQuestions(@ModelAttribute("responseForm") final ResponseForm responseForm) {
        final ModelAndView mav = new ModelAndView("userQuestions");
        long userid = authControl.getCurrentUser().orElseThrow(UserNotFoundException::new).getUserId();

        mav.addObject("pendingQst", questionService.getQuestionsToRespond(userid));
        return mav;
    }


    @RequestMapping(method = RequestMethod.GET, path = "/turnos")
    public ModelAndView userAppointments( @RequestParam(name = "confirmados") final boolean confirmed) {

        final ModelAndView mav = new ModelAndView("userAppointments");

        long userid = authControl.getCurrentUser().get().getUserId();

        List<AppointmentInfo> appointmentList = appointmentService.getAllUpcomingUserAppointments(userid,confirmed);

        mav.addObject("appointmentList", appointmentList);
        mav.addObject("confirmed",confirmed);
        mav.addObject("userId", userid);
        return mav;
    }


}
