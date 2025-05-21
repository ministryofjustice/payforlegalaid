package uk.gov.laa.gpfd.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.services.stream.DataStream;

import static java.util.Map.of;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.enums.FileExtension.CSV;
import static uk.gov.laa.gpfd.enums.FileExtension.XLSX;
import static uk.gov.laa.gpfd.services.StreamingService.DefaultStreamingService;

@ExtendWith(MockitoExtension.class)
class StreamingServiceTest {

    @Mock
    private DataStream csvStrategy;

    @Mock
    private DataStream excelStrategy;

    @Mock
    private ResponseEntity<StreamingResponseBody> mockResponse;

    @InjectMocks
    private DefaultStreamingService streamingService;

    @Test
    void shouldUseCorrectSteamStrategyForFormat() {
        var reportId = randomUUID();
        var strategies = of(
                CSV, csvStrategy,
                XLSX, excelStrategy
        );

        var service = new DefaultStreamingService(strategies);
        when(csvStrategy.stream(reportId)).thenReturn(mockResponse);

        var result = service.stream(reportId, CSV);

        assertEquals(mockResponse, result);
        verify(csvStrategy).stream(reportId);
        verifyNoInteractions(excelStrategy);
    }

    @Test
    void shouldThrowExceptionForUnsupportedSteamFormat() {
        var reportId = randomUUID();
        var strategies = of(
                CSV, csvStrategy
        );

        var service = new DefaultStreamingService(strategies);

        assertThrows(NullPointerException.class, () -> {
            service.stream(reportId, XLSX);
        });
    }

    @Test
    void shouldPropagateSteamStrategyExceptions() {
        var reportId = randomUUID();
        var strategies = of(
                CSV, csvStrategy
        );
        var service = new DefaultStreamingService(strategies);

        when(csvStrategy.stream(reportId)).thenThrow(new IllegalStateException("Streaming error"));

        assertThrows(IllegalStateException.class, () -> {
            service.stream(reportId, CSV);
        });
    }

    @Test
    void shouldHandleStreamForAllRegisteredFormats() {
        var reportId = randomUUID();
        var strategies = of(
                CSV, csvStrategy,
                XLSX, excelStrategy
        );
        var service = new DefaultStreamingService(strategies);

        when(excelStrategy.stream(reportId)).thenReturn(mockResponse);

        var result = service.stream(reportId, XLSX);

        assertEquals(mockResponse, result);
        verify(excelStrategy).stream(reportId);
    }
}