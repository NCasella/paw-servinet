package ar.edu.itba.paw.webapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorsController {

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

    @RequestMapping("/403")
    @ResponseStatus(value= HttpStatus.FORBIDDEN)
    public ModelAndView forbidden(){
        return new ModelAndView("/errors/403");
    }

    @RequestMapping("/500")
    @ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView internalServerError(){
        return new ModelAndView("/errors/500");
    }
}
