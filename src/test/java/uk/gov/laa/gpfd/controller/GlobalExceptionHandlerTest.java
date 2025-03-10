package uk.gov.laa.gpfd.controller;

import org.junit.jupiter.api.Test;
import uk.gov.laa.gpfd.exception.CsvStreamException;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.exception.TransferException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static uk.gov.laa.gpfd.exception.TemplateResourceException.ExcelTemplateCreationException;
import static uk.gov.laa.gpfd.exception.TemplateResourceException.LocalTemplateReadException;

@SuppressWarnings("DataFlowIssue")
class GlobalExceptionHandlerTest {

    private static final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void shouldHandleCsvStreamExceptionWithNullMessage() {
        // Given
        var exception = new CsvStreamException(null);

        // When
        var response = globalExceptionHandler.handleCsvStreamException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody().getError());
    }

    @Test
    void shouldHandleDatabaseReadExceptionWithLongMessage() {
        // Given
        var longMessage = "Database error occurred while processing request: " + "A".repeat(1000);
        var exception = new DatabaseReadException(longMessage);

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
    void shouldHandleCsvStreamExceptionWithWhitespaceMessage() {
        // Given
        var exception = new CsvStreamException("   ");

        // When
        var response = globalExceptionHandler.handleCsvStreamException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("   ", response.getBody().getError());
    }

    @Test
    void shouldHandleDatabaseReadExceptionWithWhitespaceMessage() {
        // Given
        var exception = new DatabaseReadException("   ");

        // When
        var response = globalExceptionHandler.handleDatabaseReadException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("   ", response.getBody().getError());
    }

    @Test
    void shouldHandleCsvStreamException() {
        // Given
        var exception = new CsvStreamException("CSV Stream Error");

        // When
        var response = globalExceptionHandler.handleCsvStreamException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("CSV Stream Error", response.getBody().getError());
    }

    @Test
    void shouldHandleLocalTemplateReadException() {
        // Given
        var exception = new LocalTemplateReadException("Could not find template");

        // When
        var response = globalExceptionHandler.handleLocalTemplateReadException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Could not find template", response.getBody().getError());
    }

    @Test
    void shouldHandleExcelTemplateCreationException() {
        // Given
        var exception = new ExcelTemplateCreationException("Meh, doesnt work on my machine!", new RuntimeException());

        // When
        var response = globalExceptionHandler.handleExcelTemplateCreationException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Meh, doesnt work on my machine!", response.getBody().getError());
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

    @Test
    void shouldHandleDatabaseReadException() {
        // Given
        var exception = new DatabaseReadException("Database Read Error");

        // When
        var response = globalExceptionHandler.handleDatabaseReadException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Database Read Error", response.getBody().getError());
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
    void shouldHandleCsvStreamExceptionWithMultilineMessage() {
        // Given
        var multilineMessage = "CSV Stream Error\nLine 1\nLine 2";
        var exception = new CsvStreamException(multilineMessage);

        // When
        var response = globalExceptionHandler.handleCsvStreamException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(multilineMessage, response.getBody().getError());
    }

    @Test
    void shouldHandleDatabaseReadExceptionWithSpecialCharacters() {
        // Given
        var exception = new DatabaseReadException("Error! @#$%^&*()");

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
    void shouldHandleCsvStreamExceptionWithSpecialCharacters() {
        // Given
        var exception = new CsvStreamException("Special chars: \t\n!@#$%^&*()");

        // When
        var response = globalExceptionHandler.handleCsvStreamException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Special chars: \t\n!@#$%^&*()", response.getBody().getError());
    }

    @Test
    void shouldHandleCsvStreamExceptionWithEmojiMessage() {
        // Given
        var exception = new CsvStreamException("Error 🚀🔥");

        // When
        var response = globalExceptionHandler.handleCsvStreamException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error 🚀🔥", response.getBody().getError());
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
    void shouldHandleCsvStreamExceptionWithVeryLongMessage() {
        // Given
        var longMessage = "A".repeat(10000);
        var exception = new CsvStreamException(longMessage);

        // When
        var response = globalExceptionHandler.handleCsvStreamException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(longMessage, response.getBody().getError());
    }

    @Test
    void shouldHandleDatabaseReadExceptionWithNewlineCharacters() {
        // Given
        var message = "Database error\nDetails: connection failed.";
        var exception = new DatabaseReadException(message);

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
    void shouldHandleCsvStreamExceptionWithMessageAsPeriod() {
        // Given
        var exception = new CsvStreamException(".");

        // When
        var response = globalExceptionHandler.handleCsvStreamException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(".", response.getBody().getError());
    }

    @Test
    void shouldHandleDatabaseReadExceptionWithJsonMessage() {
        // Given
        var jsonMessage = "{\"error\":\"database failure\"}";
        var exception = new DatabaseReadException(jsonMessage);

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
        var exception = new DatabaseReadException(utf16Message);

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
    void shouldHandleCsvStreamExceptionWithAllWhitespaceMessage() {
        // Given
        var exception = new CsvStreamException("   ");

        // When
        var response = globalExceptionHandler.handleCsvStreamException(exception);

        // Then
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("   ", response.getBody().getError());
    }

    @Test
    void shouldHandleDatabaseReadExceptionWithSingleNewlineMessage() {
        // Given
        var exception = new DatabaseReadException("\n");

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

}
