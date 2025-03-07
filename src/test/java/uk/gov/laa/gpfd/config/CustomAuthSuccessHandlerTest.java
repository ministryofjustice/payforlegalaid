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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomAuthSuccessHandlerTest {

    @InjectMocks
    private final CustomAuthSuccessHandler customAuthSuccessHandler = new CustomAuthSuccessHandler();

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private Authentication mockAuthentication;

    @Mock
    private HttpSession mockSession;

    @BeforeEach
    public void beforeEach() {
        reset(mockRequest, mockResponse, mockAuthentication, mockSession);
        when(mockRequest.getSession()).thenReturn(mockSession);
    }

    @SneakyThrows
    @Test
    public void shouldFollowRedirectUrlIfDefinedAndNonEmpty() {

        when(mockSession.getAttribute(eq("redirect_uri"))).thenReturn("/hi");

        customAuthSuccessHandler.onAuthenticationSuccess(mockRequest, mockResponse, mockAuthentication);

        verify(mockResponse).sendRedirect("/hi");

    }

    @SneakyThrows
    @Test
    public void shouldFollowRedirectUrlIfDefinedAndSavedRequestAlsoDefined() {

        SavedRequest savedRequest = mock(SavedRequest.class);

        when(mockSession.getAttribute(eq("redirect_uri"))).thenReturn("/hi");
        when(mockSession.getAttribute(eq("SPRING_SECURITY_SAVED_REQUEST"))).thenReturn(savedRequest);

        customAuthSuccessHandler.onAuthenticationSuccess(mockRequest, mockResponse, mockAuthentication);

        verify(mockResponse).sendRedirect("/hi");

    }

    @SneakyThrows
    @Test
    public void shouldFollowSavedRequestIfDefinedAndRedirectUrlIsNot() {

        SavedRequest savedRequest = mock(SavedRequest.class);

        when(mockSession.getAttribute(eq("redirect_uri"))).thenReturn(null);
        when(mockSession.getAttribute(eq("SPRING_SECURITY_SAVED_REQUEST"))).thenReturn(savedRequest);
        when(savedRequest.getRedirectUrl()).thenReturn("/hello");

        customAuthSuccessHandler.onAuthenticationSuccess(mockRequest, mockResponse, mockAuthentication);

        verify(mockResponse).sendRedirect("/hello");

    }

    @SneakyThrows
    @Test
    public void shouldNotRedirectIfNeitherRedirectUriNorSavedRequestDefined() {

        when(mockSession.getAttribute(eq("redirect_uri"))).thenReturn(null);
        when(mockSession.getAttribute(eq("SPRING_SECURITY_SAVED_REQUEST"))).thenReturn(null);

        customAuthSuccessHandler.onAuthenticationSuccess(mockRequest, mockResponse, mockAuthentication);

        verify(mockResponse, times(0)).sendRedirect(any());

    }

    @SneakyThrows
    @Test
    public void shouldNotFollowRedirectUriIfItIsEmptyString() {

        when(mockSession.getAttribute(eq("redirect_uri"))).thenReturn("");
        when(mockSession.getAttribute(eq("SPRING_SECURITY_SAVED_REQUEST"))).thenReturn(null);

        customAuthSuccessHandler.onAuthenticationSuccess(mockRequest, mockResponse, mockAuthentication);

        verify(mockResponse, times(0)).sendRedirect(any());

    }

}
