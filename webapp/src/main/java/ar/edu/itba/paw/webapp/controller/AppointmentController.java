package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.AppointmentNonExistentException;
import ar.edu.itba.paw.model.exceptions.ServiceNotFoundException;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.auth.ServinetAuthControl;
import ar.edu.itba.paw.webapp.form.AppointmentForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class AppointmentController {

    private final ServiceService serviceService;
    private final AppointmentService appointmentService;
    private final ServinetAuthControl authControl;
    @Autowired
    public AppointmentController(
        @Qualifier("serviceServiceImpl") final ServiceService serviceService,
        @Qualifier("appointmentServiceImpl") final AppointmentService appointmentService,
        @Qualifier("servinetAuthControl") final ServinetAuthControl authControl
    ){
        this.serviceService = serviceService;
        this.appointmentService = appointmentService;
        this.authControl= authControl;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/contratar-servicio/{serviceId:\\d+}")
    public ModelAndView hireService(@PathVariable("serviceId") final long serviceId, @ModelAttribute("appointmentForm") final AppointmentForm form) {

        final ModelAndView mav = new ModelAndView("postAppointment");
        Service service = serviceService.findById(serviceId).orElseThrow(ServiceNotFoundException::new);
        mav.addObject("service",service);
        return mav;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/contratar-servicio/{serviceId:\\d+}")
    public ModelAndView appointment(
            @PathVariable("serviceId") final long serviceId,
            @Valid @ModelAttribute("appointmentForm") AppointmentForm form, BindingResult errors
    ){

        if(errors.hasErrors()) {
            return hireService(serviceId, form);
        }
        User user = authControl.getCurrentUser().orElseThrow(UserNotFoundException::new);

        Appointment createdAppointment = appointmentService.create(serviceId,user.getName(),user.getSurname(),user.getEmail(),form.getLocation(),user.getEmail(), form.getLocalDateTime().toString(), form.getDescription());
        return new ModelAndView("redirect:/turno/"+ serviceId + "/" + createdAppointment.getId());
    }


    @RequestMapping(method = RequestMethod.POST , path = "/aceptar-turno/{appointmentId:\\d+}")
    public void confirmAppointmentPost(@PathVariable("appointmentId") final long appointmentId) {
        appointmentService.confirmAppointment(appointmentId);
    }
    @RequestMapping(method = RequestMethod.POST , path = "/rechazar-turno/{appointmentId:\\d+}")
    public void denyAppointmentPost(@PathVariable("appointmentId") final long appointmentId) {
        appointmentService.denyAppointment(appointmentId);
    }

    @RequestMapping(method = RequestMethod.POST , path = "/cancelar-turno/{appointmentId:\\d+}")
    public ModelAndView cancelAppointmentFromAppointmentView(@PathVariable("appointmentId") final long appointmentId) {
        final long serviceId = appointmentService.cancelAppointment(appointmentId);
        return new ModelAndView("redirect:/sinturno/" + serviceId + "/?argumento=cancelado");
    }


    @RequestMapping(method = RequestMethod.DELETE , path = "/cancelar-turno/{appointmentId:\\d+}")
    public void cancelAppointment(@PathVariable("appointmentId") final long appointmentId){
        appointmentService.cancelAppointment(appointmentId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/turno/{serviceId:\\d+}/{appointmentId:\\d+}")
    public ModelAndView getAppointment(
            @PathVariable("appointmentId") final long appointmentId,
            @PathVariable("serviceId") final long serviceId) {

        Optional<Appointment> optionalAppointment = appointmentService.findById(appointmentId);
        if(!optionalAppointment.isPresent()) {
            if(serviceService.findById(serviceId).isPresent() ) {
                return new ModelAndView("redirect:/sinturno/" + serviceId + "/?argumento=cancelado");
            }
            else {
                return new ModelAndView("redirect:/sinturno/" + serviceId + "/?argumento=noexiste");
            }
        }

        Appointment app = appointmentService.findById(appointmentId).orElseThrow(AppointmentNonExistentException::new);
        User user = authControl.getCurrentUser().orElseThrow(UserNotFoundException::new);

        Service service = app.getServiceAppointed();
        final ModelAndView mav = new ModelAndView("appointment");
        mav.addObject("appointment", app);
        mav.addObject("user", user);
        mav.addObject("service", service);
        mav.addObject("new", true);
        mav.addObject("confirmed", app.getConfirmed());
        mav.addObject("TBDPricing", PricingTypes.TBD.getValue());
        return mav;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/sinturno/{serviceId:\\d+}")
    public ModelAndView noneAppointment(
            @PathVariable("serviceId") final long serviceId,
            @RequestParam(name = "argumento") String argument
    ){
        final ModelAndView mav = new ModelAndView("noneAppointment");
        mav.addObject("argument", argument);
        mav.addObject("serviceId", serviceId);
        return mav;
    }

}

