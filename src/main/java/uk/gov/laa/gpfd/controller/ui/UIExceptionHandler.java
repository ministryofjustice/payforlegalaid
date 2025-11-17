package uk.gov.laa.gpfd.controller.ui;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice(assignableTypes = {ReportsViewController.class})
public class UIExceptionHandler {
    /**
     * Handles any unhandled exception in UI controllers.
     * <p>Adds an {@code errorMessage} to the model and returns
     * the {@code reports/list} view so Thymeleaf can display
     * an exception to user.</p>
     *
     * @param e     the thrown exception
     * @param model the model to pass attributes to the view
     * @return      the Thymeleaf view name "reports/list"
     */
    @ExceptionHandler(Exception.class)
    public String handleAnyExceptionUi(Exception e, Model model) {
        log.error("Unhandled exception handled (UI): {}", e.getMessage());
        model.addAttribute("errorMessage", e.getMessage());
        return "reports/list";
    }
}