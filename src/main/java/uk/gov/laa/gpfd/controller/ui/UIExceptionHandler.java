package uk.gov.laa.gpfd.controller.ui;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Exception handler for ReportsViewController.
 * Catches any exception thrown by UI, puts the message in the model,
 * and returns the reports list view.
 */
@Slf4j
@ControllerAdvice(assignableTypes = ReportsViewController.class)
@Order(1)
public class UIExceptionHandler {
    /**
     * Handles any unhandled exception in UI controllers.
     * Adds an errorMessage to the model and returns
     * the {reports/list} view so Thymeleaf can display
     * an exception to user.
     *
     * @param e     the thrown exception
     * @param model the model to pass attributes to the view
     * @return      the Thymeleaf view name "reports/list"
     */
    @ExceptionHandler(Exception.class)
    public String handleAnyExceptionUi(Exception e, Model model) {
        log.error("{} handled (UI): {}", e.getClass().getName(), e.getMessage());
        model.addAttribute("errorMessage", e.getMessage());
        return "reports/list";
    }
}