package uk.gov.laa.gpfd.config;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.web.savedrequest.SavedRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomAuthSuccessHandlerTest {

    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private final CustomAuthSuccessHandler customAuthSuccessHandler = new CustomAuthSuccessHandler(appConfig);

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private Authentication mockAuthentication;

    @Mock
    private HttpSession mockSession;

    @BeforeEach
    void beforeEach() {
        reset(mockRequest, mockResponse, mockAuthentication, mockSession, appConfig);
        when(mockRequest.getSession()).thenReturn(mockSession);
    }

    @SneakyThrows
    @Test
    void shouldFollowRedirectUrlIfDefinedAndNonEmptyAndWhiteListed() {

        when(mockSession.getAttribute("redirect_uri")).thenReturn("/hi");
        when(appConfig.isValidRedirectUri("/hi")).thenReturn(true);

        customAuthSuccessHandler.onAuthenticationSuccess(mockRequest, mockResponse, mockAuthentication);

        verify(mockResponse).sendRedirect("/hi");

    }

    @SneakyThrows
    @Test
    void shouldNotFollowRedirectUrlIfNotWhiteListed() {

        when(mockSession.getAttribute("redirect_uri")).thenReturn("/hi");
        when(appConfig.isValidRedirectUri("/hi")).thenReturn(false);

        customAuthSuccessHandler.onAuthenticationSuccess(mockRequest, mockResponse, mockAuthentication);

        verify(mockResponse, times(0)).sendRedirect("/hi");

    }

    @SneakyThrows
    @Test
    void shouldFollowRedirectUrlIfDefinedAndSavedRequestAlsoDefined() {

        SavedRequest savedRequest = mock(SavedRequest.class);

        when(mockSession.getAttribute("redirect_uri")).thenReturn("/hi");
        when(mockSession.getAttribute("SPRING_SECURITY_SAVED_REQUEST")).thenReturn(savedRequest);
        when(appConfig.isValidRedirectUri("/hi")).thenReturn(true);

        customAuthSuccessHandler.onAuthenticationSuccess(mockRequest, mockResponse, mockAuthentication);

        verify(mockResponse).sendRedirect("/hi");

    }

    @SneakyThrows
    @Test
    void shouldFollowSavedRequestIfDefinedAndRedirectUrlIsNot() {

        SavedRequest savedRequest = mock(SavedRequest.class);

        when(mockSession.getAttribute("redirect_uri")).thenReturn(null);
        when(mockSession.getAttribute("SPRING_SECURITY_SAVED_REQUEST")).thenReturn(savedRequest);
        when(savedRequest.getRedirectUrl()).thenReturn("/hello");

        customAuthSuccessHandler.onAuthenticationSuccess(mockRequest, mockResponse, mockAuthentication);

        verify(mockResponse).sendRedirect("/hello");

    }

    @SneakyThrows
    @Test
    void shouldNotRedirectIfNeitherRedirectUriNorSavedRequestDefined() {

        when(mockSession.getAttribute("redirect_uri")).thenReturn(null);
        when(mockSession.getAttribute("SPRING_SECURITY_SAVED_REQUEST")).thenReturn(null);

        customAuthSuccessHandler.onAuthenticationSuccess(mockRequest, mockResponse, mockAuthentication);

        verify(mockResponse, times(0)).sendRedirect(any());

    }

    @SneakyThrows
    @Test
    void shouldNotFollowRedirectUriIfItIsEmptyString() {

        when(mockSession.getAttribute("redirect_uri")).thenReturn("");
        when(mockSession.getAttribute("SPRING_SECURITY_SAVED_REQUEST")).thenReturn(null);

        customAuthSuccessHandler.onAuthenticationSuccess(mockRequest, mockResponse, mockAuthentication);

        verify(mockResponse, times(0)).sendRedirect(any());

    }

}
