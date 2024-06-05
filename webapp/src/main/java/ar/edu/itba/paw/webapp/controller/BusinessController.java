package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.AppointmentNonExistentException;
import ar.edu.itba.paw.model.exceptions.BusinessNotFoundException;

import ar.edu.itba.paw.model.exceptions.InvalidFilterException;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.auth.ServinetAuthControl;

import ar.edu.itba.paw.webapp.form.BusinessForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class BusinessController {

    private final BusinessService businessService;
    private final ServiceService serviceService;
    private final AppointmentService appointmentService;
    private final UserService userService;
    private final ServinetAuthControl authControl;
    private final RatingService ratingService;

    List<Neighbourhoods> neighbourhoods = Arrays.asList(Neighbourhoods.values());
    @Autowired
    public BusinessController(@Qualifier("BusinessServiceImpl") final BusinessService businessService,  @Qualifier("serviceServiceImpl") final ServiceService serviceService,
                              @Qualifier("appointmentServiceImpl") final AppointmentService appointmentService,
                              @Qualifier("userServiceImpl") final UserService userService,
                              @Qualifier("servinetAuthControl") final ServinetAuthControl authControl,
                              @Qualifier("RatingServiceImpl") final RatingService ratingService
    ){
        this.businessService = businessService;
        this.serviceService = serviceService;
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.authControl = authControl;
        this.ratingService = ratingService;
    }


    @RequestMapping(method = RequestMethod.GET, path="/registrar-negocio")
    public ModelAndView registerBusiness(@ModelAttribute("BusinessForm") final BusinessForm form) {
        final ModelAndView mav = new ModelAndView("postBusiness");
        User user = authControl.getCurrentUser().orElseThrow(UserNotFoundException::new);
        form.setBusinessEmail(user.getEmail());
        form.setBusinessTelephone(user.getTelephone());
        mav.addObject("neighbours",neighbourhoods);
        return mav;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/registrar-negocio")
    public ModelAndView postBusiness(@ModelAttribute("BusinessForm") @Valid final BusinessForm form, final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return registerBusiness(form);
        }
        /*
        ServinetAuthUserDetails userDetails = (ServinetAuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null){
            return new ModelAndView("redirect:/login");
        }
         */
        long userid = authControl.getCurrentUser().orElseThrow(UserNotFoundException::new).getUserId();
        Business business = businessService.createBusiness(form.getBusinessName(),userid, form.getBusinessTelephone(), form.getBusinessEmail(),form.getBusinessLocation());
        return new ModelAndView("redirect:/negocio/"+ business.getBusinessid());
    }

    @RequestMapping(method = RequestMethod.POST , path = "/borrar-negocio/{businessId:\\d+}")
    public ModelAndView deleteBusiness(@PathVariable("businessId") final long businessId){
        businessService.deleteBusiness(businessId);
        return new ModelAndView("redirect:/negocios");
    }

    //todo: available only for business admin
    @RequestMapping(method = RequestMethod.GET, path = "/negocio/{businessId:\\d+}/estadisticas")
    public ModelAndView appointmentStatistics(@PathVariable("businessId") final long businessId,
                                               @RequestParam(name = "filtro", required = false, defaultValue = "w") String filterId) {
        final Business business = businessService.findById(businessId).orElseThrow(BusinessNotFoundException::new);
        DateIntervalFilter filter = DateIntervalFilter.of(filterId);
        if (filter==null) throw new InvalidFilterException();
        final List<BasicService> services = serviceService.getAllBusinessBasicServices(businessId);

        Map<Long, BasicService> serviceMap = new HashMap<>();
        services.forEach(service -> serviceMap.put(service.getId(), service));
        Set<Long> serviceIds = serviceMap.keySet();

        List<Pair<Long,Long>> serviceAppointmentCount = appointmentService.getServicesFinishedAppointmentCount(serviceIds,filter);

        final ModelAndView mav = new ModelAndView("statistics");
        mav.addObject("serviceAppointmentCountList", serviceAppointmentCount);
        mav.addObject("serviceMap", serviceMap );
        mav.addObject("filter",filterId);
        AtomicLong totalFinishedAppointments = new AtomicLong();
        serviceAppointmentCount.forEach(pair -> totalFinishedAppointments.addAndGet(pair.getValue()));
        mav.addObject("finishedAppointments",totalFinishedAppointments.get());
        Long requestedAppointments = appointmentService.getServicesRequestedAppointmentCount(serviceIds,filter);
        mav.addObject("requestedAppointments", totalFinishedAppointments.get()+requestedAppointments);
        return mav;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/negocio/{businessId:\\d+}/turnos")
    public ModelAndView businessesAppointments(@PathVariable("businessId") final long businessId, @RequestParam(name = "confirmados") final boolean confirmed,
                                               @RequestParam(name = "pagina", required = false, defaultValue = "0") Integer page) {

        Business business = businessService.findById(businessId).orElseThrow(BusinessNotFoundException::new);
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
        mav.addObject("pageCount", appointmentService.getPageCount(totalResults));
        return mav;
    }

    @RequestMapping(method = RequestMethod.POST, path = "negocio/{businessId:\\d+}/solicitud-turno/{appoinmentId:\\d+}")
    public void acceptOrDenyAppointment(@PathVariable(value = "businessId") final long businessId,
                                            @PathVariable(value = "appoinmentId") final long appoinmentId,
                                            @RequestParam(value = "accepted") final boolean accepted,
                                            HttpServletResponse response) throws IOException{
        if ( accepted )
            try {
                appointmentService.confirmAppointment(appoinmentId);
            } catch (AppointmentNonExistentException e) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().println("El turno ya no existe");
            }
        else
            appointmentService.denyAppointment(appoinmentId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/negocio/{businessId:\\d+}")
    public ModelAndView businesses(
            @PathVariable("businessId") final long businessId,
            @ModelAttribute("BusinessForm") final BusinessForm form
    ) {
        final ModelAndView mav = new ModelAndView("business");
        Business business = businessService.findById(businessId).orElseThrow(BusinessNotFoundException::new);
        List<BasicService> serviceList = serviceService.getAllBusinessBasicServices(businessId);
        Optional<User> currentUser = authControl.getCurrentUser();
        Long userId = currentUser.isPresent() ? currentUser.get().getUserId() : null;
        boolean isOwner = userId != null && business.getUserId()==userId;

        mav.addObject("business",business);
        mav.addObject("serviceList", serviceList);
        mav.addObject("isOwner", isOwner);
        mav.addObject("avgRating", business.getBusinessRatingAvg());
        return mav;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/{businessId:\\d+}/editar-negocio")
    public ModelAndView editBusiness (
            @Valid @ModelAttribute("BusinessForm") BusinessForm form, final BindingResult errors,
            @PathVariable("businessId") final long businessId
    ){
        if(errors.hasErrors()) {
            return businesses(businessId, form);
        }
        businessService.changeBusinessEmail(businessId, form.getBusinessEmail());
        businessService.changeBusinessLocation(businessId, form.getBusinessLocation());
        businessService.changeBusinessTelephone(businessId, form.getBusinessTelephone());
        return new ModelAndView("redirect:/negocio/"+businessId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/negocio/opiniones/{businessID:\\d+}")
    public ModelAndView businessReviews (
            @PathVariable("businessID") final long businessId,
            @RequestParam(value = "rwFilter", required = false) final String filter,
            @RequestParam(value = "pagina", required = false) Integer page
    ) {
        final ModelAndView mav = new ModelAndView("businessReviews");
        if(page == null) page = 1;
        Business business = businessService.findById(businessId).orElseThrow(BusinessNotFoundException::new);
        mav.addObject("business", business);
        mav.addObject("reviews", ratingService.getAllBusinessRatingsFiltered(businessId, page, RatingsFilters.findByValue(filter)));
        mav.addObject("ratingCountList", ratingService.getBusinessRatingsAvgByRate(businessId));
        mav.addObject("currentFilter", RatingsFilters.findByValue(filter));
        mav.addObject("availableFilters", RatingsFilters.values());
        mav.addObject("pageCount", ratingService.getBusinessRatingsPageCount(business));
        mav.addObject("page", page);
        return mav;
    }


}
