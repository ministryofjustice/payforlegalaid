package uk.gov.laa.gpfd.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RedirectUriFilterTest {

    @InjectMocks
    private final RedirectUriFilter redirectUriFilter = new RedirectUriFilter();

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private FilterChain mockFilterChain;

    @Mock
    private HttpSession mockSession;

    @BeforeEach
    public void beforeEach() {
        reset(mockRequest, mockResponse, mockFilterChain, mockSession);
        when(mockRequest.getSession()).thenReturn(mockSession);
    }

    @SneakyThrows
    @Test
    public void shouldStoreRedirectUriIfSupplied() {

        when(mockRequest.getParameter(eq("redirect_uri"))).thenReturn("/hi");

        redirectUriFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockSession).setAttribute(eq("redirect_uri"), eq("/hi"));

    }

    @SneakyThrows
    @Test
    public void shouldNotAttemptToStoreRedirectUriIfNotSet() {

        when(mockRequest.getParameter(eq("redirect_uri"))).thenReturn(null);

        redirectUriFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockSession, times(0)).setAttribute(eq("redirect_uri"), any());

    }

    @SneakyThrows
    @Test
    public void shouldNotAttemptToStoreRedirectUriIfEmptyString() {

        when(mockRequest.getParameter(eq("redirect_uri"))).thenReturn("");

        redirectUriFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockSession, times(0)).setAttribute(eq("redirect_uri"), any());

    }
}
