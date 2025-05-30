package uk.gov.laa.gpfd.dao.support;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.laa.gpfd.model.ReportQuerySql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReportWithQueriesAndFieldAttributesExtractorTest {

    private ReportWithQueriesAndFieldAttributesExtractor extractor;
    private ResultSet resultSet;

    @BeforeEach
    void setUp() {
        extractor = new ReportWithQueriesAndFieldAttributesExtractor();
        resultSet = mock(ResultSet.class);
    }

    @Test
    void shouldExtractReportWithQueriesAndFieldAttributes() throws SQLException {
        // Given
        var reportId = UUID.randomUUID();
        var queryId = UUID.randomUUID();
        var fieldAttributeId = UUID.randomUUID();
        when(resultSet.next()).thenReturn(true, true, false); // Two rows, then end
        when(resultSet.getString("ID")).thenReturn(reportId.toString());
        when(resultSet.getString("NAME")).thenReturn("Test Report");
        when(resultSet.getString("TEMPLATE_SECURE_DOCUMENT_ID")).thenReturn("TEMPLATE_123");
        when(resultSet.getTimestamp("REPORT_CREATION_DATE")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getTimestamp("LAST_DATABASE_REFRESH_DATETIME")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getString("DESCRIPTION")).thenReturn("Test Description");
        when(resultSet.getInt("NUM_DAYS_TO_KEEP")).thenReturn(30);
        when(resultSet.getString("REPORT_CREATOR_EMAIL")).thenReturn("creator@example.com");
        when(resultSet.getString("REPORT_OWNER_ID")).thenReturn(UUID.randomUUID().toString());
        when(resultSet.getString("REPORT_OWNER_NAME")).thenReturn("Owner Name");
        when(resultSet.getString("ACTIVE")).thenReturn("Y");
        when(resultSet.getString("REPORT_OWNER_EMAIL")).thenReturn("owner@example.com");
        when(resultSet.getString("FILE_NAME")).thenReturn("report.pdf");
        when(resultSet.getString("QUERY_ID")).thenReturn(queryId.toString());
        when(resultSet.getString("QUERY")).thenReturn("SELECT * FROM ANY_REPORT.V_TABLE");
        when(resultSet.getString("TAB_NAME")).thenReturn("Sheet1");
        when(resultSet.getString("FIELD_ATTRIBUTE_ID")).thenReturn(fieldAttributeId.toString());
        when(resultSet.getString("SOURCE_NAME")).thenReturn("source_column");
        when(resultSet.getString("MAPPED_NAME")).thenReturn("mapped_column");
        when(resultSet.getString("FORMAT")).thenReturn("dd/MM/yyyy");
        when(resultSet.getString("FORMAT_TYPE")).thenReturn("DATE");
        when(resultSet.getString("EXTENSION")).thenReturn("xls");
        when(resultSet.getDouble("COLUMN_WIDTH")).thenReturn(10.5);

        // When
        var reports = extractor.extractData(resultSet);

        // Then
        assertNotNull(reports);
        assertEquals(1, reports.size());

        var report = reports.iterator().next();
        assertEquals(reportId, report.getId());
        assertEquals("Test Report", report.getName());
        assertEquals("TEMPLATE_123", report.getTemplateSecureDocumentId());
        assertEquals("Test Description", report.getDescription());
        assertEquals(30, report.getNumDaysToKeep());
        assertEquals("Owner Name", report.getReportOwnerName());
        assertEquals("owner@example.com", report.getReportOwnerEmail());
        assertEquals("report.pdf", report.getFileName());
        assertTrue(report.getActive());

        // Verify queries
        assertEquals(1, report.getQueries().size());
        var query = report.getQueries().iterator().next();
        assertEquals(queryId, query.getId());
        assertEquals(reportId, query.getReportId());
        assertEquals(ReportQuerySql.of("SELECT * FROM ANY_REPORT.V_TABLE"), query.getQuery());
        assertEquals("Sheet1", query.getSheetName());

        // Verify field attributes
        assertEquals(2, query.getFieldAttributes().size());
        var fieldAttribute = query.getFieldAttributes().iterator().next();
        assertEquals(fieldAttributeId, fieldAttribute.getId());
        assertEquals(queryId, fieldAttribute.getReportQueryId());
        assertEquals("source_column", fieldAttribute.getSourceName());
        assertEquals("mapped_column", fieldAttribute.getMappedName());
        assertEquals("dd/MM/yyyy", fieldAttribute.getFormat());
        assertEquals("DATE", fieldAttribute.getFormatType());
        assertEquals(10.5, fieldAttribute.getColumnWidth());
    }

    @Test
    void shouldHandleNullQueryId() throws SQLException {
        // Given
        var reportId = UUID.randomUUID();

        when(resultSet.next()).thenReturn(true, false); // One row, then end
        when(resultSet.getString("ID")).thenReturn(reportId.toString());
        when(resultSet.getString("NAME")).thenReturn("Test Report");
        when(resultSet.getString("TEMPLATE_SECURE_DOCUMENT_ID")).thenReturn("TEMPLATE_123");
        when(resultSet.getTimestamp("REPORT_CREATION_DATE")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getTimestamp("LAST_DATABASE_REFRESH_DATETIME")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getString("DESCRIPTION")).thenReturn("Test Description");
        when(resultSet.getInt("NUM_DAYS_TO_KEEP")).thenReturn(30);
        when(resultSet.getString("REPORT_CREATOR_NAME")).thenReturn("Creator Name");
        when(resultSet.getString("REPORT_CREATOR_EMAIL")).thenReturn("creator@example.com");
        when(resultSet.getString("REPORT_OWNER_ID")).thenReturn(UUID.randomUUID().toString());
        when(resultSet.getString("REPORT_OWNER_NAME")).thenReturn("Owner Name");
        when(resultSet.getString("ACTIVE")).thenReturn("Y");
        when(resultSet.getString("REPORT_OWNER_EMAIL")).thenReturn("owner@example.com");
        when(resultSet.getString("FILE_NAME")).thenReturn("report.pdf");
        when(resultSet.getString("EXTENSION")).thenReturn("xls");
        when(resultSet.getString("QUERY_ID")).thenReturn(null); // Null query ID

        // When
        var reports = extractor.extractData(resultSet);

        // Then
        assertNotNull(reports);
        assertEquals(1, reports.size());

        var report = reports.iterator().next();
        assertEquals(reportId, report.getId());
        assertTrue(report.getQueries().isEmpty()); // No queries should be added
    }

    @Test
    void shouldHandleNullFieldAttributeId() throws SQLException {
        // Given
        var reportId = UUID.randomUUID();
        var queryId = UUID.randomUUID();

        when(resultSet.next()).thenReturn(true, false); // One row, then end
        when(resultSet.getString("ID")).thenReturn(reportId.toString());
        when(resultSet.getString("NAME")).thenReturn("Test Report");
        when(resultSet.getString("TEMPLATE_SECURE_DOCUMENT_ID")).thenReturn("TEMPLATE_123");
        when(resultSet.getTimestamp("REPORT_CREATION_DATE")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getTimestamp("LAST_DATABASE_REFRESH_DATETIME")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getString("DESCRIPTION")).thenReturn("Test Description");
        when(resultSet.getString("EXTENSION")).thenReturn("xls");
        when(resultSet.getInt("NUM_DAYS_TO_KEEP")).thenReturn(30);
        when(resultSet.getString("REPORT_CREATOR_NAME")).thenReturn("Creator Name");
        when(resultSet.getString("REPORT_CREATOR_EMAIL")).thenReturn("creator@example.com");
        when(resultSet.getString("REPORT_OWNER_ID")).thenReturn(UUID.randomUUID().toString());
        when(resultSet.getString("REPORT_OWNER_NAME")).thenReturn("Owner Name");
        when(resultSet.getString("ACTIVE")).thenReturn("Y");
        when(resultSet.getString("REPORT_OWNER_EMAIL")).thenReturn("owner@example.com");
        when(resultSet.getString("FILE_NAME")).thenReturn("report.pdf");
        when(resultSet.getString("QUERY_ID")).thenReturn(queryId.toString());
        when(resultSet.getString("QUERY")).thenReturn("SELECT * FROM ANY_REPORT.V_TABLE");
        when(resultSet.getString("TAB_NAME")).thenReturn("Sheet1");
        when(resultSet.getString("FIELD_ATTRIBUTE_ID")).thenReturn(null); // Null field attribute ID

        // When
        var reports = extractor.extractData(resultSet);

        // Then
        assertNotNull(reports);
        assertEquals(1, reports.size());

        var report = reports.iterator().next();
        assertEquals(reportId, report.getId());
        assertEquals(1, report.getQueries().size());

        var query = report.getQueries().iterator().next();
        assertEquals(queryId, query.getId());
        assertTrue(query.getFieldAttributes().isEmpty()); // No field attributes should be added
    }

    @Test
    void shouldHandleSQLException() throws SQLException {
        // Given
        when(resultSet.next()).thenThrow(new SQLException("Database error"));

        // When & Then
        assertThrows(SQLException.class, () -> extractor.extractData(resultSet));
    }
}
