package uk.gov.laa.gpfd.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.dao.ReportDao;
import uk.gov.laa.gpfd.data.ReportsTestDataFactory;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.mapper.GetReportById200ResponseMapper;
import uk.gov.laa.gpfd.mapper.ReportsGet200ResponseReportListInnerMapper;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;
import uk.gov.laa.gpfd.services.ReportManagementService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
}
