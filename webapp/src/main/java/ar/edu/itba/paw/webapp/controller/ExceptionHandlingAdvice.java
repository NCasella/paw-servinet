package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.exceptions.InvalidFilterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ar.edu.itba.paw.model.exceptions.InvalidOperationException;
@ControllerAdvice
public class ExceptionHandlingAdvice {

    @ExceptionHandler(InvalidOperationException.class)
    public ModelAndView invalidOperation(InvalidOperationException ex){
        return new ModelAndView("redirect:/operacion-invalida/?argumento="+ ex.getArgument());
    }

    @ExceptionHandler(InvalidFilterException.class)
    public ModelAndView invalidFilter(){
        return new ModelAndView("redirect:/400");
    }
}
