package uk.gov.laa.gpfd.controller.ui;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class UIExceptionHandlerTest {

    private final UIExceptionHandler handler = new UIExceptionHandler();

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
