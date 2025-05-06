package uk.gov.laa.gpfd.service;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.config.AppConfig;
import uk.gov.laa.gpfd.dao.ReportDetailsDao;
import uk.gov.laa.gpfd.data.ReportsTestDataFactory;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.exception.ReportOutputTypeNotFoundException;
import uk.gov.laa.gpfd.mapper.ReportsGet200ResponseReportListInnerMapper;
import uk.gov.laa.gpfd.services.ReportManagementService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportManagementServiceTest {

    @Mock
    AppConfig appConfig;

    @Mock
    private ReportDetailsDao reportDetailsDao;

    @InjectMocks
    private ReportManagementService reportManagementService;

    UUID DEFAULT_REPORT_ID = UUID.fromString("b36f9bbb-1178-432c-8f99-8090e285f2d3");
    UUID INVALID_EXTENSION_REPORT_ID = UUID.fromString("f46b4d3d-c100-429a-bf9a-6c3305dbdbf6");

    @Test
    void should_return_exception_in_fetch_report_list_entries() throws DatabaseReadException {
        when(reportDetailsDao.fetchReportList()).thenThrow(DatabaseReadException.class);

        assertThrows(DatabaseReadException.class, () -> reportManagementService.fetchReportListEntries());
    }

    @Test
    void shouldReturnReportListEntriesWhenValidReportsExist() {
        // Given
        var excelReport = ReportsTestDataFactory.aCCMSInvoiceAnalysisExcelReport();
        var csvReport = ReportsTestDataFactory.aCCMSInvoiceAnalysisCSVReport();
        var list = Arrays.asList(excelReport, csvReport);
        var expected = list.stream().map(ReportsGet200ResponseReportListInnerMapper::map).toList();

        when(reportDetailsDao.fetchReportList()).thenReturn(list);

        // When
        var result = reportManagementService.fetchReportListEntries();

        // Then
        assertNotNull(result);
        assertEquals(expected.size(), result.size());
        assertEquals(expected, result);
        verify(reportDetailsDao, times(1)).fetchReportList();
    }

    @Test
    void shouldReturnEmptyListWhenNoReportsExist() {
        // Given
        when(reportDetailsDao.fetchReportList()).thenReturn(Collections.emptyList());

        // When
        var result = reportManagementService.fetchReportListEntries();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reportDetailsDao, times(1)).fetchReportList();
    }

    @Test
    void shouldReturnSingleReportWhenOnlyOneReportExists() throws DatabaseReadException {
        // Given
        var excelReport = ReportsTestDataFactory.aCCMSInvoiceAnalysisExcelReport();
        when(reportDetailsDao.fetchReportList()).thenReturn(Collections.singletonList(excelReport));
        var expected = List.of(ReportsGet200ResponseReportListInnerMapper.map(excelReport));

        // When
        var result = reportManagementService.fetchReportListEntries();

        // Then
        assertNotNull(result);
        assertEquals(expected.size(), result.size());
        assertEquals(expected, result);
        verify(reportDetailsDao, times(1)).fetchReportList();
    }

    @Test
    void shouldReturnValidReportsAndIgnoreInvalidData() throws DatabaseReadException {
        // Given
        var csvReport = ReportsTestDataFactory.aCCMSInvoiceAnalysisCSVReport();
        var invalidReport = ReportsTestDataFactory.invalidReportData();
        var list = Arrays.asList(csvReport, invalidReport);
        when(reportDetailsDao.fetchReportList()).thenReturn(list);

        // When
        var result = reportManagementService.fetchReportListEntries();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());  // Only one valid mapping should be returned
        verify(reportDetailsDao, times(1)).fetchReportList();
    }

    @Test
    void shouldRaiseDatbaseReadExceptionWhenDatabaseReadException() throws DatabaseReadException {
        when(reportDetailsDao.fetchReportList()).thenThrow(DatabaseReadException.class);

        assertThrows(DatabaseReadException.class,
                () -> reportManagementService.fetchReportListEntries());
    }

    @Test
    void shouldRaiseReportIdNotFoundExceptionWhenReportIdNotFoundException() throws ReportIdNotFoundException {
        when(reportDetailsDao.fetchReportList()).thenThrow(ReportIdNotFoundException.class);

        assertThrows(ReportIdNotFoundException.class,
                () -> reportManagementService.fetchReportListEntries());
    }

    @Test
    void shouldRaiseReportIdNotFoundExceptionWhenSpecificReportIdNotFound() throws ReportIdNotFoundException {
        when(reportDetailsDao.fetchReport(Mockito.any())).thenThrow(ReportIdNotFoundException.class);

        assertThrows(ReportIdNotFoundException.class,
            () -> reportManagementService.getDetailsForSpecificReport(DEFAULT_REPORT_ID));
    }

    @Test
    void shouldRaiseReportOutputTypeNotFoundExceptionWhenUnknownExtension() throws ReportIdNotFoundException {
        when(reportDetailsDao.fetchReport(Mockito.any())).thenReturn(ReportsTestDataFactory.aReportWithInvalidExtension());
        when(appConfig.getServiceUrl()).thenReturn("http://localhost");

        assertThrows(ReportOutputTypeNotFoundException.class,
            () -> reportManagementService.getDetailsForSpecificReport(INVALID_EXTENSION_REPORT_ID));
    }

    @Test
    void shouldReturnCorrectReportDownloadUrlWhenValidReport() {
        when(reportDetailsDao.fetchReport(Mockito.any())).thenReturn(ReportsTestDataFactory.aCCMSInvoiceAnalysisCSVReport());
        when(appConfig.getServiceUrl()).thenReturn("http://localhost");

        assertEquals("http://localhost/csv/f46b4d3d-c100-429a-bf9a-6c3305dbdbf5", reportManagementService.getDetailsForSpecificReport(DEFAULT_REPORT_ID).getReportDownloadUrl());
    }

}
