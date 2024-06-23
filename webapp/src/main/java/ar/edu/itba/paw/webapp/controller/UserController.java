package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.BusinessNotFoundException;
import ar.edu.itba.paw.model.exceptions.InvalidFilterException;
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
    private final UserService userService;
    private final QuestionService questionService;
    private final ServinetAuthControl authControl;
    private final AppointmentService appointmentService;
    private final ServiceService serviceService;

    @Autowired
    public UserController (@Qualifier("userServiceImpl") final UserService userService,
                           @Qualifier("QuestionServiceImpl") final QuestionService questionService,
                           @Qualifier("servinetAuthControl") final ServinetAuthControl authControl,
                           @Qualifier("serviceServiceImpl") final ServiceService serviceService,
                           @Qualifier("appointmentServiceImpl") final AppointmentService appointmentService){
        this.userService = userService;
        this.questionService = questionService;
        this.authControl= authControl;
        this.appointmentService = appointmentService;
        this.serviceService=serviceService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/perfil")
    public ModelAndView profile() {
        final ModelAndView mav = new ModelAndView("profile");
        User user = authControl.getCurrentUser().orElseThrow(UserNotFoundException::new);

        List<Business> businessList = Collections.emptyList();
        if ( user.isProvider() )
            businessList = user.getBusinessOwned();
        mav.addObject("businessList", businessList);
        mav.addObject("user", user);
        return mav;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/perfil/cambiar-idioma")
    public ModelAndView changeLanguage(){
        long userid = authControl.getCurrentUser().orElseThrow(UserNotFoundException::new).getUserId();
        userService.changeLocale(userid);
        return new ModelAndView("redirect:/perfil");
    }


    @RequestMapping(method = RequestMethod.GET, path = "/negocios")
    public ModelAndView business() {
        final ModelAndView mav = new ModelAndView("userBusiness");

        User currentUser = authControl.getCurrentUser().orElseThrow(UserNotFoundException::new);
        List<Business> businessList= currentUser.getBusinessOwned();

        mav.addObject("user",currentUser);
        mav.addObject("businessList", businessList);
        return mav;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/negocios/consultas")
    public ModelAndView userServicesQuestions(
            @ModelAttribute("responseForm") final ResponseForm responseForm,
            @RequestParam(name = "pagina", required = false, defaultValue = "1") Integer page
    ) {
        final ModelAndView mav = new ModelAndView("userQuestions");
        User currentUser = authControl.getCurrentUser().orElseThrow(UserNotFoundException::new);
        mav.addObject("pendingQst", questionService.getQuestionsToRespond(currentUser, page));
        mav.addObject("page", page);
        mav.addObject("pageCount", questionService.getQuestionsToRespondPageCount(currentUser));
        return mav;
    }


    @RequestMapping(method = RequestMethod.GET, path = "/negocios/turnos")
    public ModelAndView userServicesAppointments(
            @RequestParam(name = "confirmados") final boolean confirmed,
            @RequestParam(name = "pagina", required = false, defaultValue = "1") Integer page
    ) {
        Business business = businessService.findById(businessId).orElseThrow(BusinessNotFoundException::new);
        if ( page<1 )
            throw new InvalidFilterException();
        List<BasicService> services = serviceService.getAllBusinessBasicServices(businessId);
        List<Appointment> appointmentList;

        Map<Long, BasicService> serviceMap = new HashMap<>();
        services.forEach(service -> serviceMap.put(service.getId(), service));
        Set<Long> serviceIds =  serviceMap.keySet();
        appointmentList = appointmentService.getAllUpcomingServicesAppointments(serviceIds, confirmed, page);

        final ModelAndView mav = new ModelAndView("businessAppointments");
        Map<Long, User> userMap = new HashMap<>();
        if (confirmed){
            for (Appointment a : appointmentList){
                if (!userMap.containsKey(a.getUserid()))
                    userMap.put(a.getUserid(),userService.findById(a.getUserid()).orElseThrow(UserNotFoundException::new));
            }
            mav.addObject("userMap", userMap );
        }

        mav.addObject("business",business);
        mav.addObject("serviceMap", serviceMap );
        mav.addObject("appointmentList", appointmentList);
        mav.addObject("confirmed",confirmed);
        mav.addObject("page",page);
        final long totalResults = appointmentService.getServicesAppointmentCount(serviceIds,confirmed);
        mav.addObject("totalResults",totalResults);
        long pageCount = appointmentService.getPageCount(totalResults);
        if ( page!=1 && pageCount < page) {
            return new ModelAndView("redirect:/negocio/" + businessId + "/turnos?confirmados=" + confirmed + "&pagina=" + pageCount);
        }
        mav.addObject("moreResults", appointmentService.getServicesAppointmentCount(serviceIds,!confirmed));
        mav.addObject("pageCount", pageCount);
        return mav;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/turnos")
    public ModelAndView userAppointments( @RequestParam(name = "confirmados") final boolean confirmed,
                                          @RequestParam(name = "pagina", required = false, defaultValue = "1") Integer page) {

        final ModelAndView mav = new ModelAndView("userAppointments");

        long userid = authControl.getCurrentUser().orElseThrow(UserNotFoundException::new).getUserId();
        if ( page<1 )
            throw new InvalidFilterException();

        List<Appointment> appointmentList = appointmentService.getAllUpcomingUserAppointments(userid,confirmed,page);
        Set<Long> serviceids = new HashSet<>();
        for ( Appointment a : appointmentList){
            serviceids.add(a.getServiceid());
        }
        Map<Long,ServiceContactInfo> serviceContactInfoMap = serviceService.getServicesContactInfo(serviceids);
        mav.addObject("appointmentList", appointmentList);
        mav.addObject("serviceContactInfoMap", serviceContactInfoMap );
        mav.addObject("confirmed",confirmed);
        mav.addObject("page",page);
        mav.addObject("moreResults",appointmentService.getUserAppointmentCount(userid,!confirmed));
        final long totalResults = appointmentService.getUserAppointmentCount(userid,confirmed);
        long pageCount = appointmentService.getPageCount(totalResults);
        if ( page!=1 && pageCount < page) {
            return new ModelAndView("redirect:/turnos?confirmados=" + confirmed + "&pagina=" + pageCount);
        }
        mav.addObject("totalResults",totalResults);
        mav.addObject("pageCount", pageCount);
        return mav;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/turnos/historial")
    public ModelAndView userPreviousAppointments(
            @RequestParam(name = "pagina", required = false, defaultValue = "1") Integer page
    ) {

        final ModelAndView mav = new ModelAndView("userAppointments");

        long userid = authControl.getCurrentUser().orElseThrow(UserNotFoundException::new).getUserId();
        if ( page<1 )
            throw new InvalidFilterException();
        List<Appointment> appointmentList = appointmentService.getPreviousUserAppointments(userid,page);
        Set<Long> serviceids = new HashSet<>();
        for ( Appointment a : appointmentList){
            serviceids.add(a.getServiceid());
        }
        Map<Long,ServiceContactInfo> serviceContactInfoMap = serviceService.getServicesContactInfo(serviceids);
        mav.addObject("appointmentList", appointmentList);
        mav.addObject("serviceContactInfoMap", serviceContactInfoMap );
        mav.addObject("history",true);
        mav.addObject("page",page);
        final long totalResults = appointmentService.getPreviousUserAppointmentCount(userid);
        mav.addObject("totalResults",totalResults);
        mav.addObject("pageCount", appointmentService.getPageCount(totalResults));
        return mav;
    }

}
