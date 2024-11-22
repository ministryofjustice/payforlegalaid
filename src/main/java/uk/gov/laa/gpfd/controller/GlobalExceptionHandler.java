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
import uk.gov.laa.gpfd.model.ReportsGet500Response;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.internalServerError;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_STRING = "Error: ";

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CsvStreamException.class)
    public ResponseEntity<ReportsGet500Response> handleCsvStreamException(CsvStreamException e) {
        var response = new ReportsGet500Response() {{
            setError(e.getMessage());
        }};

        log.error("CsvStreamException Thrown: %s".formatted(response));

        return internalServerError().body(response);
    }

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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ReportIdNotFoundException.class)
    public ResponseEntity<ReportsGet400Response> handleReportIdNotFoundException(ReportIdNotFoundException e) {
        var response = new ReportsGet400Response() {{
            setError(e.getMessage());
        }};

        log.error("ReportIdNotFoundException Thrown: %s".formatted(response));

        return badRequest().body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public ResponseEntity<ReportsGet400Response> handleIndexOutOfBoundsException(IndexOutOfBoundsException e) {
        var response = new ReportsGet400Response() {{
            setError(e.getMessage());
        }};

        log.error("IndexOutOfBoundsException Thrown: %s".formatted(response));

        return badRequest().body(response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ReportsGet400Response> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = ERROR_STRING + "Invalid input for parameter " + e.getName() + ". Expected a numeric value";
        var response = new ReportsGet400Response() {{
            setError(message);
        }};

        log.error("MethodArgumentTypeMismatchException Thrown: %s".formatted(response));

        return badRequest().body(response);
    }

}
