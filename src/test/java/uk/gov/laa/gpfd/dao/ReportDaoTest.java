package uk.gov.laa.gpfd.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import uk.gov.laa.gpfd.dao.support.ReportWithQueriesAndFieldAttributesExtractor;
import uk.gov.laa.gpfd.data.ReportsTestDataFactory;
import uk.gov.laa.gpfd.exception.ReportAccessException;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.utils.SecurityUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.spy;
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

    @Mock
    SecurityUtils securityUtils;

    @Mock
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private UUID testReportId;
    private Report testReport;

    @BeforeEach
    void setUp() {
        testReportId = UUID.randomUUID();
        testReport =  ReportsTestDataFactory.createTestReport(testReportId);
        reportDao = spy(new ReportDao(extractor, readOnlyJdbcTemplate, namedParameterJdbcTemplate, securityUtils));
    }

    @Test
    void fetchReportById_shouldReturnReportWhenFound() {
        doNothing().when(reportDao).verifyUserCanAccessReport(any());
        when(readOnlyJdbcTemplate.query(anyString(), any(ReportWithQueriesAndFieldAttributesExtractor.class), any()))
                .thenReturn(Collections.singletonList(testReport));

        var result = reportDao.fetchReportById(testReportId);

        assertTrue(result.isPresent());
        assertEquals(testReportId, result.get().getId());
        verify(readOnlyJdbcTemplate).query(anyString(), any(ReportWithQueriesAndFieldAttributesExtractor.class), eq(testReportId.toString()));
    }

    @Test
    void fetchReportById_shouldReturnEmptyOptionalWhenReportNotFound() {
        doNothing().when(reportDao).verifyUserCanAccessReport(any());
        when(readOnlyJdbcTemplate.query(anyString(), any(ReportWithQueriesAndFieldAttributesExtractor.class), any()))
                .thenReturn(Collections.emptyList());

        var result = reportDao.fetchReportById(testReportId);

        assertFalse(result.isPresent());
    }

    @Test
    void fetchReportById_shouldThrowDatabaseReadExceptionOnDataAccessError() {
        doNothing().when(reportDao).verifyUserCanAccessReport(any());
        when(readOnlyJdbcTemplate.query(anyString(), any(ReportWithQueriesAndFieldAttributesExtractor.class), any()))
                .thenThrow(new DataAccessException("Database error") {});

        assertThrows(DatabaseFetchException.class, () -> reportDao.fetchReportById(testReportId));
    }

    @Test
    void fetchReports_shouldReturnCollectionOfReports() {
        List<String> roles = List.of("REP000", "Reconciliation");
        when(securityUtils.extractRoles()).thenReturn(roles);
        var expectedReports = Arrays.asList(testReport,  ReportsTestDataFactory.createTestReport());
        when(namedParameterJdbcTemplate.query(eq(ReportDao.SELECT_ALL_REPORTS_SQL),
                eq(Map.of("roles", roles)),
                eq(extractor)))
                .thenReturn(expectedReports);

        var result = reportDao.fetchReports();

        assertEquals(2, result.size());
        assertTrue(result.contains(testReport));
        verify(namedParameterJdbcTemplate).query(anyString(), anyMap(), eq(extractor));
    }

    @Test
    void fetchReports_shouldReturnEmptyCollectionWhenNoReportsFound() {
        when(namedParameterJdbcTemplate.query(
                eq(ReportDao.SELECT_ALL_REPORTS_SQL),
                anyMap(),
                any(ReportWithQueriesAndFieldAttributesExtractor.class)
        )).thenReturn(Collections.emptyList());

        var result = reportDao.fetchReports();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(namedParameterJdbcTemplate).query(
                eq(ReportDao.SELECT_ALL_REPORTS_SQL),
                anyMap(),
                any(ReportWithQueriesAndFieldAttributesExtractor.class)
        );
    }


    @Test
    void fetchReports_shouldThrowDatabaseFetchExceptionOnDataAccessError() {
        when(namedParameterJdbcTemplate.query(
                eq(ReportDao.SELECT_ALL_REPORTS_SQL),
                anyMap(),
                any(ReportWithQueriesAndFieldAttributesExtractor.class)
        )).thenThrow(new DataAccessException("Database error") {});
        assertThrows(DatabaseFetchException.class, () -> reportDao.fetchReports());
    }


    @Test
    void fetchReports_shouldNotThrowReportIdNotFoundException() {
        when(namedParameterJdbcTemplate.query(
                eq(ReportDao.SELECT_ALL_REPORTS_SQL),
                anyMap(),
                any(ReportWithQueriesAndFieldAttributesExtractor.class)
        )).thenReturn(Collections.emptyList());
        assertDoesNotThrow(() -> reportDao.fetchReports());
    }

    @Test
    void verifyUserCanAccessReport_whenAuthorized_shouldNotThrow() {
        List<String> userRoles = List.of("REP000");
        List<String> requiredRoles = List.of("REP000");
        when(securityUtils.extractRoles()).thenReturn(userRoles);
        when(readOnlyJdbcTemplate.query(
                anyString(), any(RowMapper.class),
                eq(testReportId.toString()) )).thenReturn(requiredRoles);
        when(securityUtils.isAuthorized(userRoles, requiredRoles))
                .thenReturn(true);
        assertDoesNotThrow(() -> reportDao.verifyUserCanAccessReport(testReportId));
    }

    @Test
    void verifyUserCanAccessReport_whenNotAuthorized_shouldThrowAccessDenied() {
        List<String> userRoles = List.of("REP000");
        List<String> requiredRoles = List.of("Reconciliation");
        when(securityUtils.extractRoles()).thenReturn(userRoles);
        when(readOnlyJdbcTemplate.query( anyString(),
                any(RowMapper.class),
                eq(testReportId.toString()) ))
                .thenReturn(requiredRoles);
        when(securityUtils.isAuthorized(userRoles,
                requiredRoles))
                .thenReturn(false);
        assertThrows(ReportAccessException.class,
                () -> reportDao.verifyUserCanAccessReport(testReportId) );
    }
}
