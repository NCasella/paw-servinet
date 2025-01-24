package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Categories;
import ar.edu.itba.paw.model.Neighbourhoods;
import ar.edu.itba.paw.model.PricingTypes;
import ar.edu.itba.paw.model.Ratings;
import ar.edu.itba.paw.services.EmailService;
import ar.edu.itba.paw.services.UserVerificationService;
import ar.edu.itba.paw.webapp.auth.ServinetAuthControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ModelAttributesControllerAdvice {

    private final ServinetAuthControl authControl;

    @Autowired
    public ModelAttributesControllerAdvice(final ServinetAuthControl authControl) {
        this.authControl = authControl;
    }

    @ModelAttribute("categories")
    public Categories[] categories() {
        return Categories.values();
    }
    @ModelAttribute("neighbourhoods")
    public Neighbourhoods[] neighbourhoods() {
        return Neighbourhoods.values();
    }

    @ModelAttribute("pricingTypes")
    public PricingTypes[] pricingTypes() {
        return PricingTypes.values();
    }
    @ModelAttribute("ratings")
    public Ratings[] ratings() {
        return Ratings.values();
    }

    @ModelAttribute("isLoggedIn")
    public boolean isLoggedIn(){
        return authControl.isLoggedIn();
    }

    @ModelAttribute("isProvider")
    public boolean isProvider(){
        return authControl.isProvider();
    }

    @ModelAttribute("isVerified")
    public boolean isVerified(){
        return authControl.isVerified();
    }
}
