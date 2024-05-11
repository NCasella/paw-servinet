package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.auth.ServinetAuthControl;
import ar.edu.itba.paw.webapp.form.QuestionForm;
import ar.edu.itba.paw.webapp.form.ResponseForm;
import ar.edu.itba.paw.webapp.form.ReviewsForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.Valid;

@Controller
public class RatingsQuestionsController {

    private final  RatingService rating;
    private final  QuestionService question;
    private final ServiceController serviceController;
    private final UserController userController;
    private final ServinetAuthControl authControl;

    @Autowired
    public RatingsQuestionsController(
            @Qualifier("ServiceController") ServiceController serviceController,
            @Qualifier("userController") final UserController userController,
            @Qualifier("QuestionServiceImpl") final QuestionService question,
            @Qualifier("RatingServiceImpl") final RatingService rating,
            @Qualifier("servinetAuthControl") final ServinetAuthControl authControl
        ){
        this.serviceController = serviceController;
        this.rating = rating;
        this.question = question;
        this.userController = userController;
        this.authControl= authControl;
}

    @RequestMapping(method = RequestMethod.POST, path = "/preguntar/{serviceId:\\d+}")
    public ModelAndView addQuestion(
            @Valid @ModelAttribute("questionForm") QuestionForm form, final BindingResult errors,
            @PathVariable("serviceId") final long serviceId
    ){
        if(errors.hasErrors()) {
            return serviceController.service(serviceId, form, null, "qst", 0, 0);
        }
        long userid = authControl.getCurrentUser().get().getUserId();
        question.create(serviceId, userid, form.getQuestion());
        return new ModelAndView("redirect:/servicio/" + serviceId + "/?opcion=qst");
    }


    @RequestMapping(method = RequestMethod.POST, path = "/responder/{questionId:\\d+}")
    public ModelAndView addResponse(
            @Valid @ModelAttribute("responseForm") ResponseForm form, final BindingResult errors,
            @PathVariable("questionId") final long questionId
    ){
        if(errors.hasErrors()) {
            return userController.userServicesQuestions(form);
        }
        question.addResponse(questionId, form.getResponse());
        return new ModelAndView("redirect:/negocios/consultas");
    }


    @RequestMapping(method = RequestMethod.POST, path = "/opinar/{serviceId:\\d+}")
    public ModelAndView addReview(
            @Valid @ModelAttribute("reviewForm") ReviewsForm form, final BindingResult errors,
            @PathVariable("serviceId") final long serviceId
    ){
        if(errors.hasErrors()) {
            return serviceController.service(serviceId, null, form, "rw", 0, 0);
        }
        long userid = authControl.getCurrentUser().get().getUserId();
        if(rating.hasAlreadyRated(userid, serviceId) == null) {
            rating.create(serviceId, userid, form.getRating(), form.getComment());
        }
        return new ModelAndView("redirect:/servicio/" + serviceId + "/?opcion=rw");
    }


    @RequestMapping(method = RequestMethod.POST, path = "/editar-opinion/{serviceId:\\d+}/{ratingId:\\d+}")
    public ModelAndView editReview (
            @PathVariable("ratingId") final long ratingId,
            @PathVariable("serviceId") final long serviceId,
            @RequestParam("comment") final String newComment,
            @RequestParam("rating") final int newRating
    ){

        rating.edit(ratingId, newRating, newComment);
        return new ModelAndView("redirect:/servicio/" + serviceId + "/?opcion=rw");
    }


}
