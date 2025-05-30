package uk.gov.laa.gpfd.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.laa.gpfd.dao.support.ReportWithQueriesAndFieldAttributesExtractor;
import uk.gov.laa.gpfd.data.ReportsTestDataFactory;
import uk.gov.laa.gpfd.model.Report;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.exception.DatabaseReadException.DatabaseFetchException;

@ExtendWith(MockitoExtension.class)
class ReportDaoTest {

    @Mock
    private JdbcTemplate readOnlyJdbcTemplate;

    @Mock
    private ReportWithQueriesAndFieldAttributesExtractor extractor;

    @InjectMocks
    private ReportDao reportDao;

    private UUID testReportId;
    private Report testReport;

    @BeforeEach
    void setUp() {
        testReportId = UUID.randomUUID();
        testReport =  ReportsTestDataFactory.createTestReport(testReportId);
    }

    @Test
    void fetchReportById_shouldReturnReportWhenFound() {
        when(readOnlyJdbcTemplate.query(anyString(), any(ReportWithQueriesAndFieldAttributesExtractor.class), any()))
                .thenReturn(Collections.singletonList(testReport));

        var result = reportDao.fetchReportById(testReportId);

        assertTrue(result.isPresent());
        assertEquals(testReportId, result.get().getId());
        verify(readOnlyJdbcTemplate).query(anyString(), any(ReportWithQueriesAndFieldAttributesExtractor.class), eq(testReportId.toString()));
    }

    @Test
    void fetchReportById_shouldReturnEmptyOptionalWhenReportNotFound() {
        when(readOnlyJdbcTemplate.query(anyString(), any(ReportWithQueriesAndFieldAttributesExtractor.class), any()))
                .thenReturn(Collections.emptyList());

        var result = reportDao.fetchReportById(testReportId);

        assertFalse(result.isPresent());
    }

    @Test
    void fetchReportById_shouldThrowDatabaseReadExceptionOnDataAccessError() {
        when(readOnlyJdbcTemplate.query(anyString(), any(ReportWithQueriesAndFieldAttributesExtractor.class), any()))
                .thenThrow(new DataAccessException("Database error") {});

        assertThrows(DatabaseFetchException.class, () -> reportDao.fetchReportById(testReportId));
    }

    @Test
    void fetchReports_shouldReturnCollectionOfReports() {
        var expectedReports = Arrays.asList(testReport,  ReportsTestDataFactory.createTestReport());
        when(readOnlyJdbcTemplate.query(anyString(), any(ReportWithQueriesAndFieldAttributesExtractor.class)))
                .thenReturn(expectedReports);

        var result = reportDao.fetchReports();

        assertEquals(2, result.size());
        assertTrue(result.contains(testReport));
        verify(readOnlyJdbcTemplate).query(anyString(), any(ReportWithQueriesAndFieldAttributesExtractor.class));
    }

    @Test
    void fetchReports_shouldReturnEmptyCollectionWhenNoReportsFound() {
        when(readOnlyJdbcTemplate.query(anyString(), any(ReportWithQueriesAndFieldAttributesExtractor.class)))
                .thenReturn(Collections.emptyList());

        var result = reportDao.fetchReports();

        assertTrue(result.isEmpty());
    }

    @Test
    void fetchReports_shouldThrowDatabaseReadExceptionOnDataAccessError() {
        when(readOnlyJdbcTemplate.query(anyString(), any(ReportWithQueriesAndFieldAttributesExtractor.class)))
                .thenThrow(new DataAccessException("Database error") {});

        assertThrows(DatabaseFetchException.class, () -> reportDao.fetchReports());
    }

    @Test
    void fetchReports_shouldNotThrowReportIdNotFoundException() {
        when(readOnlyJdbcTemplate.query(anyString(), any(ReportWithQueriesAndFieldAttributesExtractor.class)))
                .thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> reportDao.fetchReports());
    }
}
