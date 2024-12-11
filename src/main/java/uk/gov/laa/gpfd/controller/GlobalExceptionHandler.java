package uk.gov.laa.gpfd.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import uk.gov.laa.gpfd.exception.CsvStreamException;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.model.ReportsGet400Response;
import uk.gov.laa.gpfd.model.ReportsGet404Response;
import uk.gov.laa.gpfd.model.ReportsGet500Response;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.internalServerError;
import static org.springframework.http.ResponseEntity.notFound;

/**
 * Global exception handler for managing exceptions thrown by controllers.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_STRING = "Error: ";

    /**
     * Handles CsvStreamException and responds with an HTTP 500 Internal Server Error.
     *
     * @param e the CsvStreamException thrown when there is an issue in CSV streaming.
     * @return a {@link ResponseEntity} containing a {@link ReportsGet500Response} with error details.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CsvStreamException.class)
    public ResponseEntity<ReportsGet500Response> handleCsvStreamException(CsvStreamException e) {
        var response = new ReportsGet500Response() {{
            setError(e.getMessage());
        }};

        log.error("CsvStreamException Thrown: %s".formatted(response));

        return internalServerError().body(response);
    }

    /**
     * Handles DatabaseReadException and responds with an HTTP 500 Internal Server Error.
     *
     * @param e the DatabaseReadException thrown when there is a database read failure.
     * @return a {@link ResponseEntity} containing a {@link ReportsGet500Response} with error details.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DatabaseReadException.class)
    public ResponseEntity<ReportsGet500Response> handleDatabaseReadException(DatabaseReadException e) {
        var response = new ReportsGet500Response() {{
            setError(e.getMessage());
        }};

        log.error("DatabaseReadException Thrown: %s".formatted(response));
        log.error("DatabaseReadException stacktrace: %s".formatted(e.getStackTrace()));

        return internalServerError().body(response);
    }

    /**
     * Handles ReportIdNotFoundException and responds with an HTTP 404 Not Found.
     *
     * @param e the ReportIdNotFoundException thrown when a requested report ID is not found.
     * @return a {@link ResponseEntity} containing a {@link ReportsGet404Response} with error details.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ReportIdNotFoundException.class)
    public ResponseEntity<ReportsGet400Response> handleReportIdNotFoundException(ReportIdNotFoundException e) {
        var response = new ReportsGet404Response() {{
            setError(e.getMessage());
        }};

        log.error("ReportIdNotFoundException Thrown: %s".formatted(response));

        return notFound().build();
    }

    /**
     * Handles IndexOutOfBoundsException and responds with an HTTP 400 Bad Request.
     *
     * @param e the IndexOutOfBoundsException thrown when an index is accessed out of bounds.
     * @return a {@link ResponseEntity} containing a {@link ReportsGet400Response} with error details.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public ResponseEntity<ReportsGet400Response> handleIndexOutOfBoundsException(IndexOutOfBoundsException e) {
        var response = new ReportsGet400Response() {{
            setError(e.getMessage());
        }};

        log.error("IndexOutOfBoundsException Thrown: %s".formatted(response));

        return badRequest().body(response);
    }

    /**
     * Handles MethodArgumentTypeMismatchException and responds with an HTTP 400 Bad Request.
     *
     * @param e the MethodArgumentTypeMismatchException thrown when an argument type is mismatched.
     * @return a {@link ResponseEntity} containing a {@link ReportsGet400Response} with error details.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ReportsGet400Response> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        var message = ERROR_STRING + "Invalid input for parameter " + e.getName() + ". Expected a numeric value";
        var response = new ReportsGet400Response() {{
            setError(message);
        }};

        log.error("MethodArgumentTypeMismatchException Thrown: %s".formatted(response));

        return badRequest().body(response);
    }

}
