package uk.gov.laa.gpfd.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import tools.jackson.core.exc.JacksonIOException;
import uk.gov.laa.gpfd.exception.CsvGenerationException.MetadataInvalidException;
import uk.gov.laa.gpfd.exception.CsvGenerationException.WritingToCsvException;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.DatabaseWriteException;
import uk.gov.laa.gpfd.exception.FileDownloadException.InvalidDownloadFormatException;
import uk.gov.laa.gpfd.exception.FileDownloadException.ReportNotSupportedForDownloadException;
import uk.gov.laa.gpfd.exception.FileDownloadException.S3BucketHasNoCopiesOfReportException;
import uk.gov.laa.gpfd.exception.InvalidReportFormatException;
import uk.gov.laa.gpfd.exception.OperationNotSupportedException;
import uk.gov.laa.gpfd.exception.ReportAccessException;
import uk.gov.laa.gpfd.exception.ReportGenerationException;
import uk.gov.laa.gpfd.exception.ReportGenerationException.InvalidWorkbookTypeException;
import uk.gov.laa.gpfd.exception.ReportGenerationException.PivotTableCopyException;
import uk.gov.laa.gpfd.exception.ReportGenerationException.PivotTableCreationException;
import uk.gov.laa.gpfd.exception.ReportGenerationException.SheetCopyException;
import uk.gov.laa.gpfd.exception.ReportGenerationException.SheetNotFoundException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.exception.ReportOutputTypeNotFoundException;
import uk.gov.laa.gpfd.exception.StreamErrorException;
import uk.gov.laa.gpfd.exception.TemplateResourceException;
import uk.gov.laa.gpfd.exception.TransferException;
import uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException;
import uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException.AuthenticationIsNullException;
import uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException.NoAttributesOnTokenException;
import uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException.NoOidSetOnTokenException;
import uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException.NoRolesException;
import uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException.NoRolesInAttributeException;
import uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException.PrincipalIsNullException;
import uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException.UnexpectedAuthClassException;
import uk.gov.laa.gpfd.exception.FileDownloadException.S3BucketHasNoCopiesOfReportException;
import uk.gov.laa.gpfd.utils.RequestLogUtils;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Stream.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;
import static uk.gov.laa.gpfd.exception.DatabaseReadException.DatabaseFetchException;
import static uk.gov.laa.gpfd.exception.DatabaseReadException.MappingException;
import static uk.gov.laa.gpfd.exception.DatabaseReadException.SqlFormatException;

@SuppressWarnings("DataFlowIssue")
class GlobalExceptionHandlerTest {

    private static final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
    private ListAppender<ILoggingEvent> appender;

    @AfterEach
    void tearDown() {
        MDC.clear();
        if (appender != null) {
            appender.stop();
        }
    }

    private ListAppender<ILoggingEvent> createListAppender() {
        Logger logger = (Logger) LoggerFactory.getLogger(GlobalExceptionHandler.class);
        LoggerContext loggerContext = logger.getLoggerContext();

        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.setContext(loggerContext);
        listAppender.start();

        logger.addAppender(listAppender);
        logger.setLevel(Level.ERROR);
        logger.setAdditive(false);

        return listAppender;
    }

    private Map<String, String> extractKeyValuePairs(ILoggingEvent event) {
        return event.getKeyValuePairs().stream()
                .collect(Collectors.toMap(kv -> kv.key, kv -> String.valueOf(kv.value)));
    }

    @Test
    void shouldHandleDatabaseFetchExceptionWithLongMessage() {
        // Given
        var longMessage = "Database error occurred while processing request: " + "A".repeat(1000);
        var exception = new DatabaseFetchException(longMessage);

        // When
        var response = globalExceptionHandler.handleDatabaseReadException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(longMessage, response.getBody().getError());
    }

    @Test
    void shouldHandleIndexOutOfBoundsExceptionWithNullMessage() {
        // Given
        var exception = new IndexOutOfBoundsException(null);

        // When
        var response = globalExceptionHandler.handleIndexOutOfBoundsException(exception);

        // Then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody().getError());
    }

    @ParameterizedTest
    @ValueSource(strings = {"",
            "\n\n\n",
            "Report ID not found",
            "Report   ID   not   found",
            "Informe no encontrado",
            "Ni chanfuwyd adnabodwyr adroddiad"})
    void shouldHandleReportIdNotFoundException(String messageToTest) {
        // Given
        var exception = new ReportIdNotFoundException(messageToTest);

        // When
        var response = globalExceptionHandler.handleReportIdNotFoundException(exception);

        // Then
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals(messageToTest, response.getBody().getError());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "   ", //Only whitespace
            "Error! @#$%^&*()", //Special chars
            "Database error\nDetails: connection failed.", //Has new lines
            "数据库错误", //Foreign characters
            "{\"error\":\"database failure\"}", //JSON message
            "\n"
    })
    void shouldHandleDatabaseFetchExceptionWithDifferentEdgeCases(String messageToTest) {
        // Given
        var exception = new DatabaseFetchException(messageToTest);

        // When
        var response = globalExceptionHandler.handleDatabaseReadException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(messageToTest, response.getBody().getError());
    }

    @ParameterizedTest
    @MethodSource("templateExceptionProvider")
    void shouldHandleTemplateResourceExceptions(TemplateResourceException exception, String expectedErrorMessage) {
        // When
        var response = globalExceptionHandler.handleTemplateResourceException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(expectedErrorMessage, response.getBody().getError());
    }

    private static Stream<Arguments> templateExceptionProvider() {
        return of(Arguments.of(
                new TemplateResourceException.TemplateNotFoundException("Template not found in resources for ID: 1"),
                "Template not found in resources for ID: 1"
        ), Arguments.of(
                new TemplateResourceException.LocalTemplateReadException("Could not find template"),
                "Could not find template"
        ), Arguments.of(
                new TemplateResourceException.ExcelTemplateCreationException("Meh, doesnt work on my machine!", new RuntimeException()),
                "Meh, doesnt work on my machine!"
        ), Arguments.of(
                new TemplateResourceException.ExcelTemplateCreationException(new RuntimeException(), "Meh %s, doesnt work on my machine! %s", "arg1", "arg2"),
                "Meh arg1, doesnt work on my machine! arg2"
        ), Arguments.of(
                new TemplateResourceException.TemplateResourceNotFoundException("Template file '%s' not found in resources for ID: %s"),
                "Template file '%s' not found in resources for ID: %s"
        ), Arguments.of(
                new TemplateResourceException.TemplateDownloadException("Template download failed"),
                "Template download failed"
        ));
    }

    @Test
    void shouldHandleExcelStreamWriteException() {
        // Given
        var exception = new TransferException.StreamException.ExcelStreamWriteException("CSV Stream Error", new RuntimeException());

        // When
        var response = globalExceptionHandler.handleExcelStreamWriteException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("CSV Stream Error", response.getBody().getError());
    }

    @ParameterizedTest
    @MethodSource("databaseExceptionProvider")
    void shouldHandleDatabaseReadExceptions(DatabaseReadException exception, String expectedErrorMessage) {
        // When
        var response = globalExceptionHandler.handleDatabaseReadException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(expectedErrorMessage, response.getBody().getError());
    }

    private static Stream<Arguments> databaseExceptionProvider() {
        return of(Arguments.of(
                        new DatabaseFetchException("Error reading from DB: permissions problem"),
                        "Error reading from DB: permissions problem"
                ),
                Arguments.of(
                        new DatabaseFetchException("Error reading from DB: permissions problem", new RuntimeException("error")),
                        "Error reading from DB: permissions problem"
                ),
                Arguments.of(
                        new MappingException("Error mapping Report data"),
                        "Error mapping Report data"
                ),
                Arguments.of(
                        new SqlFormatException("SQL format invalid for report FinanceStuff (id 123ab-432fa-32423-das24)"),
                        "SQL format invalid for report FinanceStuff (id 123ab-432fa-32423-das24)"
                )
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"Index out of bounds",
            "Custom error message",
            "", "Error: \n---\n|   |\n---"})
    void shouldHandleIndexOutOfBoundsException(String messageToTest) {
        // Given
        var exception = new IndexOutOfBoundsException(messageToTest);

        // When
        var response = globalExceptionHandler.handleIndexOutOfBoundsException(exception);

        // Then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals(messageToTest, response.getBody().getError());
    }

    @Test
    void shouldHandleReportOutputTypeNotFoundExceptionWithExpectedErrorMessage() {
        // Given
        var exception = new ReportOutputTypeNotFoundException("Invalid file extension: xyz");

        // When
        var response = globalExceptionHandler.handleReportOutputTypeNotFoundException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Invalid file extension: xyz", response.getBody().getError());
    }

    @Test
    void shouldHandleAWSServiceExceptionByThrowing500() {
        var exception = NoSuchKeyException.builder().message("File don't exist and some maybe sensitive stuff about addresses here")
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("312").errorMessage("uh oh").build())
                .build();

        var response = globalExceptionHandler.handleAWSErrors(exception);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error: Failed to prepare report for download", response.getBody().getError());
    }

    @Test
    void shouldHandleOperationNotSupportedExceptions() {
        var exception = new OperationNotSupportedException("/reports/id/file");

        var response = globalExceptionHandler.handleNotSupportedException(exception);

        assertEquals(NOT_IMPLEMENTED, response.getStatusCode());
        assertEquals("Operation /reports/id/file is not supported on this instance", response.getBody().getError());
    }

    @Test
    void shouldHandleInvalidDownloadFormatException() {
        var reportId = UUID.randomUUID();
        var exception = new InvalidDownloadFormatException("blah.docx", reportId);

        var response = globalExceptionHandler.handleInvalidDownloadFormatException(exception);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("Unable to download file for report with ID: " + reportId, response.getBody().getError());
    }

    @Test
    void shouldHandleReportNotSupportedForDownloadException() {
        var reportId = UUID.randomUUID();
        var exception = new ReportNotSupportedForDownloadException(reportId);

        var response = globalExceptionHandler.handleReportNotSupportedForDownloadException(exception);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("Report " + reportId + " is not valid for file retrieval.", response.getBody().getError());
    }

    @Test
    void shouldHandleUnexpectedAuthClassException() {
        var exception = new UnexpectedAuthClassException("spring.User");

        var response = globalExceptionHandler.handleUnexpectedAuthTypeException(exception);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Authentication response error.", response.getBody().getError());
    }

    @Test
    void shouldHandleAuthenticationIsNullException() {
        var exception = new AuthenticationIsNullException();

        var response = globalExceptionHandler.handleUnexpectedAuthTypeException(exception);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Authentication response error.", response.getBody().getError());
    }

    @Test
    void shouldHandlePrincipalIsNullException() {
        var exception = new PrincipalIsNullException();

        var response = globalExceptionHandler.handleUnexpectedAuthTypeException(exception);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Authentication response error.", response.getBody().getError());
    }

    @Test
    void shouldHandleReportAccessException() {
        var reportId = UUID.randomUUID();
        var exception = new ReportAccessException(reportId);

        var response = globalExceptionHandler.handleReportAccessException(exception);

        assertEquals(FORBIDDEN, response.getStatusCode());
        assertEquals("You cannot access report with ID: " + reportId,
                response.getBody().getError());
    }

    @Test
    void shouldHandleMetadataInvalidException() {
        var exception = new MetadataInvalidException("Metadata is null");
        var response = globalExceptionHandler.handleCsvGenerationException(exception);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Metadata is null",
                response.getBody().getError());
    }

    @Test
    void shouldHandleWritingToCsvException() {
        var source = JacksonIOException.construct(new IOException("Can't write to file"));
        var exception = new WritingToCsvException("File creation error", source);
        var response = globalExceptionHandler.handleCsvGenerationException(exception);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("File creation error",
                response.getBody().getError());
    }

    @Test
    void shouldHandleS3BucketHasNoCopiesOfReportException() {
        var reportId = UUID.randomUUID();
        var exception = new S3BucketHasNoCopiesOfReportException(reportId, "reports/folder/filename");

        var response = globalExceptionHandler.handleS3BucketHasNoCopiesOfReportException(exception);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to download report with id " + reportId + ".", response.getBody().getError());
    }

    @Test
    void shouldHandleInvalidReportFormatException() {
        var reportId = UUID.randomUUID();
        var exception = new InvalidReportFormatException(reportId, "XLSX", "CSV");

        var response = globalExceptionHandler.handleInvalidReportFormatException(exception);

        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("Report " + reportId + " is not valid for XLSX retrieval. This report is in CSV format.",
                response.getBody().getError());
    }

    @Test
    void shouldHandleDatabaseWriteException() {
        var exception = new DatabaseWriteException("Error writing to db :(");

        var response = globalExceptionHandler.handleDatabaseWriteException(exception);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error writing to db :(",
                response.getBody().getError());
    }


    @ParameterizedTest
    @MethodSource("unableToParseAuthDetailsProvider")
    void shouldHandleUnableToParseAuthDetailsExceptions(UnableToParseAuthDetailsException exception) {
        var response = globalExceptionHandler.handleUnexpectedAuthTypeException(exception);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Authentication response error.", response.getBody().getError());
    }

    private static Stream<UnableToParseAuthDetailsException> unableToParseAuthDetailsProvider() {
        return of(
                new AuthenticationIsNullException(),
                new PrincipalIsNullException(),
                new UnexpectedAuthClassException("UserJwt"),
                new NoOidSetOnTokenException(),
                new NoRolesException(),
                new NoRolesInAttributeException(),
                new NoAttributesOnTokenException()
        );
    }

    @Test
    void shouldHandleStreamErrorException() {
        var reportId = UUID.randomUUID();
        var exception = new StreamErrorException("Error writing to db :(", reportId);

        var response = globalExceptionHandler.handleStreamErrorException(exception);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Report streaming failure",
                response.getBody().getError());
    }

    @ParameterizedTest
    @MethodSource("reportGenerationExceptionProvider")
    void shouldHandleReportGenerationExceptionProvider(ReportGenerationException exception, String expectedMessage) {
        var response = globalExceptionHandler.handleReportGenerationException(exception);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().getError());
    }

    private static Stream<Arguments> reportGenerationExceptionProvider() {
        return of(
                Arguments.of(new InvalidWorkbookTypeException("oh no"), "oh no"),
                Arguments.of(new SheetNotFoundException("uh oh"), "uh oh"),
                Arguments.of(new SheetCopyException("errorin'", new RuntimeException("error")), "errorin'"),
                Arguments.of(new PivotTableCreationException("oops"), "oops"),
                Arguments.of(new PivotTableCreationException("excel problems", new RuntimeException("error")), "excel problems"),
                Arguments.of(new PivotTableCopyException("WORKBOOK_A", "no copy"), "Failed to copy pivot table in sheet 'WORKBOOK_A': no copy"),
                Arguments.of(new PivotTableCopyException("WORKBOOK_B", "boo", new RuntimeException("error")), "Failed to copy pivot table in sheet 'WORKBOOK_B': boo")
        );
    }

}
    @Test
    void shouldLogAwsErrorWithCorrectStructure() {
        MDC.put(RequestLogUtils.REQUEST_ID, "test-request-id");
        MDC.put(RequestLogUtils.TRACE_ID, "test-trace-id");
        MDC.put(RequestLogUtils.USER_ID, "test-user-id");

        var exception = NoSuchKeyException.builder()
                .message("File don't exist")
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("312").errorMessage("uh oh").build())
                .build();

        appender = createListAppender();
        globalExceptionHandler.handleAWSErrors(exception);

        assertFalse(appender.list.isEmpty(), "Expected at least one log event from handleAWSErrors");

        ILoggingEvent loggingEvent = appender.list.get(0);
        Map<String, String> keyValuePairs = extractKeyValuePairs(loggingEvent);

        assertEquals("s3.download.failure", keyValuePairs.get(RequestLogUtils.EVENT_ACTION));
        assertEquals("failure", keyValuePairs.get(RequestLogUtils.EVENT_OUTCOME));
    }

    @Test
    void shouldLogReportAccessExceptionWithCorrectStructure() {
        MDC.put(RequestLogUtils.REQUEST_ID, "test-request-id");
        MDC.put(RequestLogUtils.TRACE_ID, "test-trace-id");
        MDC.put(RequestLogUtils.USER_ID, "test-user-id");

        var reportId = UUID.randomUUID();
        var exception = new ReportAccessException(reportId);

        appender = createListAppender();
        globalExceptionHandler.handleReportAccessException(exception);

        assertFalse(appender.list.isEmpty(), "Expected at least one log event from handleReportAccessException");

        ILoggingEvent loggingEvent = appender.list.get(0);
        Map<String, String> keyValuePairs = extractKeyValuePairs(loggingEvent);

        assertEquals("authorization.denied", keyValuePairs.get(RequestLogUtils.EVENT_ACTION));
        assertEquals("failure", keyValuePairs.get(RequestLogUtils.EVENT_OUTCOME));
    }

    @Test
    void shouldProduceExactlyOneLogEventPerHandlerCall() {
        MDC.put(RequestLogUtils.REQUEST_ID, "test-request-id");
        MDC.put(RequestLogUtils.TRACE_ID, "test-trace-id");

        var exception = NoSuchKeyException.builder()
                .message("File don't exist")
                .awsErrorDetails(AwsErrorDetails.builder().errorCode("312").errorMessage("uh oh").build())
                .build();

        appender = createListAppender();
        globalExceptionHandler.handleAWSErrors(exception);

        assertEquals(1, appender.list.size(),
                "Expected exactly one log event per handler call — duplicate MDC keys or repeated logging would produce more");
    }
}