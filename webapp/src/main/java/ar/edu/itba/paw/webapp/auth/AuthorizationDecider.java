package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.mediaType.CustomMediaTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.function.Supplier;

@Component
public class AuthorizationDecider {

    @Autowired
    private ServinetAuthControl authControl;

    public AuthorizationDecision canChangeService (Supplier<Authentication> auth, RequestAuthorizationContext context){
        long serviceId=Long.parseLong(context.getVariables().getOrDefault("serviceId","-1"));
        return new AuthorizationDecision(authControl.isServiceOwner(serviceId));
    }
    public AuthorizationDecision isCurrentUserBusinessOwner(Supplier<Authentication> auth, RequestAuthorizationContext context){
        long businessId=Long.parseLong(context.getVariables().getOrDefault("businessId","-1"));
        Optional<User> currentUser=authControl.getCurrentUser();
        if(currentUser.isEmpty() || businessId==-1){
            return new AuthorizationDecision(false);
        }
        return new AuthorizationDecision(authControl.isBusinessOwner(businessId,currentUser.get().getUserId()));

    }
    public AuthorizationDecision isCurrentUser(Supplier<Authentication> auth,RequestAuthorizationContext context){
        long userId=Long.parseLong(context.getVariables().getOrDefault("userId","-1"));
        return new AuthorizationDecision(authControl.isCurrentUser(userId));
    }
    public AuthorizationDecision canViewAppointment(Supplier<Authentication> auth,RequestAuthorizationContext context){
        long appointmentId=Long.parseLong(context.getVariables().getOrDefault("appointmentId","-1"));
        if(appointmentId==-1){
            return new AuthorizationDecision(false);
        }
        return new AuthorizationDecision(authControl.isAdminAppointment(appointmentId)||authControl.isUserAppointment(appointmentId));
    }
    public AuthorizationDecision canViewUserContactInfo(Supplier<Authentication> auth,RequestAuthorizationContext context){
        String requestMimeType=context.getRequest().getHeader(HttpHeaders.ACCEPT);
        long userId=Long.parseLong(context.getVariables().getOrDefault("userId","-1"));
        Optional<User> currentUser=authControl.getCurrentUser();
        if (currentUser.isEmpty()||userId==-1 || requestMimeType==null) {
            return new AuthorizationDecision(false);
        }
        long currentUserId=currentUser.get().getUserId();
        boolean allowed = !requestMimeType.contains(CustomMediaTypes.USER_CONTACT_INFO) || userId==currentUserId || authControl.isUserProvidee(currentUserId,userId);
        return new AuthorizationDecision(allowed);
    }

    public AuthorizationDecision canViewAppointmentList(Supplier<Authentication> auth,RequestAuthorizationContext context){
        HttpServletRequest request=context.getRequest();
        Optional<User> currentUser=authControl.getCurrentUser();
        if(currentUser.isEmpty()){
            return new AuthorizationDecision(false);
        }

        String userIdParam=request.getParameter("userId");
        String businessIdParam=request.getParameter("businessId");
        long currentUserId=currentUser.get().getUserId();

        boolean allow= (userIdParam!=null && authControl.isCurrentUser(Long.parseLong(userIdParam)))||(businessIdParam!=null && authControl.isBusinessOwner(Long.parseLong(businessIdParam),currentUserId));

        return new AuthorizationDecision(allow);

    }
}
