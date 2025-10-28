package uk.gov.laa.gpfd.services.stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.dao.ReportDao;
import uk.gov.laa.gpfd.model.FileExtension;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.services.DataStreamer;

import java.util.Optional;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.createTestReport;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.createTestReportWithQuery;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.createTestReportForTacticalSol;
import static uk.gov.laa.gpfd.model.FileExtension.CSV;
import static uk.gov.laa.gpfd.model.FileExtension.S3STORAGE;
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

        var response = testStream.buildResponse(testReportDetails, streamingResponseBody);

        assertEquals(streamingResponseBody, response.getBody());
        assertEquals("attachment; filename=\"Test Report.csv\"",
                response.getHeaders().getFirst("Content-Disposition"));
    }

    @Test
    void shouldCreateProperResponseForS3StorageReports() {
        var testStream = new TestDataStream1();
        var reportId = randomUUID();

        var response = testStream.buildResponse(createTestReportForTacticalSol(reportId), streamingResponseBody);

        assertEquals(streamingResponseBody, response.getBody());
        assertEquals("application/octet-stream",
                response.getHeaders().getFirst("Content-Type"));
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

    private static class TestDataStream1 extends AbstractDataStream {
        @Override
        public FileExtension getFormat() {
            return S3STORAGE;
        }

        @Override
        public ResponseEntity<StreamingResponseBody> stream(UUID uuid) {
            return null;
        }
    }
}