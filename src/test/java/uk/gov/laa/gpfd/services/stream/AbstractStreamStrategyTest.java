package uk.gov.laa.gpfd.services.stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.dao.ReportDao;
import uk.gov.laa.gpfd.enums.FileExtension;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.services.DataStreamer;

import java.util.Optional;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.createTestReport;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.createTestReportWithQuery;
import static uk.gov.laa.gpfd.enums.FileExtension.CSV;
import static uk.gov.laa.gpfd.services.stream.AbstractDataStream.createCsvStreamStrategy;
import static uk.gov.laa.gpfd.services.stream.AbstractDataStream.createExcelStreamStrategy;

@ExtendWith(MockitoExtension.class)
class AbstractDataStreamTest {

    Report testReportDetails = createTestReport();

    @Mock
    private ReportDao reportDao;

    @Mock
    private DataStreamer dataStreamer;

    @Mock
    private StreamingResponseBody streamingResponseBody;

    @Test
    void shouldCreateProperResponse() {
        var testStream = new TestDataStream();

        var response = testStream.buildResponse("test_report", streamingResponseBody);

        assertEquals(streamingResponseBody, response.getBody());
        assertEquals("attachment; filename=\"test_report.csv\"",
                response.getHeaders().getFirst("Content-Disposition"));
    }

    @Test
    void shouldSanitizeFilename() {
        var testStream = new TestDataStream();

        var response = testStream.buildResponse("test\"report", streamingResponseBody);

        assertTrue(response.getHeaders().getFirst("Content-Disposition").contains("testreport"));
    }

    @Test
    void shouldRequireNonNullArgs() {
        assertThrows(NullPointerException.class, () -> createCsvStreamStrategy(null, dataStreamer));
        assertThrows(NullPointerException.class, () -> createCsvStreamStrategy(reportDao, null));
        assertThrows(NullPointerException.class, () -> createExcelStreamStrategy(null, dataStreamer));
        assertThrows(NullPointerException.class, () -> createExcelStreamStrategy(reportDao, null));
    }

    @Test
    void shouldStreamCSVReportData() {
        var reportId = randomUUID();

        when(reportDao.fetchReportById(reportId)).thenReturn(Optional.of(createTestReportWithQuery()));

        var stream = createCsvStreamStrategy(reportDao, dataStreamer);
        var response = stream.stream(reportId);

        assertEquals("Test Report.csv",
                response.getHeaders().getFirst("Content-Disposition").split("=")[1].replace("\"", ""));
    }

    @Test
    void shouldStreamXLSReportData() {
        var reportId = randomUUID();

        when(reportDao.fetchReportById(reportId)).thenReturn(Optional.of(createTestReportWithQuery()));

        var stream = createExcelStreamStrategy(reportDao, dataStreamer);
        var response = stream.stream(reportId);

        assertEquals("Test Report.xlsx",
                response.getHeaders().getFirst("Content-Disposition").split("=")[1].replace("\"", ""));
    }

    @Test
    void shouldThrowCSVWhenReportNotFound() {
        var reportId = randomUUID();
        when(reportDao.fetchReportById(reportId)).thenReturn(Optional.empty());

        var stream = createCsvStreamStrategy(reportDao, dataStreamer);

        assertThrows(ReportIdNotFoundException.class, () -> stream.stream(reportId));
    }

    @Test
    void shouldThrowWhenXLSReportNotFound() {
        var reportId = randomUUID();
        when(reportDao.fetchReportById(reportId)).thenReturn(Optional.empty());

        var stream = createExcelStreamStrategy(reportDao, dataStreamer);

        assertThrows(ReportIdNotFoundException.class, () -> stream.stream(reportId));
    }

    private static class TestDataStream extends AbstractDataStream {
        @Override
        public FileExtension getFormat() {
            return CSV;
        }

        @Override
        public ResponseEntity<StreamingResponseBody> stream(UUID uuid) {
            return null;
        }
    }
}