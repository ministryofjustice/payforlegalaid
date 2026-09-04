package uk.gov.laa.gpfd.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.exception.ReportOutputTypeNotFoundException;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.services.stream.DataStream;

import static java.util.Map.of;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.model.FileExtension.CSV;
import static uk.gov.laa.gpfd.model.FileExtension.XLSX;
import static uk.gov.laa.gpfd.services.StreamingService.DefaultStreamingService;

@ExtendWith(MockitoExtension.class)
class StreamingServiceTest {

    @Mock
    private DataStream csvStrategy;

    @Mock
    private DataStream excelStrategy;

    @Mock
    private StreamingResponseBody mockStreamingBody;

    @Mock
    private Report report;

    @Test
    void shouldUseCorrectStreamStrategyForFormat() {
        var reportId = randomUUID();
        var strategies = of(
                CSV, csvStrategy,
                XLSX, excelStrategy
        );

        var service = new DefaultStreamingService(strategies);
        when(csvStrategy.stream(reportId)).thenReturn(mockStreamingBody);

        var result = service.stream(reportId, CSV);

        assertEquals(mockStreamingBody, result);
        verify(csvStrategy).stream(reportId);
        verifyNoInteractions(excelStrategy);
    }

    @Test
    void shouldUseCorrectStreamStrategyForResolvedReport() {
        var strategies = of(
                CSV, csvStrategy,
                XLSX, excelStrategy
        );

        var service = new DefaultStreamingService(strategies);
        when(csvStrategy.stream(report)).thenReturn(mockStreamingBody);

        var result = service.stream(report, CSV);

        assertEquals(mockStreamingBody, result);
        verify(csvStrategy).stream(report);
        verifyNoInteractions(excelStrategy);
    }

    @Test
    void shouldThrowReportOutputTypeNotFoundExceptionForUnsupportedStreamFormat() {
        var reportId = randomUUID();
        var strategies = of(
                CSV, csvStrategy
        );

        var service = new DefaultStreamingService(strategies);

        assertThrows(ReportOutputTypeNotFoundException.class, () -> service.stream(reportId, XLSX));
    }

    @Test
    void shouldThrowReportOutputTypeNotFoundExceptionForUnsupportedResolvedReportFormat() {
        var service = new DefaultStreamingService(of(CSV, csvStrategy));

        assertThrows(ReportOutputTypeNotFoundException.class, () -> service.stream(report, XLSX));
    }

    @Test
    void shouldPropagateStreamStrategyExceptions() {
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

        when(excelStrategy.stream(reportId)).thenReturn(mockStreamingBody);

        var result = service.stream(reportId, XLSX);

        assertEquals(mockStreamingBody, result);
        verify(excelStrategy).stream(reportId);
    }
}