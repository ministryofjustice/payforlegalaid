package uk.gov.laa.gpfd.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@AllArgsConstructor
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    AppConfig appConfig;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        log.debug("Authentication successful");

        HttpSession session = request.getSession();
        Object redirectUriObject = session.getAttribute("redirect_uri");
        Object savedRequestObject = session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");

        boolean validRedirectUri = false;

        if (redirectUriObject instanceof String redirectUri && !redirectUri.isEmpty()) {
            if (appConfig.isValidRedirectUri(redirectUri)) {
                validRedirectUri = true;
            } else {
                // Should have caught invalid redirectUri earlier and not saved it in the session but belt and braces
                log.warn("Invalid Redirect URI was supplied and was ignored");
            }
        }

        if (validRedirectUri) {
            // We passed a redirect uri into our auth request (aka request is from the ui) and it is whitelisted
            log.info("After authentication we are returning to the request's Redirect URI");
            response.sendRedirect(redirectUriObject.toString());
        } else if (savedRequestObject instanceof SavedRequest savedRequest) {
            // We came here from another part of the api, such as trying to load the /reports endpoint without being logged in
            log.info("After authentication we are returning to their original URI");
            response.sendRedirect(savedRequest.getRedirectUrl());
        } else {
            // Else just let Spring do what it wants to do - will probably be the redirect-uri-template
            log.info("After authentication, redirect to the default URI");
        }

    }

}
