package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.model.Neighbourhoods;
import ar.edu.itba.paw.webapp.form.ServiceForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LocationFormattedValidator implements ConstraintValidator<LocationFormatted, ServiceForm> {
//    private Neighbourhoods[] neighbourhoods;
 //   private boolean hasHomeService;
    @Override
    public void initialize(LocationFormatted constraintAnnotation) {

    }
    @Override
    public boolean isValid(ServiceForm form, ConstraintValidatorContext context) {
        boolean homeserv=form.getHomeserv();//Neighbourhoods[] neighbourhoods1 = (Neighbourhoods[]) new BeanWrapperImpl(value).getPropertyValue(neighbourhoods);
        Neighbourhoods[] neighbourhoods = homeserv? form.getNeighbourhood():form.getUniqueNeighbourhood();
        return neighbourhoods!=null && ((homeserv && neighbourhoods.length > 0) || (!homeserv && neighbourhoods.length==1 ) );//Boolean hasHomeService1 = (Boolean) new BeanWrapperImpl(value).getPropertyValue(hasHomeService);
    }

}
