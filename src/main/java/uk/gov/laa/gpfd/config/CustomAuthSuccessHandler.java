package uk.gov.laa.gpfd.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        log.debug("Authentication successful");

        HttpSession session = request.getSession();

        Object redirectUrlObject = session.getAttribute("redirect_uri");
        Object savedRequestObject = session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");

        if (redirectUrlObject instanceof String redirectUrl && !redirectUrl.isEmpty()) {
            // We passed a redirect url into our auth request (aka request is from the ui)
            log.info("After authentication we are returning to the request redirect url");
            response.sendRedirect(redirectUrl);
        } else if (savedRequestObject instanceof SavedRequest savedRequest) {
            // We came here from another part of the api, such as trying to load the /reports endpoint without being logged in
            log.info("After authentication we are returning to their original url");
            response.sendRedirect(savedRequest.getRedirectUrl());
        } else {
            // Else just let Spring do what it wants to do
            log.info("After authentication, redirect to the default url");
        }

    }

}
