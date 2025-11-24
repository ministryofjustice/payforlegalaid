package uk.gov.laa.gpfd.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.InvalidDownloadFormatException;
import uk.gov.laa.gpfd.exception.OperationNotSupportedException;
import uk.gov.laa.gpfd.exception.ReportAccessException;
import uk.gov.laa.gpfd.exception.ReportGenerationException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.exception.ReportNotSupportedForDownloadException;
import uk.gov.laa.gpfd.exception.ReportOutputTypeNotFoundException;
import uk.gov.laa.gpfd.exception.ServiceUnavailableException;
import uk.gov.laa.gpfd.exception.TemplateResourceException;
import uk.gov.laa.gpfd.exception.UnableToGetAuthGroupException;
import uk.gov.laa.gpfd.model.GetReportDownloadById403Response;
import uk.gov.laa.gpfd.model.GetReportDownloadById501Response;
import uk.gov.laa.gpfd.model.ReportsGet400Response;
import uk.gov.laa.gpfd.model.ReportsGet404Response;
import uk.gov.laa.gpfd.model.ReportsGet500Response;

import java.sql.SQLSyntaxErrorException;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.internalServerError;
import static uk.gov.laa.gpfd.exception.TransferException.StreamException.ExcelStreamWriteException;

/**
 * Global exception handler for API controllers.
 * Catches unhandled exceptions, logs them, and returns
 * appropriate HTTP responses to the client.
 */
@Slf4j
@ControllerAdvice
@SuppressWarnings({"java:S1171", "java:S3599"}) //Disabling due to generated code
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final String ERROR_STRING = "Error: ";

    /**
     * Handles LocalTemplateReadException and responds with an HTTP 500 Internal Server Error.
     *
     * @param e the LocalTemplateReadException thrown when there is an issue reading a local template resource.
     * @return a {@link ResponseEntity} containing a {@link ReportsGet500Response} with error details.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            TemplateResourceException.TemplateNotFoundException.class,
            TemplateResourceException.LocalTemplateReadException.class,
            TemplateResourceException.ExcelTemplateCreationException.class,
            TemplateResourceException.TemplateDownloadException.class,
            TemplateResourceException.TemplateResourceNotFoundException.class
    })
    public ResponseEntity<ReportsGet500Response> handleTemplateResourceException(TemplateResourceException e) {
        var response = new ReportsGet500Response() {{
            setError(e.getMessage());
        }};

        log.error("ExcelTemplateCreationException Thrown: {}", response.getError());
        if (e.getCause() != null) {
            log.error("Caused by: {}", e.getCause().getMessage());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handles ReportGenerationException and responds with an HTTP 500 Internal Server Error.
     *
     * @param e the ReportGenerationException thrown when there is an issue while creating xls report
     * @return a {@link ResponseEntity} containing a {@link ReportsGet500Response} with error details.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            ReportGenerationException.InvalidWorkbookTypeException.class,
            ReportGenerationException.PivotTableCopyException.class,
            ReportGenerationException.PivotTableCreationException.class,
            ReportGenerationException.SheetCopyException.class,
            ReportGenerationException.SheetNotFoundException.class
    })
    public ResponseEntity<ReportsGet500Response> handleReportGenerationException(ReportGenerationException e) {
        var response = new ReportsGet500Response() {{
            setError(e.getMessage());
        }};

        log.error("ReportGenerationException Thrown: {}", response.getError());
        if (e.getCause() != null) {
            log.error("Caused by: {}", e.getCause().getMessage());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handles ExcelStreamWriteException and responds with an HTTP 500 Internal Server Error.
     *
     * @param e the ExcelStreamWriteException thrown when there is an issue writing an Excel file to a stream.
     * @return a {@link ResponseEntity} containing a {@link ReportsGet500Response} with error details.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ExcelStreamWriteException.class)
    public ResponseEntity<ReportsGet500Response> handleExcelStreamWriteException(ExcelStreamWriteException e) {
        var response = new ReportsGet500Response() {{
            setError(e.getMessage());
        }};

        log.error("ExcelStreamWriteException Thrown: {}", response.getError());

        return internalServerError().body(response);
    }

    /**
     * Handles DatabaseReadException and responds with an HTTP 500 Internal Server Error.
     *
     * @param e the DatabaseReadException thrown when there is a database read failure.
     * @return a {@link ResponseEntity} containing a {@link ReportsGet500Response} with error details.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {
            DatabaseReadException.class,
            DataAccessException.class,
            SQLSyntaxErrorException.class
    }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReportsGet500Response> handleDatabaseReadException(Exception e) {
        var response = new ReportsGet500Response() {{
            setError(e.getMessage());
        }};

        log.error("DatabaseReadException Thrown: %s".formatted(response));
        log.error("DatabaseReadException stacktrace: %s".formatted((Object) e.getStackTrace()));

        return internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    /**
     * Handles ReportOutputTypeNotFoundException and responds with an HTTP 500 Internal Server Error.
     *
     * @param e the ReportOutputTypeNotFoundException thrown when an unknown report output type is encountered.
     * @return a {@link ResponseEntity} containing a {@link ReportsGet500Response} with error details.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ReportOutputTypeNotFoundException.class)
    public ResponseEntity<ReportsGet500Response> handleReportOutputTypeNotFoundException(ReportOutputTypeNotFoundException e) {

        var response = new ReportsGet500Response() {{
            setError(e.getMessage());
        }};

        log.error("ReportOutputTypeNotFoundException Thrown: %s".formatted(response));
        log.error("ReportOutputTypeNotFoundException stacktrace: %s".formatted((Object) e.getStackTrace()));

        return internalServerError().body(response);
    }

    /**
     * Handles ReportIdNotFoundException and responds with an HTTP 400 Bad Request.
     *
     * @param e the ReportIdNotFoundException thrown when a requested report ID is not found.
     * @return a {@link ResponseEntity} containing a {@link ReportsGet400Response} with error details.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ReportIdNotFoundException.class)
    public ResponseEntity<ReportsGet404Response> handleReportIdNotFoundException(ReportIdNotFoundException e) {
        var response = new ReportsGet404Response() {{
            setError(e.getMessage());
        }};

        log.error("ReportIdNotFoundException Thrown: %s".formatted(response));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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

    /**
     * Handles {@link AwsServiceException} and its subtypes, and responds with an HTTP 500 Internal Server Error.
     *
     * @param e the exception thrown when there is an issue connecting to S3.
     * @return a {@link ResponseEntity} containing a {@link ReportsGet500Response} with error details.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(AwsServiceException.class)
    public ResponseEntity<ReportsGet500Response> handleAWSErrors(AwsServiceException e) {
        var message = ERROR_STRING + "Failed to prepare report for download";
        var errorResponse = new ReportsGet500Response();
        errorResponse.setError(message);

        // Ensure log has specific AWS exception class name in, such as NoSuchKeyException.
        log.error("AwsServiceException ({}) Thrown: {}", e.getClass().getSimpleName(), e.awsErrorDetails().toString());

        return internalServerError().body(errorResponse);
    }

    /**
     * Handles {@link ServiceUnavailableException} and responds with an HTTP 500 Internal Server Error.
     *
     * @param e the exception thrown when the user is acting outside of service active hours.
     * @return a {@link ResponseEntity} containing a {@link ReportsGet500Response} with error details.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ReportsGet500Response> handleServiceUnavailable(ServiceUnavailableException e) {
        var errorResponse = new ReportsGet500Response();
        errorResponse.setError(e.getMessage());

        log.error("ServiceUnavailableException Thrown: {}", e.getMessage());

        return internalServerError().body(errorResponse);
    }

    /**
     * Handles {@link OperationNotSupportedException} and responds with an HTTP 501 Not Implemented.
     *
     * @param e the exception thrown when the user is on a system that doesn't support the endpoint.
     * @return a {@link ResponseEntity} containing a {@link GetReportDownloadById501Response} with error details.
     */
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(OperationNotSupportedException.class)
    public ResponseEntity<GetReportDownloadById501Response> handleNotSupportedException(OperationNotSupportedException e) {
        var errorResponse = new GetReportDownloadById501Response();
        errorResponse.setError(e.getMessage());

        log.error("OperationNotSupportedException Thrown: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(errorResponse);
    }

    /**
     * Handles {@link InvalidDownloadFormatException} and responds with an HTTP 400 Bad Request.
     *
     * @param e the exception thrown when the file being downloaded would not be a csv
     * @return a {@link ResponseEntity} containing a {@link ReportsGet400Response} with error details.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidDownloadFormatException.class)
    public ResponseEntity<ReportsGet400Response> handleInvalidDownloadFormatException(InvalidDownloadFormatException e) {
        var errorResponse = new ReportsGet400Response();
        errorResponse.setError(e.getErrorMessage());

        log.error("InvalidDownloadFormatException Thrown: Report {} has file {} which is not a csv file", e.getReportId(), e.getFileName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * Handles {@link ReportNotSupportedForDownloadException} and responds with an HTTP 400 Bad Request.
     *
     * @param e the exception thrown when the report can't be downloaded via this endpoint
     * @return a {@link ResponseEntity} containing a {@link ReportsGet400Response} with error details.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ReportNotSupportedForDownloadException.class)
    public ResponseEntity<ReportsGet400Response> handleReportNotSupportedForDownloadException(ReportNotSupportedForDownloadException e) {
        var errorResponse = new ReportsGet400Response();
        errorResponse.setError(e.getErrorMessage());

        log.error("ReportNotSupportedForDownloadException Thrown: Report {} is not supported on the '/report/{id}/file' endpoint", e.getReportId());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * Handles {@link UnableToGetAuthGroupException} and responds with an HTTP 500 Internal Server Error.
     *
     * @param e the exception thrown when we can't extract a group from the user's auth token
     * @return a {@link ResponseEntity} containing a {@link ReportsGet500Response} with error details.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(UnableToGetAuthGroupException.class)
    public ResponseEntity<ReportsGet500Response> handleUnexpectedAuthTypeException(UnableToGetAuthGroupException e) {
        var errorResponse = new ReportsGet500Response();
        errorResponse.setError("Authentication response error.");

        log.error("UnexpectedAuthTypeException Thrown: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    /**
     * Handles {@link ReportAccessException} and responds with an HTTP 403 Forbidden.
     *
     * @param e the exception thrown when we can't extract a group from the user's auth token
     * @return a {@link ResponseEntity} containing a {@link GetReportDownloadById403Response} with error details.
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ReportAccessException.class)
    public ResponseEntity<GetReportDownloadById403Response> handleReportAccessException(ReportAccessException e) {
        var errorResponse = new GetReportDownloadById403Response();
        errorResponse.setError(e.getErrorMessage());

        log.error("ReportAccessException Thrown: User tried to access report {} but lacks the relevant permission(s)", e.getReportId());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(errorResponse);
    }

}
