package uk.gov.laa.gpfd.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
class RedirectUriFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        // If a redirect URI is passed in, we need it after the auth journey is complete.
        // The use case of this is the UI logging in and returning back to the UI.
        // By filtering here, we can store the redirect uri before Spring throws it away
        // Because there are multiple redirects happen between here and then as it goes through the Entra id journey
        // We retrieve this value later on in the auth success handler

        String redirectUri = httpServletRequest.getParameter("redirect_uri");
        HttpSession session = httpServletRequest.getSession();

        if (redirectUri != null && !redirectUri.isEmpty()) {
            session.setAttribute("redirect_uri", redirectUri);
            log.debug("Redirect URI was supplied and saved");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
