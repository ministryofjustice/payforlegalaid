package uk.gov.laa.gpfd.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.InvalidDownloadFormatException;
import uk.gov.laa.gpfd.exception.OperationNotSupportedException;
import uk.gov.laa.gpfd.exception.ReportAccessException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.exception.ReportNotSupportedForDownloadException;
import uk.gov.laa.gpfd.exception.ReportOutputTypeNotFoundException;
import uk.gov.laa.gpfd.exception.TemplateResourceException;
import uk.gov.laa.gpfd.exception.TransferException;
import uk.gov.laa.gpfd.exception.UnableToGetAuthGroupException.AuthenticationIsNullException;
import uk.gov.laa.gpfd.exception.UnableToGetAuthGroupException.PrincipalIsNullException;
import uk.gov.laa.gpfd.exception.UnableToGetAuthGroupException.UnexpectedAuthClassException;

import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Stream.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
                new TemplateResourceException.TemplateResourceNotFoundException("Template file '%s' not found in resources for ID: %s"),
                "Template file '%s' not found in resources for ID: %s"
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
        "","Error: \n---\n|   |\n---"})
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

}
