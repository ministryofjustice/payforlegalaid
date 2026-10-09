package uk.gov.laa.gpfd.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.sentry.Hint;
import io.sentry.ITransportFactory;
import io.sentry.IScopes;
import io.sentry.ScopeCallback;
import io.sentry.Sentry;
import io.sentry.SentryEnvelope;
import io.sentry.SentryEvent;
import io.sentry.protocol.Message;
import io.sentry.protocol.User;
import io.sentry.transport.ITransport;
import io.sentry.spring.boot4.SentryAutoConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
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
import uk.gov.laa.gpfd.utils.RequestLogUtils;
import uk.gov.laa.gpfd.config.AsyncConfig;
import uk.gov.laa.gpfd.config.SentryConfig;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Stream.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    @TestConfiguration(proxyBeanMethods = false)
    @EnableWebMvc
    static class MvcTestConfiguration {
    }

    @RestController
    static class FailingController {
        @GetMapping("/sentry-policy-server")
        public String serverFailure() {
            throw new DatabaseFetchException("Synthetic database failure");
        }

        @GetMapping("/sentry-policy-client")
        public String expectedClientFailure() {
            throw new ReportIdNotFoundException("Synthetic missing report");
        }
    }

    private static final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
    private ListAppender<ILoggingEvent> appender;

    @Test
    void preservesSentExceptionAndCauseMessagesWithoutMutatingLocalExceptions() throws Exception {
        ITransport transport = mock(ITransport.class);
        Sentry.init(options -> {
            options.setDsn("https://public@example.test/1");
            options.setTransportFactory((settings, details) -> transport);
            options.setBeforeSend(new SentryConfig().sentryBeforeSend());
            options.setEnableUncaughtExceptionHandler(false);
            options.setEnableShutdownHook(false);
        });
        try {
            var cause = new IOException("Email person@example.test token=synthetic-secret");
            var exception = new IllegalStateException("Name: Synthetic Person; address: 12 Example Street", cause);
            uk.gov.laa.gpfd.utils.SentryEvents.captureException(exception, "synthetic.failure");

            var envelope = ArgumentCaptor.forClass(SentryEnvelope.class);
            verify(transport).send(envelope.capture(), any(Hint.class));
            var serializer = Sentry.getCurrentScopes().getOptions().getSerializer();
            var event = envelope.getValue().getItems().iterator().next().getEvent(serializer);
            assertEquals(2, event.getExceptions().size());
            event.getExceptions().forEach(sent -> {
                assertTrue(sent.getValue().equals(exception.getMessage())
                    || sent.getValue().equals(cause.getMessage()));
                assertNotNull(sent.getStacktrace());
                assertFalse(sent.getStacktrace().getFrames().isEmpty());
            });
            assertEquals("synthetic.failure", event.getTag(RequestLogUtils.EVENT_ACTION));
            var json = new StringWriter();
            serializer.serialize(event, json);
            assertTrue(json.toString().contains("person@example.test"));
            assertTrue(json.toString().contains("synthetic-secret"));
            assertTrue(json.toString().contains("Synthetic Person"));
            assertTrue(json.toString().contains("12 Example Street"));
            assertTrue(exception.getMessage().contains("Synthetic Person"));
            assertSame(cause, exception.getCause());
        } finally {
            Sentry.close();
        }
    }

    @Test
    void preservesFormattedMessagesTemplatesAndParameters() {
        var event = new SentryEvent();
        var original = new Message();
        original.setMessage("User %s failed");
        original.setFormatted("User synthetic@example.test failed");
        original.setParams(java.util.List.of("synthetic@example.test"));
        event.setMessage(original);

        assertSame(event, new SentryConfig().sentryBeforeSend().execute(event, new Hint()));
        assertSame(original, event.getMessage());
        assertEquals("User synthetic@example.test failed", event.getMessage().getFormatted());
        assertEquals("User %s failed", event.getMessage().getMessage());
        assertEquals(java.util.List.of("synthetic@example.test"), event.getMessage().getParams());
    }

    @Test
    void defaultMvcIntegrationCapturesServerFailureOnceAndSkipsExpectedClientFailure() {
        ITransport transport = mock(ITransport.class);
        new WebApplicationContextRunner()
                .withInitializer(new ConfigDataApplicationContextInitializer())
                .withConfiguration(AutoConfigurations.of(SentryAutoConfiguration.class))
                .withUserConfiguration(MvcTestConfiguration.class, SentryConfig.class)
                .withBean(FailingController.class)
                .withBean(GlobalExceptionHandler.class)
                .withBean(ITransportFactory.class, () -> (options, details) -> transport)
                .withPropertyValues("spring.config.location=file:target/classes/application.yml",
                        "sentry.dsn=https://public@example.test/1", "sentry.traces-sample-rate=0.0",
                        "sentry.enable-uncaught-exception-handler=false", "sentry.enable-shutdown-hook=false")
                .run(context -> {
                    assertNull(context.getStartupFailure());
                    var options = context.getBean(IScopes.class).getOptions();
                    assertSame(context.getBean("sentryBeforeSend"), options.getBeforeSend());
                    var mvc = MockMvcBuilders.webAppContextSetup(context).build();
                    assertEquals(500, mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .get("/sentry-policy-server")).andReturn().getResponse().getStatus());
                    assertEquals(404, mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .get("/sentry-policy-client")).andReturn().getResponse().getStatus());
                    var envelope = ArgumentCaptor.forClass(SentryEnvelope.class);
                    verify(transport).send(envelope.capture(), any(Hint.class));
                    var event = envelope.getValue().getItems().iterator().next().getEvent(options.getSerializer());
                    assertEquals("database.read.failure", event.getTag(RequestLogUtils.EVENT_ACTION));
                });
    }

    @ParameterizedTest
    @MethodSource("expectedSentryFailures")
    void ignoresExpectedFailuresAtAnyManualCaptureBoundary(Throwable exception) {
        try (var sentry = mockStatic(Sentry.class)) {
            uk.gov.laa.gpfd.utils.SentryEvents.captureException(exception, "synthetic.failure");
            sentry.verifyNoInteractions();
        }
    }

    private static Stream<Throwable> expectedSentryFailures() {
        return Stream.of(new InvalidReportFormatException(UUID.randomUUID(), "XLSX", "CSV"),
                new InvalidDownloadFormatException("synthetic.docx", UUID.randomUUID()),
                new ReportNotSupportedForDownloadException(UUID.randomUUID()),
                new org.springframework.web.server.ResponseStatusException(BAD_REQUEST),
                new java.io.EOFException());
    }

    @Test
    void sdkFilterDropsDisconnectsAndExpectedFailuresButKeepsSecurityEvents() {
        var callback = new SentryConfig().sentryBeforeSend();
        assertNull(callback.execute(new SentryEvent(new java.io.EOFException()), new Hint()));
        assertNull(callback.execute(new SentryEvent(new ReportIdNotFoundException("Synthetic missing report")), new Hint()));
        assertNotNull(callback.execute(new SentryEvent(new OperationNotSupportedException("/synthetic")), new Hint()));
        assertNotNull(callback.execute(new SentryEvent(new ReportAccessException(UUID.randomUUID())), new Hint()));
        assertNotNull(callback.execute(new SentryEvent(new IllegalStateException("Synthetic bug")), new Hint()));
        assertNotNull(callback.execute(new SentryEvent(), new Hint()));
    }

    @Test
    void capturesUncaughtAsyncFailureExactlyOnce() throws Exception {
        var exception = new IllegalStateException("Synthetic async failure");
        try (var sentry = mockStatic(Sentry.class)) {
            new AsyncConfig().getAsyncUncaughtExceptionHandler().handleUncaughtException(exception,
                    GlobalExceptionHandlerTest.class.getDeclaredMethod("capturesUncaughtAsyncFailureExactlyOnce"),
                    "private-method-argument");
            sentry.verify(() -> Sentry.captureException(eq(exception), any(ScopeCallback.class)));
            sentry.verifyNoMoreInteractions();
        }
    }

    @Test
    void capturesUnsupportedOperationOnceAndKeeps501Response() {
        var exception = new OperationNotSupportedException("/synthetic");
        try (var sentry = mockStatic(Sentry.class)) {
            var response = globalExceptionHandler.handleNotSupportedException(exception);
            assertEquals(NOT_IMPLEMENTED, response.getStatusCode());
            assertEquals(exception.getMessage(), response.getBody().getError());
            sentry.verify(() -> Sentry.captureException(eq(exception), any(ScopeCallback.class)));
            sentry.verifyNoMoreInteractions();
        }
    }

    @Test
    void capturesUnexpectedDatabaseFailureExactlyOnce() {
        var exception = new DatabaseFetchException("Synthetic database failure");
        try (var sentry = mockStatic(Sentry.class)) {
            assertEquals(INTERNAL_SERVER_ERROR,
                    globalExceptionHandler.handleDatabaseReadException(exception).getStatusCode());
            sentry.verify(() -> Sentry.captureException(eq(exception), any(ScopeCallback.class)));
            sentry.verifyNoMoreInteractions();
        }
    }

    @Test
    void doesNotCaptureRoutineMissingReport() {
        try (var sentry = mockStatic(Sentry.class)) {
            assertEquals(NOT_FOUND, globalExceptionHandler.handleReportIdNotFoundException(
                    new ReportIdNotFoundException("Synthetic missing report")).getStatusCode());
            sentry.verifyNoInteractions();
        }
    }

    @Test
    void doesNotCaptureClientDisconnectInWrappedStreamError() {
        var exception = new StreamErrorException("Synthetic disconnect", UUID.randomUUID());
        exception.initCause(new java.io.EOFException());
        try (var sentry = mockStatic(Sentry.class)) {
            globalExceptionHandler.handleStreamErrorException(exception);
            sentry.verifyNoInteractions();
        }
    }

    @Test
    void capturesDeniedReportAccessWithScopedMetadata() {
        var exception = new ReportAccessException(UUID.randomUUID());
        try (var sentry = mockStatic(Sentry.class)) {
            var response = globalExceptionHandler.handleReportAccessException(exception);
            assertEquals(FORBIDDEN, response.getStatusCode());
            sentry.verify(() -> Sentry.captureException(eq(exception), any(ScopeCallback.class)));
            sentry.verifyNoMoreInteractions();
        }
    }

    @Test
    void deliveredSecurityEventHasScopedTagsWithoutUserIdentity() throws Exception {
        ITransport transport = mock(ITransport.class);
        Sentry.init(options -> {
            options.setDsn("https://public@example.test/1");
            options.setEnvironment("dev");
            options.setRelease("security-events-test");
            options.setTransportFactory((settings, details) -> transport);
            options.setEnableUncaughtExceptionHandler(false);
            options.setEnableShutdownHook(false);
        });
        try {
            User user = new User();
            user.setId("synthetic-user");
            user.setEmail("synthetic@example.test");
            Sentry.setUser(user);

            globalExceptionHandler.handleReportAccessException(new ReportAccessException(UUID.randomUUID()));
            Sentry.captureException(new IllegalStateException("Separate synthetic failure"));

            var envelopes = ArgumentCaptor.forClass(SentryEnvelope.class);
            verify(transport, times(2)).send(envelopes.capture(), any(Hint.class));
            var serializer = Sentry.getCurrentScopes().getOptions().getSerializer();
            var securityEvent = envelopes.getAllValues().getFirst().getItems().iterator().next().getEvent(serializer);
            var nextEvent = envelopes.getAllValues().getLast().getItems().iterator().next().getEvent(serializer);
            assertEquals("authorization.denied", securityEvent.getTag(RequestLogUtils.EVENT_ACTION));
            assertEquals("failure", securityEvent.getTag(RequestLogUtils.EVENT_OUTCOME));
            assertEquals("dev", securityEvent.getEnvironment());
            assertEquals("security-events-test", securityEvent.getRelease());
            assertNull(securityEvent.getUser().getId());
            assertNull(securityEvent.getUser().getEmail());
            assertNull(securityEvent.getUser().getUsername());
            assertNull(securityEvent.getUser().getIpAddress());
            assertNull(nextEvent.getTag(RequestLogUtils.EVENT_ACTION));
            assertEquals("synthetic-user", nextEvent.getUser().getId());
        } finally {
            Sentry.close();
        }
    }

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

        ILoggingEvent loggingEvent = appender.list.getFirst();
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

        ILoggingEvent loggingEvent = appender.list.getFirst();
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