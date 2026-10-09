package uk.gov.laa.gpfd.config;

import io.sentry.ScopeCallback;
import io.sentry.Sentry;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

class SentryAuthenticationFailureHandlerTest {

    @Test
    void capturesLoginFailureAndPreservesDefaultRedirect() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var exception = new BadCredentialsException("Synthetic authentication failure");
        try (var sentry = mockStatic(Sentry.class)) {
            new SentryAuthenticationFailureHandler().onAuthenticationFailure(request, response, exception);

            sentry.verify(() -> Sentry.captureException(eq(exception), any(ScopeCallback.class)));
            sentry.verifyNoMoreInteractions();
            assertThat(response.getRedirectedUrl()).isEqualTo("/login?error");
            assertThat(response.getStatus()).isEqualTo(302);
        }
    }
}