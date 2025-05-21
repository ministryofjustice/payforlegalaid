package uk.gov.laa.gpfd.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.exception.ReportOutputTypeNotFoundException;
import uk.gov.laa.gpfd.exception.TemplateResourceException;
import uk.gov.laa.gpfd.exception.TransferException;

import java.util.stream.Stream;

import static java.util.stream.Stream.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@SuppressWarnings("DataFlowIssue")
class GlobalExceptionHandlerTest {

    private static final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void shouldHandleDatabaseReadExceptionWithLongMessage() {
        // Given
        var longMessage = "Database error occurred while processing request: " + "A".repeat(1000);
        var exception = new DatabaseReadException.DatabaseFetchException(longMessage);

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

    @Test
    void shouldHandleReportIdNotFoundExceptionWithEmptyMessage() {
        // Given
        var exception = new ReportIdNotFoundException("");

        // When
        var response = globalExceptionHandler.handleReportIdNotFoundException(exception);

        // Then
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals("", response.getBody().getError());
    }

    @Test
    void shouldHandleDatabaseReadExceptionWithWhitespaceMessage() {
        // Given
        var exception = new DatabaseReadException.DatabaseFetchException("   ");

        // When
        var response = globalExceptionHandler.handleDatabaseReadException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("   ", response.getBody().getError());
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
                        new DatabaseReadException.DatabaseFetchException("Error reading from DB: permissions problem"),
                        "Error reading from DB: permissions problem"
                ),
                Arguments.of(
                        new DatabaseReadException.MappingException("Error mapping Report data"),
                        "Error mapping Report data"
                ),
                Arguments.of(
                        new DatabaseReadException.SqlFormatException("SQL format invalid for report FinanceStuff (id 123ab-432fa-32423-das24)"),
                        "SQL format invalid for report FinanceStuff (id 123ab-432fa-32423-das24)"
                )
        );
    }


    @Test
    void shouldHandleReportIdNotFoundException() {
        // Given
        var exception = new ReportIdNotFoundException("Report ID not found");

        // When
        var response = globalExceptionHandler.handleReportIdNotFoundException(exception);

        // Then
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals("Report ID not found", response.getBody().getError());
    }

    @Test
    void shouldHandleIndexOutOfBoundsException() {
        // Given
        var exception = new IndexOutOfBoundsException("Index out of bounds");

        // When
        var response = globalExceptionHandler.handleIndexOutOfBoundsException(exception);

        // Then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("Index out of bounds", response.getBody().getError());
    }

    @Test
    void shouldHandleIndexOutOfBoundsExceptionWithCustomMessage() {
        // Given
        var exception = new IndexOutOfBoundsException("Custom error message");

        // When
        var response = globalExceptionHandler.handleIndexOutOfBoundsException(exception);

        // Then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("Custom error message", response.getBody().getError());
    }

    @Test
    void shouldHandleDatabaseReadExceptionWithSpecialCharacters() {
        // Given
        var exception = new DatabaseReadException.DatabaseFetchException("Error! @#$%^&*()");

        // When
        var response = globalExceptionHandler.handleDatabaseReadException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error! @#$%^&*()", response.getBody().getError());
    }

    @Test
    void shouldHandleReportIdNotFoundExceptionWithLongMessage() {
        // Given
        var longMessage = "Report ID not found: " + "X".repeat(1000);
        var exception = new ReportIdNotFoundException(longMessage);

        // When
        var response = globalExceptionHandler.handleReportIdNotFoundException(exception);

        // Then
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals(longMessage, response.getBody().getError());
    }

    @Test
    void shouldHandleIndexOutOfBoundsExceptionWithDefaultMessage() {
        // Given
        var exception = new IndexOutOfBoundsException();

        // When
        var response = globalExceptionHandler.handleIndexOutOfBoundsException(exception);

        // Then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody().getError());
    }

    @Test
    void shouldHandleIndexOutOfBoundsExceptionWithZeroIndex() {
        // Given
        var exception = new IndexOutOfBoundsException("Index 0 is out of bounds");

        // When
        var response = globalExceptionHandler.handleIndexOutOfBoundsException(exception);

        // Then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("Index 0 is out of bounds", response.getBody().getError());
    }

    @Test
    void shouldHandleDatabaseReadExceptionWithNewlineCharacters() {
        // Given
        var message = "Database error\nDetails: connection failed.";
        var exception = new DatabaseReadException.DatabaseFetchException(message);

        // When
        var response = globalExceptionHandler.handleDatabaseReadException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(message, response.getBody().getError());
    }

    @Test
    void shouldHandleReportIdNotFoundExceptionWithExtraSpacesInMessage() {
        // Given
        var exception = new ReportIdNotFoundException("Report   ID   not   found");

        // When
        var response = globalExceptionHandler.handleReportIdNotFoundException(exception);

        // Then
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals("Report   ID   not   found", response.getBody().getError());
    }

    @Test
    void shouldHandleIndexOutOfBoundsExceptionWithLargeNumericIndex() {
        // Given
        var exception = new IndexOutOfBoundsException("Index 1000000 is out of bounds");

        // When
        var response = globalExceptionHandler.handleIndexOutOfBoundsException(exception);

        // Then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("Index 1000000 is out of bounds", response.getBody().getError());
    }

    @Test
    void shouldHandleDatabaseReadExceptionWithJsonMessage() {
        // Given
        var jsonMessage = "{\"error\":\"database failure\"}";
        var exception = new DatabaseReadException.DatabaseFetchException(jsonMessage);

        // When
        var response = globalExceptionHandler.handleDatabaseReadException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(jsonMessage, response.getBody().getError());
    }

    @Test
    void shouldHandleReportIdNotFoundExceptionWithForeignLanguageMessage() {
        // Given
        var exception = new ReportIdNotFoundException("Informe no encontrado");

        // When
        var response = globalExceptionHandler.handleReportIdNotFoundException(exception);

        // Then
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals("Informe no encontrado", response.getBody().getError());
    }

    @Test
    void shouldHandleIndexOutOfBoundsExceptionWithNegativeIndex() {
        // Given
        var exception = new IndexOutOfBoundsException("Index -5 is out of bounds");

        // When
        var response = globalExceptionHandler.handleIndexOutOfBoundsException(exception);

        // Then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("Index -5 is out of bounds", response.getBody().getError());
    }


    @Test
    void shouldHandleDatabaseReadExceptionWithUtf16Message() {
        // Given
        var utf16Message = "数据库错误";
        var exception = new DatabaseReadException.DatabaseFetchException(utf16Message);

        // When
        var response = globalExceptionHandler.handleDatabaseReadException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(utf16Message, response.getBody().getError());
    }

    @Test
    void shouldHandleReportIdNotFoundExceptionWithWelshMessage() {
        // Given
        var exception = new ReportIdNotFoundException("Ni chanfuwyd adnabodwyr adroddiad");

        // When
        var response = globalExceptionHandler.handleReportIdNotFoundException(exception);

        // Then
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals("Ni chanfuwyd adnabodwyr adroddiad", response.getBody().getError());
    }

    @Test
    void shouldHandleIndexOutOfBoundsExceptionWithIsoDateMessage() {
        // Given
        var exception = new IndexOutOfBoundsException("Index out of bounds on 2024-11-24");

        // When
        var response = globalExceptionHandler.handleIndexOutOfBoundsException(exception);

        // Then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("Index out of bounds on 2024-11-24", response.getBody().getError());
    }



    @Test
    void shouldHandleDatabaseReadExceptionWithSingleNewlineMessage() {
        // Given
        var exception = new DatabaseReadException.DatabaseFetchException("\n");

        // When
        var response = globalExceptionHandler.handleDatabaseReadException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("\n", response.getBody().getError());
    }

    @Test
    void shouldHandleReportIdNotFoundExceptionWithExcessiveNewlines() {
        // Given
        var exception = new ReportIdNotFoundException("\n\n\n");

        // When
        var response = globalExceptionHandler.handleReportIdNotFoundException(exception);

        // Then
        assertEquals(NOT_FOUND, response.getStatusCode());
        assertEquals("\n\n\n", response.getBody().getError());
    }

    @Test
    void shouldHandleIndexOutOfBoundsExceptionWithAsciiArtMessage() {
        // Given
        var exception = new IndexOutOfBoundsException("Error: \n---\n|   |\n---");

        // When
        var response = globalExceptionHandler.handleIndexOutOfBoundsException(exception);

        // Then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: \n---\n|   |\n---", response.getBody().getError());
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

}
