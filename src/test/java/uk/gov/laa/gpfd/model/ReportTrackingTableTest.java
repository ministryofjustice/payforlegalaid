package uk.gov.laa.gpfd.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportTrackingTableTest {

    @Test
    void testReportTrackingTableCreationWithValidData() {
        // Given
        var creationTime = Timestamp.from(Instant.now());
        var table = new ReportTrackingTable(
                1, "ReportName", "http://report.url", creationTime, 10, "GeneratedBy"
        );

        // When
        // Then
        assertNotNull(table);
        assertEquals(1, table.id());
        assertEquals("ReportName", table.reportName());
        assertEquals("http://report.url", table.reportUrl());
        assertEquals(10, table.mappingId());
        assertEquals("GeneratedBy", table.reportGeneratedBy());
        assertEquals(creationTime, table.creationTime());
    }

    @Test
    void testReportTrackingTableImmutableProperties() {
        // Given
        var creationTime = Timestamp.from(Instant.now());
        var table = new ReportTrackingTable(
                2, "ReportName", "http://report.url", creationTime, 15, "GeneratedBy"
        );

        // When
        // Then
        assertThrows(IllegalAccessException.class, () -> {
            table.getClass().getDeclaredField("id").set(table, 2);
        });
    }

    @Test
    void testReportTrackingTableWithEmptyStrings() {
        // Given
        var creationTime = Timestamp.from(Instant.now());
        var table = new ReportTrackingTable(
                3, "", "", creationTime, 0, ""
        );

        // When
        // Then
        assertEquals("", table.reportName());
        assertEquals("", table.reportUrl());
        assertEquals("", table.reportGeneratedBy());
    }

    @Test
    void testReportTrackingTableWithNullFields() {
        // Given
        var table = new ReportTrackingTable(
                4, null, null, null, 0, null
        );

        // When
        // Then
        assertNull(table.reportName());
        assertNull(table.reportUrl());
        assertNull(table.reportGeneratedBy());
        assertNull(table.creationTime());
    }

    @Test
    void testReportTrackingTableEquality() {
        // Given
        var creationTime = Timestamp.from(Instant.now());
        var table1 = new ReportTrackingTable(
                5, "ReportName", "http://report.url", creationTime, 20, "GeneratedBy"
        );
        var table2 = new ReportTrackingTable(
                5, "ReportName", "http://report.url", creationTime, 20, "GeneratedBy"
        );

        // When
        // Then
        assertEquals(table1, table2);
    }

    @Test
    void testReportTrackingTableInequality() {
        // Given
        var creationTime = Timestamp.from(Instant.now());
        var table1 = new ReportTrackingTable(
                6, "Report1", "http://url1", creationTime, 25, "GeneratedBy1"
        );
        var table2 = new ReportTrackingTable(
                7, "Report2", "http://url2", creationTime, 30, "GeneratedBy2"
        );

        // When
        // Then
        assertNotEquals(table1, table2);
    }

    @Test
    void testReportTrackingTableHashCode() {
        // Given
        var creationTime = Timestamp.from(Instant.now());
        var table1 = new ReportTrackingTable(
                8, "ReportName", "http://report.url", creationTime, 35, "GeneratedBy"
        );
        var table2 = new ReportTrackingTable(
                8, "ReportName", "http://report.url", creationTime, 35, "GeneratedBy"
        );

        // When
        // Then
        assertEquals(table1.hashCode(), table2.hashCode());
    }

    @Test
    void testReportTrackingTableToString() {
        // Given
        var creationTime = Timestamp.from(Instant.now());
        var table = new ReportTrackingTable(
                9, "ReportName", "http://report.url", creationTime, 40, "GeneratedBy"
        );
        var expected = "ReportTrackingTable[id=9, reportName=ReportName, " +
                "reportUrl=http://report.url, creationTime=" + creationTime +
                ", mappingId=40, reportGeneratedBy=GeneratedBy]";

        // When
        // Then
        assertEquals(expected, table.toString());
    }

    @Test
    void testReportTrackingTableWithNullCreationTime() {
        // Given
        var table = new ReportTrackingTable(
                10, "ReportName", "http://report.url", null, 45, "GeneratedBy"
        );

        // When
        // Then
        assertNull(table.creationTime());
    }

    @Test
    void testReportTrackingTableIdBoundaryValues() {
        // Given
        var creationTime = Timestamp.from(Instant.now());
        var table1 = new ReportTrackingTable(
                Integer.MIN_VALUE, "MinIDReport", "http://url", creationTime, 50, "GeneratedBy"
        );
        var table2 = new ReportTrackingTable(
                Integer.MAX_VALUE, "MaxIDReport", "http://url", creationTime, 60, "GeneratedBy"
        );

        // When
        // Then
        assertEquals(Integer.MIN_VALUE, table1.id());
        assertEquals(Integer.MAX_VALUE, table2.id());
    }

    @Test
    void testReportTrackingTableMappingIdBoundaryValues() {
        // Given
        var creationTime = Timestamp.from(Instant.now());
        var table1 = new ReportTrackingTable(
                11, "ReportName", "http://url", creationTime, Integer.MIN_VALUE, "GeneratedBy"
        );
        var table2 = new ReportTrackingTable(
                12, "ReportName", "http://url", creationTime, Integer.MAX_VALUE, "GeneratedBy"
        );

        // When
        // Then
        assertEquals(Integer.MIN_VALUE, table1.mappingId());
        assertEquals(Integer.MAX_VALUE, table2.mappingId());
    }

    @Test
    void testReportTrackingTableWithLongReportName() {
        // Given
        var longReportName = "a".repeat(1000); // Generate a string of 1000 characters
        var creationTime = Timestamp.from(Instant.now());
        var table = new ReportTrackingTable(
                13, longReportName, "http://url", creationTime, 70, "GeneratedBy"
        );

        // When
        // Then
        assertEquals(longReportName, table.reportName());
    }

    @Test
    void testReportTrackingTableWithInvalidReportUrl() {
        // Given
        var creationTime = Timestamp.from(Instant.now());
        var table = new ReportTrackingTable(
                14, "ReportName", "invalid-url", creationTime, 80, "GeneratedBy"
        );

        // When
        // Then
        assertEquals("invalid-url", table.reportUrl());
    }

    @Test
    void testReportTrackingTableWithEmptyReportUrl() {
        // Given
        var creationTime = Timestamp.from(Instant.now());
        var table = new ReportTrackingTable(
                15, "ReportName", "", creationTime, 90, "GeneratedBy"
        );

        // When
        // Then
        assertEquals("", table.reportUrl());
    }

    @Test
    void testReportTrackingTableWithInvalidTimestamp() {
        // Given
        var invalidTimestamp = Timestamp.valueOf("2024-11-24 25:00:00"); // Invalid timestamp
        var table = new ReportTrackingTable(
                16, "ReportName", "http://url", invalidTimestamp, 100, "GeneratedBy"
        );

        // When
        // Then
        assertEquals(invalidTimestamp, table.creationTime());
    }

    @Test
    void testReportTrackingTableWithNullGeneratedBy() {
        // Given
        var creationTime = Timestamp.from(Instant.now());
        var table = new ReportTrackingTable(
                17, "ReportName", "http://url", creationTime, 110, null
        );

        // When
        // Then
        assertNull(table.reportGeneratedBy());
    }

    @Test
    void testReportTrackingTableWithSpecialCharactersInReportName() {
        // Given
        var creationTime = Timestamp.from(Instant.now());
        var table = new ReportTrackingTable(
                18, "Report@#%", "http://url", creationTime, 120, "GeneratedBy"
        );

        // When
        // Then
        assertEquals("Report@#%", table.reportName());
    }

    @Test
    void testReportTrackingTableWithSpecialCharactersInReportUrl() {
        // Given
        var creationTime = Timestamp.from(Instant.now());
        var table = new ReportTrackingTable(
                19, "ReportName", "http://url?param=value&another=value", creationTime, 130, "GeneratedBy"
        );

        // When
        // Then
        assertEquals("http://url?param=value&another=value", table.reportUrl());
    }

    @Test
    void testReportTrackingTableWithGeneratedByHavingSpecialCharacters() {
        // Given
        var creationTime = Timestamp.from(Instant.now());
        var table = new ReportTrackingTable(
                20, "ReportName", "http://url", creationTime, 140, "GeneratedBy@#%"
        );

        // When
        // Then
        assertEquals("GeneratedBy@#%", table.reportGeneratedBy());
    }
}