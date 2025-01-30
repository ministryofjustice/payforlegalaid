package uk.gov.laa.gpfd.bean;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Service;
//import org.springframework.security.oauth2.server.resource.authentication;

@Service
public class JwtLogger {

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
//        if (success.getAuthentication() instanceof JwtAuthenticationToken){}
        System.out.println(success.getAuthentication());
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failures) {
    }

}
