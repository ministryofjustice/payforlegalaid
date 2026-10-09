package uk.gov.laa.gpfd.controller.ui;

import io.sentry.ScopeCallback;
import io.sentry.Sentry;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.Model;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

class UIExceptionHandlerTest {

    private final UIExceptionHandler handler = new UIExceptionHandler();

    @Test
    void capturesUnexpectedUiFailureExactlyOnce() {
        var exception = new IllegalStateException("Synthetic UI failure");
        try (var sentry = mockStatic(Sentry.class)) {
            assertThat(handler.handleAnyExceptionUi(exception, Mockito.mock(Model.class))).isEqualTo("reports/list");
            sentry.verify(() -> Sentry.captureException(eq(exception), any(ScopeCallback.class)));
            sentry.verifyNoMoreInteractions();
        }
    }

    @Test
    void doesNotCaptureExpectedUiFailure() {
        try (var sentry = mockStatic(Sentry.class)) {
            handler.handleAnyExceptionUi(new ReportIdNotFoundException("Synthetic missing report"), Mockito.mock(Model.class));
            sentry.verifyNoInteractions();
        }
    }

    @Test
    void handleAnyExceptionUi_addsErrorMessageAndReturnsView() {
        // given
        Exception ex = new Exception("Something went wrong");
        Model model = Mockito.mock(Model.class);

        // when
        String viewName = handler.handleAnyExceptionUi(ex, model);

        // then
        assertThat(viewName).isEqualTo("reports/list");
        verify(model).addAttribute("errorMessage", "Something went wrong");
    }
}
