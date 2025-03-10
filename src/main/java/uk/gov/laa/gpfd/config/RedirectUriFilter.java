package uk.gov.laa.gpfd.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@AllArgsConstructor
@Slf4j
@Component
class RedirectUriFilter implements Filter {

    AppConfig appConfig;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        // If a redirect URI is passed in, we need it after the auth journey is complete.
        // The use case of this is the UI logging in and returning back to the UI.
        // By filtering here, we can store the redirect uri before Spring throws it away
        // And ensure it matches our white list!
        // Because there are multiple redirects happen between here and then as it goes through the Entra id journey
        // We retrieve this value later on in the auth success handler
        String redirectUri = httpServletRequest.getParameter("redirect_uri");

        if (appConfig.isValidRedirectUri(redirectUri)) {
            HttpSession session = httpServletRequest.getSession();

            session.setAttribute("redirect_uri", redirectUri);
            log.debug("Valid Redirect URI was supplied and saved");
        } else if (redirectUri != null && !redirectUri.isEmpty()){
            log.warn("Invalid Redirect URI was supplied: {}. This will not be followed and defaults will be used.", redirectUri);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
