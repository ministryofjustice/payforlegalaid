package uk.gov.laa.gpfd.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.gov.laa.gpfd.utils.RequestLogUtils;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserIdContextFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void tearDown() {
        MDC.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldPutUserIdInMdcWhenAuthenticated() throws ServletException, IOException {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("user123");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        UserIdContextFilter filter = new UserIdContextFilter();
        filter.doFilterInternal(request, response, filterChain);

        String expectedUserId = UUID.nameUUIDFromBytes("user123".getBytes()).toString();
        assertEquals(expectedUserId, MDC.get(RequestLogUtils.USER_ID));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotPutUserIdInMdcWhenNotAuthenticated() throws ServletException, IOException {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        UserIdContextFilter filter = new UserIdContextFilter();
        filter.doFilterInternal(request, response, filterChain);

        assertNull(MDC.get(RequestLogUtils.USER_ID));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotPutUserIdInMdcWhenNoAuthentication() throws ServletException, IOException {
        SecurityContextHolder.clearContext();

        UserIdContextFilter filter = new UserIdContextFilter();
        filter.doFilterInternal(request, response, filterChain);

        assertNull(MDC.get(RequestLogUtils.USER_ID));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueFilterChainRegardlessOfAuthentication() throws ServletException, IOException {
        UserIdContextFilter filter = new UserIdContextFilter();
        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
