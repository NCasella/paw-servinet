package ar.edu.itba.paw.webapp.jersey;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {

    @RequestMapping(value = "/{path:[^\\.]*}")
    public String redirectToSPA() {
        return "forward:/index.html";
    }
}
