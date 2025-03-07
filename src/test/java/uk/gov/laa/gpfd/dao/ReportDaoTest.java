package uk.gov.laa.gpfd.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.laa.gpfd.dao.support.ReportWithQueriesAndFieldAttributesExtractor;
import uk.gov.laa.gpfd.model.Report;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReportDaoTest {

    @Mock
    private JdbcTemplate readOnlyJdbcTemplate;

    @Mock
    private ReportWithQueriesAndFieldAttributesExtractor extractor;

    @InjectMocks
    private ReportDao reportDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldFetchReportById() {
        // Given
        var reportId = UUID.randomUUID();
        var expectedReport = new Report();
        when(readOnlyJdbcTemplate.query(anyString(), eq(extractor), eq(reportId.toString())))
                .thenReturn(Collections.singletonList(expectedReport));

        // When
        var result = reportDao.fetchReportById(reportId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedReport, result.get());
        verify(readOnlyJdbcTemplate).query(anyString(), eq(extractor), eq(reportId.toString()));
    }

    @Test
    void shouldReturnEmptyOptionalWhenReportNotFound() {
        // Given
        var reportId = UUID.randomUUID();
        when(readOnlyJdbcTemplate.query(anyString(), eq(extractor), eq(reportId.toString())))
                .thenReturn(Collections.emptyList());

        // When
        var result = reportDao.fetchReportById(reportId);

        // Then
        assertFalse(result.isPresent());
        verify(readOnlyJdbcTemplate).query(anyString(), eq(extractor), eq(reportId.toString()));
    }

    @Test
    void shouldThrowRuntimeExceptionWhenDataAccessExceptionOccurs() {
        // Given
        var reportId = UUID.randomUUID();
        when(readOnlyJdbcTemplate.query(anyString(), eq(extractor), eq(reportId.toString())))
                .thenThrow(new DataAccessException("Database error") {});

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportDao.fetchReportById(reportId);
        });

        assertEquals("Error fetching report by ID: " + reportId, exception.getMessage());
        verify(readOnlyJdbcTemplate).query(anyString(), eq(extractor), eq(reportId.toString()));
    }
}