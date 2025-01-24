package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.auth.ServinetAuthControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class ErrorsController {

    private final UserService userService;
    private final ServinetAuthControl authControl;

    @Autowired
    public ErrorsController(
            @Qualifier("userServiceImpl") final UserService userService,
            @Qualifier("servinetAuthControl") final ServinetAuthControl authControl
    ) {
        this.userService = userService;
        this.authControl = authControl;
    }


    @RequestMapping("/400")
    @ResponseStatus(value= HttpStatus.BAD_REQUEST)
    public ModelAndView badRequest(){
        return new ModelAndView("/errors/400");
    }


    @RequestMapping("/404")
    @ResponseStatus(value= HttpStatus.NOT_FOUND)
    public ModelAndView notFound(){
        return new ModelAndView("/errors/404");
    }

    @RequestMapping("/401")
    @ResponseStatus(value= HttpStatus.UNAUTHORIZED)
    public ModelAndView unauthorized(){
        return new ModelAndView("/errors/401");
    }

    @RequestMapping("/403")
    @ResponseStatus(value= HttpStatus.FORBIDDEN)
    public ModelAndView forbidden(){
        boolean isUnverified = !authControl.isVerified();

        return new ModelAndView("/errors/403").addObject("isUnverified", isUnverified);
    }

    @RequestMapping("/500")
    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView internalServerError(){
        return new ModelAndView("/errors/500");
    }
}
