package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.ForbiddenOperation;
import ar.edu.itba.paw.model.exceptions.ServiceNotFoundException;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.auth.ServinetAuthControl;
import ar.edu.itba.paw.webapp.form.AppointmentForm;
import ar.edu.itba.paw.webapp.form.QuestionForm;
import ar.edu.itba.paw.webapp.form.ReviewsForm;
import ar.edu.itba.paw.webapp.form.ServiceForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller("ServiceController")
public class ServiceController {
    private final ServiceService ss;
    private final UserService us;
    private final AppointmentService as;
    private BusinessService bs;
    private final ServinetAuthControl authControl;
    private RatingService rating;
    private QuestionService question;
    private static final String TBDPricing = PricingTypes.TBD.getValue();



    @Autowired
    public ServiceController(
            @Qualifier("userServiceImpl") final UserService userService,
            @Qualifier("serviceServiceImpl") final ServiceService serviceService,
            @Qualifier("appointmentServiceImpl") final AppointmentService appointmentService,
            @Qualifier("BusinessServiceImpl") final BusinessService bs,
            @Qualifier("QuestionServiceImpl") final QuestionService question,
            @Qualifier("RatingServiceImpl") final RatingService rating,
            @Qualifier("servinetAuthControl") final ServinetAuthControl authControl
    ) {
        this.ss = serviceService;
        this.us = userService;
        this.as = appointmentService;
        this.bs = bs;
        this.rating = rating;
        this.question = question;
        this.authControl = authControl;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/servicios")
    public ModelAndView services(
            @RequestParam(name = "categoria", required = false) String category,
            @RequestParam(name = "ubicacion", required = false) String[] neighbourhoodFilters,
            @RequestParam(name = "calificacion", required = false) String ratingFilters,
            @RequestParam(name = "pagina", required = false) Integer page,
            @RequestParam(name="query",required=false) String query
    ) {
        if(page == null) page = 0;
        final ModelAndView mav = new ModelAndView("services");

        List<Service> serviceList = ss.services(page, category, neighbourhoodFilters, ratingFilters, query);
        mav.addObject("services", serviceList);
        mav.addObject("page", page);
        mav.addObject("isServicesEmpty", serviceList.isEmpty());
        mav.addObject("category", category);
        mav.addObject("location", neighbourhoodFilters);
        mav.addObject("resultsAmount", ss.getServiceCount(category, neighbourhoodFilters, ratingFilters, query));
        mav.addObject("pageCount", ss.getPageCount(category, neighbourhoodFilters, ratingFilters, query));
        return mav;
    }

    @RequestMapping(method = RequestMethod.GET, path="/crear-servicio")
    public ModelAndView selectBusinessForService() {
        final ModelAndView mav = new ModelAndView("businessesForNewService");
        long userid = authControl.getCurrentUser().get().getUserId();
        User currentUser = us.findById(userid).orElseThrow(UserNotFoundException::new);
        List<Business> businessList = bs.findByAdminId(currentUser.getUserId());
        mav.addObject("user",currentUser);
        mav.addObject("businessList", businessList);
        return mav;
    }

    @RequestMapping(method = RequestMethod.GET, path="/crear-servicio/{businessId:\\d+}")
    public ModelAndView registerService(@PathVariable("businessId")long businessId, @ModelAttribute("serviceForm") final ServiceForm form) {
        final ModelAndView mav = new ModelAndView("postService");
        mav.addObject("durationTypes",DurationTypes.values());
        return mav;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/crear-servicio/{businessId:\\d+}")
    public ModelAndView createService(@PathVariable("businessId") final long businessId, @Valid @ModelAttribute("serviceForm") final ServiceForm form, BindingResult errors) throws IOException {

        if (errors.hasErrors()) {
            return registerService(businessId, form);
        }
        Service newService = ss.create(businessId,form.getTitle(),form.getDescription(),form.getHomeserv(),form.getNeighbourhood(),form.getLocation(),form.getCategory(),form.getMinimalduration(),form.getPricingtype(),form.getPrice(),form.getAdditionalCharges(), form.getImage());
        return new ModelAndView("redirect:/servicio/"+newService.getId());
    }

    @RequestMapping(method = RequestMethod.POST , path = "/borrar-servicio/{serviceId:\\d+}")
    public ModelAndView deleteService(@PathVariable("serviceId") final long serviceId){

        ss.delete(serviceId);
        return new ModelAndView("redirect:/operacion-invalida/?argumento=servicionoexiste");
    }

    @RequestMapping(method = RequestMethod.GET, path = "/servicio/{serviceId:\\d+}")
    public ModelAndView service(
            @PathVariable("serviceId") final long serviceId,
            @ModelAttribute("questionForm") final QuestionForm questionForm,
            @ModelAttribute("reviewForm") final ReviewsForm reviewForm,
            @RequestParam(value = "opcion", required = false) final String option,
            @RequestParam(value = "qstPag", required = false) Integer questionPage,
            @RequestParam(value = "rwPag", required = false) Integer reviewPage
    ) {
        final ModelAndView mav = new ModelAndView("service");
        Optional<User> currentUser = authControl.getCurrentUser();
        Long userId = currentUser.isPresent() ? currentUser.get().getUserId() : null;
        Service serv;
        if(questionPage == null) questionPage = 0;
        if(reviewPage == null) reviewPage = 0;
        try {
            serv = ss.findById(serviceId).orElseThrow(ServiceNotFoundException::new);
        } catch (ServiceNotFoundException e) {
            return new ModelAndView("redirect:/operacion-invalida/?argumento=servicionoexiste");
        }
        Business business = bs.findById(serv.getBusinessid()).get();
        boolean isOwner = userId != null && business.getUserId()==userId;

        mav.addObject("isOwner", isOwner);
        mav.addObject("option", option);
        mav.addObject("avgRating", rating.getRatingsAvg(serviceId));
        mav.addObject("service",serv);
        mav.addObject("questions", question.getAllQuestions(serviceId, questionPage));
        mav.addObject("reviews", rating.getAllRatings(serviceId, reviewPage));
        mav.addObject("questionsCount", question.getQuestionsCount(serviceId));
        mav.addObject("reviewsCount", rating.getRatingsCount(serviceId));
        mav.addObject("questionPage", questionPage);
        mav.addObject("reviewPage", reviewPage);
        mav.addObject("TBDPricing", TBDPricing);
        mav.addObject("hasAlreadyRated", (userId==null)? null : rating.hasAlreadyRated(userId, serviceId));
        return mav;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/contratar-servicio/{serviceId:\\d+}")
    public ModelAndView hireService(@PathVariable("serviceId") final long serviceId, @ModelAttribute("appointmentForm") final AppointmentForm form) {

        final ModelAndView mav = new ModelAndView("postAppointment");
        try {
            Service service = ss.findById(serviceId).orElseThrow(ServiceNotFoundException::new);
            mav.addObject("service",service);
        } catch (ServiceNotFoundException ex) {
            return new ModelAndView("redirect:/operacion-invalida/?argumento=servicionoexiste");
        }

        return mav;
    }

}