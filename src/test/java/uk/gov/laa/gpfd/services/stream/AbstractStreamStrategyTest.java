package uk.gov.laa.gpfd.services.stream;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.dao.ReportDao;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.services.DataStreamer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.services.stream.AbstractDataStream.createCsvStreamStrategy;
import static uk.gov.laa.gpfd.services.stream.AbstractDataStream.createExcelStreamStrategy;
import static uk.gov.laa.gpfd.services.stream.DataStream.APPLICATION_EXCEL;

@ExtendWith(MockitoExtension.class)
class AbstractDataStreamTest {

    @Mock
    private ReportDao reportDao;

    @Mock
    private DataStreamer dataStreamer;

    @Mock
    private StreamingResponseBody streamingResponseBody;

    @SneakyThrows
    @Test
    void excelDataStream_shouldOnlyCallStreamWhenWritingToOutput() {
        var testStream = new AbstractDataStream.ExcelDataStream(reportDao, dataStreamer);
        var reportId = randomUUID();
        var mockReport = mock(Report.class);

        when(reportDao.fetchReportById(reportId)).thenReturn(Optional.of(mockReport));
        var outputStream = new ByteArrayOutputStream();
        var body = testStream.stream(reportId);

        body.writeTo(outputStream);

        verify(reportDao).fetchReportById(reportId);
        verify(dataStreamer).stream(mockReport, outputStream);
    }

    @SneakyThrows
    @Test
    void csvDataStream_shouldOnlyCallStreamWhenWritingToOutput() {
        var testStream = new AbstractDataStream.CsvDataStream(reportDao, dataStreamer);
        var reportId = randomUUID();
        var mockReport = mock(Report.class);

        when(reportDao.fetchReportById(reportId)).thenReturn(Optional.of(mockReport));
        var outputStream = new ByteArrayOutputStream();
        var body = testStream.stream(reportId);

        body.writeTo(outputStream);

        verify(reportDao).fetchReportById(reportId);
        verify(dataStreamer).stream(mockReport, outputStream);
    }

    @SneakyThrows
    @Test
    void shouldNotCallStreamIfDoesNotWriteToOutput() {
        var testStream = new AbstractDataStream.CsvDataStream(reportDao, dataStreamer);
        var reportId = randomUUID();
        var mockReport = mock(Report.class);

        when(reportDao.fetchReportById(reportId)).thenReturn(Optional.of(mockReport));

        testStream.stream(reportId);

        verify(reportDao).fetchReportById(reportId);
        verifyNoInteractions(dataStreamer);
    }

    @Test
    void shouldRequireNonNullArgs() {
        assertThrows(NullPointerException.class, () -> createCsvStreamStrategy(null, dataStreamer));
        assertThrows(NullPointerException.class, () -> createCsvStreamStrategy(reportDao, null));
        assertThrows(NullPointerException.class, () -> createExcelStreamStrategy(null, dataStreamer));
        assertThrows(NullPointerException.class, () -> createExcelStreamStrategy(reportDao, null));
    }

    @Test
    void shouldThrowWhenCSVReportNotFound() {
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

    @SneakyThrows
    @Test
    void shouldThrowWhenStreamThrowsException() {
        var testStream = new AbstractDataStream.ExcelDataStream(reportDao, dataStreamer);
        var reportId = randomUUID();
        var mockReport = mock(Report.class);

        when(reportDao.fetchReportById(reportId)).thenReturn(Optional.of(mockReport));
        doThrow(new IOException("uh oh")).when(dataStreamer).stream(any(), any());

        var outputStream = new ByteArrayOutputStream();
        var body = testStream.stream(reportId);
        assertThrows(IOException.class, () -> body.writeTo(outputStream));
    }

    @Test
    void excelDataStream_shouldGetMediaTypeOfXlsx() {
        var testStream = new AbstractDataStream.ExcelDataStream(reportDao, dataStreamer);
        assertEquals(MediaType.valueOf(APPLICATION_EXCEL), testStream.getContentType());
    }

    @Test
    void csvDataStream_shouldGetMediaTypeOfCsv() {
        var testStream = new AbstractDataStream.CsvDataStream(reportDao, dataStreamer);
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, testStream.getContentType());
    }
}