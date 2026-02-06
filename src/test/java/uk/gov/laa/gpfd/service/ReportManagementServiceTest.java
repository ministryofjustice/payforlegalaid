package uk.gov.laa.gpfd.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.dao.ReportDao;
import uk.gov.laa.gpfd.data.ReportsTestDataFactory;
import uk.gov.laa.gpfd.exception.InvalidReportFormatException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.mapper.GetReportById200ResponseMapper;
import uk.gov.laa.gpfd.mapper.ReportsGet200ResponseReportListInnerMapper;
import uk.gov.laa.gpfd.model.FileExtension;
import uk.gov.laa.gpfd.model.ImmutableReportOutputType;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;
import uk.gov.laa.gpfd.services.ReportManagementService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.exception.DatabaseReadException.DatabaseFetchException;

@ExtendWith(MockitoExtension.class)
class ReportManagementServiceTest {

    @Mock
    private ReportDao reportDetailsDao;

    @Mock
    private GetReportById200ResponseMapper reportByIdMapper;

    @Mock
    private ReportsGet200ResponseReportListInnerMapper innerResponseMapper;

    @InjectMocks
    private ReportManagementService reportManagementService;

    @Test
    void shouldReturnMappedReportsWhenDaoReturnsData() {
        var report1 = ReportsTestDataFactory.createTestReport();
        var report2 = ReportsTestDataFactory.createTestReport();
        var reports = List.of(report1, report2);

        when(reportDetailsDao.fetchReports()).thenReturn(reports);

        List<ReportsGet200ResponseReportListInner> result = reportManagementService.fetchReportListEntries();

        assertEquals(2, result.size());
    }

    @Test
    void shouldThrowDatabaseReadExceptionWhenDaoThrowsException() {
        when(reportDetailsDao.fetchReports()).thenThrow(new DatabaseFetchException("DB error"));

        assertThrows(DatabaseFetchException.class, () -> reportManagementService.fetchReportListEntries());
    }

    @Test
    void shouldThrowReportIdNotFoundExceptionWhenReportDoesNotExist() {
        var reportId = UUID.randomUUID();
        when(reportDetailsDao.fetchReportById(reportId)).thenReturn(Optional.empty());

        assertThrows(ReportIdNotFoundException.class, () -> reportManagementService.createReportResponse(reportId));
        verify(reportDetailsDao).fetchReportById(reportId);
        verifyNoInteractions(reportByIdMapper);
    }

    @Test
    void sShouldThrowDatabaseReadExceptionWhenDaoThrowsException() {
        var reportId = UUID.randomUUID();
        when(reportDetailsDao.fetchReportById(reportId)).thenThrow(new DatabaseFetchException("DB error"));

        assertThrows(DatabaseFetchException.class, () -> reportManagementService.createReportResponse(reportId));
    }

    @Test
    void validateReportFormat_shouldThrowInvalidReportFormatExceptionWhenCsvReportRequestedAsExcel() {
        // Given
        var reportId = UUID.randomUUID();
        var csvOutputType = ImmutableReportOutputType.builder()
                .id(UUID.randomUUID())
                .fileExtension(FileExtension.CSV)
                .description("CSV Report")
                .build();
        var csvReport = ReportsTestDataFactory.createTestReportWithOutputType(csvOutputType);

        when(reportDetailsDao.fetchReportById(reportId)).thenReturn(Optional.of(csvReport));

        // When/Then
        var exception = assertThrows(InvalidReportFormatException.class, () ->
                reportManagementService.validateReportFormat(reportId, FileExtension.XLSX)
        );

        assertEquals(reportId, exception.getReportId());
        assertEquals("XLSX", exception.getRequestedFormat());
        assertEquals("CSV", exception.getActualFormat());
        verify(reportDetailsDao).fetchReportById(reportId);
    }

    @Test
    void validateReportFormat_shouldThrowInvalidReportFormatExceptionWhenExcelReportRequestedAsCsv() {
        // Given
        var reportId = UUID.randomUUID();
        var excelOutputType = ImmutableReportOutputType.builder()
                .id(UUID.randomUUID())
                .fileExtension(FileExtension.XLSX)
                .description("Excel Report")
                .build();
        var excelReport = ReportsTestDataFactory.createTestReportWithOutputType(excelOutputType);

        when(reportDetailsDao.fetchReportById(reportId)).thenReturn(Optional.of(excelReport));

        // When/Then
        var exception = assertThrows(InvalidReportFormatException.class, () ->
                reportManagementService.validateReportFormat(reportId, FileExtension.CSV)
        );

        assertEquals(reportId, exception.getReportId());
        assertEquals("CSV", exception.getRequestedFormat());
        assertEquals("XLSX", exception.getActualFormat());
        verify(reportDetailsDao).fetchReportById(reportId);
    }

    @Test
    void validateReportFormat_shouldNotThrowExceptionWhenCsvReportRequestedAsCsv() {
        // Given
        var reportId = UUID.randomUUID();
        var csvOutputType = ImmutableReportOutputType.builder()
                .id(UUID.randomUUID())
                .fileExtension(FileExtension.CSV)
                .description("CSV Report")
                .build();
        var csvReport = ReportsTestDataFactory.createTestReportWithOutputType(csvOutputType);

        when(reportDetailsDao.fetchReportById(reportId)).thenReturn(Optional.of(csvReport));

        // When/Then - should not throw
        assertDoesNotThrow(() ->
                reportManagementService.validateReportFormat(reportId, FileExtension.CSV)
        );

        verify(reportDetailsDao).fetchReportById(reportId);
    }

    @Test
    void validateReportFormat_shouldNotThrowExceptionWhenExcelReportRequestedAsExcel() {
        // Given
        var reportId = UUID.randomUUID();
        var excelOutputType = ImmutableReportOutputType.builder()
                .id(UUID.randomUUID())
                .fileExtension(FileExtension.XLSX)
                .description("Excel Report")
                .build();
        var excelReport = ReportsTestDataFactory.createTestReportWithOutputType(excelOutputType);

        when(reportDetailsDao.fetchReportById(reportId)).thenReturn(Optional.of(excelReport));

        // When/Then - should not throw
        assertDoesNotThrow(() ->
                reportManagementService.validateReportFormat(reportId, FileExtension.XLSX)
        );

        verify(reportDetailsDao).fetchReportById(reportId);
    }

    @Test
    void validateReportFormat_shouldThrowReportIdNotFoundExceptionWhenReportDoesNotExist() {
        // Given
        var reportId = UUID.randomUUID();
        when(reportDetailsDao.fetchReportById(reportId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(ReportIdNotFoundException.class, () ->
                reportManagementService.validateReportFormat(reportId, FileExtension.CSV)
        );

        verify(reportDetailsDao).fetchReportById(reportId);
    }

    @Test
    void validateReportFormat_shouldThrowDatabaseFetchExceptionWhenDaoThrowsException() {
        // Given
        var reportId = UUID.randomUUID();
        when(reportDetailsDao.fetchReportById(reportId)).thenThrow(new DatabaseFetchException("DB connection error"));

        // When/Then
        assertThrows(DatabaseFetchException.class, () ->
                reportManagementService.validateReportFormat(reportId, FileExtension.CSV)
        );

        verify(reportDetailsDao).fetchReportById(reportId);
    }

    @Test
    void validateReportFormat_shouldThrowInvalidReportFormatExceptionWhenS3StorageReportRequestedAsExcel() {
        // Given
        var reportId = UUID.randomUUID();
        var s3OutputType = ImmutableReportOutputType.builder()
                .id(UUID.randomUUID())
                .fileExtension(FileExtension.S3STORAGE)
                .description("Tactical Solution Report")
                .build();
        var s3Report = ReportsTestDataFactory.createTestReportWithOutputType(s3OutputType);

        when(reportDetailsDao.fetchReportById(reportId)).thenReturn(Optional.of(s3Report));

        // When/Then
        var exception = assertThrows(InvalidReportFormatException.class, () ->
                reportManagementService.validateReportFormat(reportId, FileExtension.XLSX)
        );

        assertEquals(reportId, exception.getReportId());
        assertEquals("XLSX", exception.getRequestedFormat());
        assertEquals("S3STORAGE", exception.getActualFormat());
        verify(reportDetailsDao).fetchReportById(reportId);
    }

    @Test
    void validateReportFormat_shouldThrowInvalidReportFormatExceptionWhenS3StorageReportRequestedAsCsv() {
        // Given
        var reportId = UUID.randomUUID();
        var s3OutputType = ImmutableReportOutputType.builder()
                .id(UUID.randomUUID())
                .fileExtension(FileExtension.S3STORAGE)
                .description("Tactical Solution Report")
                .build();
        var s3Report = ReportsTestDataFactory.createTestReportWithOutputType(s3OutputType);

        when(reportDetailsDao.fetchReportById(reportId)).thenReturn(Optional.of(s3Report));

        // When/Then
        var exception = assertThrows(InvalidReportFormatException.class, () ->
                reportManagementService.validateReportFormat(reportId, FileExtension.CSV)
        );

        assertEquals(reportId, exception.getReportId());
        assertEquals("CSV", exception.getRequestedFormat());
        assertEquals("S3STORAGE", exception.getActualFormat());
        verify(reportDetailsDao).fetchReportById(reportId);
    }
}
