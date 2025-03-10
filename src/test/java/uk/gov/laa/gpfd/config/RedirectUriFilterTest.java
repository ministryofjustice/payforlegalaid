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
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedirectUriFilterTest {

    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private final RedirectUriFilter redirectUriFilter = new RedirectUriFilter(appConfig);

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private FilterChain mockFilterChain;

    @Mock
    private HttpSession mockSession;

    @BeforeEach
    void beforeEach() {
        reset(mockRequest, mockResponse, mockFilterChain, mockSession);
    }

    @SneakyThrows
    @Test
    void shouldStoreRedirectUriIfSuppliedAndWhiteListed() {

        when(mockRequest.getParameter("redirect_uri")).thenReturn("/hi");
        when(appConfig.isValidRedirectUri("/hi")).thenReturn(true);
        when(mockRequest.getSession()).thenReturn(mockSession);

        redirectUriFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockSession).setAttribute("redirect_uri", "/hi");

    }

    @SneakyThrows
    @Test
    void shouldNotAttemptToStoreRedirectUriIfNotWhiteListed() {

        when(mockRequest.getParameter("redirect_uri")).thenReturn("/hi");
        when(appConfig.isValidRedirectUri("/hi")).thenReturn(false);

        redirectUriFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockSession, times(0)).setAttribute("redirect_uri", any());

    }


    @SneakyThrows
    @Test
    void shouldNotAttemptToStoreRedirectUriIfNotSet() {

        when(mockRequest.getParameter("redirect_uri")).thenReturn(null);

        redirectUriFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockSession, times(0)).setAttribute("redirect_uri", any());

    }

    @SneakyThrows
    @Test
    void shouldNotAttemptToStoreRedirectUriIfEmpty() {

        when(mockRequest.getParameter("redirect_uri")).thenReturn("");

        redirectUriFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockSession, times(0)).setAttribute("redirect_uri", any());

    }

}
