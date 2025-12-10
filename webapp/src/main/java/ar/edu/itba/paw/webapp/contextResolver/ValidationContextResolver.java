package ar.edu.itba.paw.webapp.contextResolver;

import org.glassfish.jersey.server.validation.ValidationConfig;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.Validation;
import javax.ws.rs.ext.ContextResolver;

import javax.validation.MessageInterpolator;
import javax.ws.rs.ext.Provider;
import java.util.Locale;

@Component
@Provider
public class ValidationContextResolver implements ContextResolver<ValidationConfig> {
    private final MessageInterpolator interpolator;
    @Autowired
    public ValidationContextResolver(MessageSource messageSource) {
        this.interpolator = new ValidatorMessageInterpolator(messageSource);
    }
    @Override
    public ValidationConfig getContext(Class<?> type) {
        final ValidationConfig config = new ValidationConfig();
        config.messageInterpolator(this.interpolator);
        return config;
    }

    private static class ValidatorMessageInterpolator implements MessageInterpolator {

        private final MessageInterpolator defaultInterpolator;
        private final MessageSource messageSource;
        public ValidatorMessageInterpolator(MessageSource messageSource) {
            this.messageSource=messageSource;
            defaultInterpolator = Validation.byDefaultProvider().configure().getDefaultMessageInterpolator();
        }

        @Override
        public String interpolate(String messageTemplate, Context context) {
            return this.interpolate(messageTemplate, context, LocaleContextHolder.getLocale());
        }

        @Override
        public String interpolate(String messageTemplate, Context context, Locale locale) {
            String resolved= defaultInterpolator.interpolate(messageTemplate,context,locale);

            return messageSource.getMessage(resolved,null,resolved,locale);
        }
    }
}