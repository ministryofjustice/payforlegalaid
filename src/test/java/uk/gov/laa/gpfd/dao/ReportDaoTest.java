package uk.gov.laa.gpfd.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.JdbcClient;
import uk.gov.laa.gpfd.dao.support.ReportWithQueriesAndFieldAttributesExtractor;
import uk.gov.laa.gpfd.data.ReportsTestDataFactory;
import uk.gov.laa.gpfd.exception.ReportAccessException;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.utils.SecurityUtils;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.security.SilasRoles.RECONCILIATION;
import static uk.gov.laa.gpfd.security.SilasRoles.REP000;
import static uk.gov.laa.gpfd.exception.DatabaseReadException.DatabaseFetchException;

@ExtendWith(MockitoExtension.class)
class ReportDaoTest {

    @Mock
    private JdbcClient metadataClient;

    @Mock
    private ReportWithQueriesAndFieldAttributesExtractor extractor;

    @InjectMocks
    private ReportDao reportDao;

    @Mock
    SecurityUtils securityUtils;

    private JdbcClient.StatementSpec statementSpec;

    private UUID testReportId;
    private Report testReport;

    @BeforeEach
    void setUp() {
        testReportId = UUID.randomUUID();
        testReport = ReportsTestDataFactory.createTestReport(testReportId);
        statementSpec = mock(JdbcClient.StatementSpec.class);
        when(metadataClient.sql(anyString())).thenReturn(statementSpec);
        lenient().when(statementSpec.param(any())).thenReturn(statementSpec);
        lenient().when(statementSpec.param(anyString(), any())).thenReturn(statementSpec);
        reportDao = spy(new ReportDao(extractor, metadataClient, securityUtils));
    }

    @Test
    void fetchReportById_shouldReturnReportWhenFound() {
        doNothing().when(reportDao).verifyUserCanAccessReport(any());
        when(statementSpec.query(any(ResultSetExtractor.class)))
                .thenReturn(Collections.singletonList(testReport));

        var result = reportDao.fetchReportById(testReportId);

        assertTrue(result.isPresent());
        assertEquals(testReportId, result.get().getId());
        verify(reportDao, times(1)).verifyUserCanAccessReport(testReportId);
        verify(metadataClient).sql(anyString());
    }

    @Test
    void fetchReportById_shouldReturnEmptyOptionalWhenReportNotFound() {
        doNothing().when(reportDao).verifyUserCanAccessReport(any());
        when(statementSpec.query(any(ResultSetExtractor.class)))
                .thenReturn(Collections.emptyList());

        var result = reportDao.fetchReportById(testReportId);

        assertFalse(result.isPresent());
    }

    @Test
    void fetchReportById_shouldThrowDatabaseReadExceptionOnDataAccessError() {
        doNothing().when(reportDao).verifyUserCanAccessReport(any());
        when(statementSpec.query(any(ResultSetExtractor.class)))
                .thenThrow(new DataAccessException("Database error") {});

        assertThrows(DatabaseFetchException.class, () -> reportDao.fetchReportById(testReportId));
    }

    @Test
    void fetchReports_shouldReturnCollectionOfReports() {
        List<String> roles = List.of(REP000, RECONCILIATION);
        when(securityUtils.extractRoles()).thenReturn(roles);
        var expectedReports = Arrays.asList(testReport, ReportsTestDataFactory.createTestReport());
        when(statementSpec.query(any(ResultSetExtractor.class))).thenReturn(expectedReports);

        var result = reportDao.fetchReports();

        assertEquals(2, result.size());
        assertTrue(result.contains(testReport));
        verify(statementSpec).query(any(ResultSetExtractor.class));
    }

    @Test
    void fetchReports_shouldReturnEmptyCollectionWhenNoReportsFound() {
        when(statementSpec.query(any(ResultSetExtractor.class))).thenReturn(Collections.emptyList());

        var result = reportDao.fetchReports();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(statementSpec).query(any(ResultSetExtractor.class));
    }


    @Test
    void fetchReports_shouldThrowDatabaseFetchExceptionOnDataAccessError() {
        when(statementSpec.query(any(ResultSetExtractor.class)))
                .thenThrow(new DataAccessException("Database error") {});
        assertThrows(DatabaseFetchException.class, () -> reportDao.fetchReports());
    }


    @Test
    void fetchReports_shouldNotThrowReportIdNotFoundException() {
        when(statementSpec.query(any(ResultSetExtractor.class))).thenReturn(Collections.emptyList());
        assertDoesNotThrow(() -> reportDao.fetchReports());
    }

    @Test
    void verifyUserCanAccessReport_whenAuthorized_shouldNotThrow() {
        List<String> userRoles = List.of(REP000);
        List<String> requiredRoles = List.of(REP000);
        when(securityUtils.extractRoles()).thenReturn(userRoles);
        doAnswer(invocation -> {
            RowCallbackHandler handler = invocation.getArgument(0);
            ResultSet rs = mock(ResultSet.class);
            when(rs.getString("ROLE_NAME")).thenReturn(requiredRoles.get(0));
            handler.processRow(rs);
            return null;
        }).when(statementSpec).query(any(RowCallbackHandler.class));
        when(securityUtils.isAuthorized(userRoles, requiredRoles))
                .thenReturn(true);
        assertDoesNotThrow(() -> reportDao.verifyUserCanAccessReport(testReportId));
    }

    @Test
    void verifyUserCanAccessReport_whenNotAuthorized_shouldThrowAccessDenied() {
        List<String> userRoles = List.of(REP000);
        List<String> requiredRoles = List.of(RECONCILIATION);
        when(securityUtils.extractRoles()).thenReturn(userRoles);
        doAnswer(invocation -> {
            RowCallbackHandler handler = invocation.getArgument(0);
            ResultSet rs = mock(ResultSet.class);
            when(rs.getString("ROLE_NAME")).thenReturn(requiredRoles.get(0));
            handler.processRow(rs);
            return null;
        }).when(statementSpec).query(any(RowCallbackHandler.class));
        when(securityUtils.isAuthorized(userRoles, requiredRoles))
                .thenReturn(false);
        assertThrows(ReportAccessException.class,
                () -> reportDao.verifyUserCanAccessReport(testReportId));
    }
}
