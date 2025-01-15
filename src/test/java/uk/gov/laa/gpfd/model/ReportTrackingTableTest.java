package uk.gov.laa.gpfd.model;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportTrackingTableTest {

    private static final UUID id = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");

    @Test
    void testReportTrackingTableCreationWithValidData() {
        // Given
        var creationTime = Timestamp.from(Instant.now());
        var table = new ReportTrackingTable(
                id, "ReportName", "http://report.url", creationTime, id, "GeneratedBy"
        );

        // When
        // Then
        assertNotNull(table);
        assertEquals(id, table.id());
        assertEquals("ReportName", table.reportName());
        assertEquals("http://report.url", table.reportUrl());
        assertEquals(id, table.mappingId());
        assertEquals("GeneratedBy", table.reportGeneratedBy());
        assertEquals(creationTime, table.creationTime());
    }

    @Test
    void testReportTrackingTableImmutableProperties() {
        // Given
        var creationTime = Timestamp.from(Instant.now());
        var table = new ReportTrackingTable(
                id, "ReportName", "http://report.url", creationTime, id, "GeneratedBy"
        );

        // When
        // Then
        assertThrows(IllegalAccessException.class, () -> {
            table.getClass().getDeclaredField("id").set(table, id);
        });
    }

    @Test
    void testReportTrackingTableWithEmptyStrings() {
        // Given
        var creationTime = Timestamp.from(Instant.now());
        var table = new ReportTrackingTable(
                id, "", "", creationTime, id, ""
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
                id, null, null, null, id, null
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
                id, "ReportName", "http://report.url", creationTime, id, "GeneratedBy"
        );
        var table2 = new ReportTrackingTable(
                id, "ReportName", "http://report.url", creationTime, id, "GeneratedBy"
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
                id, "Report1", "http://url1", creationTime, id, "GeneratedBy1"
        );
        var table2 = new ReportTrackingTable(
                id, "Report2", "http://url2", creationTime, id, "GeneratedBy2"
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
                id, "ReportName", "http://report.url", creationTime, id, "GeneratedBy"
        );
        var table2 = new ReportTrackingTable(
                id, "ReportName", "http://report.url", creationTime, id, "GeneratedBy"
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
                id, "ReportName", "http://report.url", creationTime, id, "GeneratedBy"
        );
        var expected = "ReportTrackingTable[id=0d4da9ec-b0b3-4371-af10-f375330d85d1, reportName=ReportName, " +
                "reportUrl=http://report.url, creationTime=" + creationTime +
                ", mappingId=0d4da9ec-b0b3-4371-af10-f375330d85d1, reportGeneratedBy=GeneratedBy]";

        // When
        // Then
        assertEquals(expected, table.toString());
    }

    @Test
    void testReportTrackingTableWithNullCreationTime() {
        // Given
        var table = new ReportTrackingTable(
                id, "ReportName", "http://report.url", null, id, "GeneratedBy"
        );

        // When
        // Then
        assertNull(table.creationTime());
    }

    @Test
    void testReportTrackingTableWithLongReportName() {
        // Given
        var longReportName = "a".repeat(1000); // Generate a string of 1000 characters
        var creationTime = Timestamp.from(Instant.now());
        var table = new ReportTrackingTable(
                id, longReportName, "http://url", creationTime, id, "GeneratedBy"
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
                id, "ReportName", "invalid-url", creationTime, id, "GeneratedBy"
        );

        // When
        // Then
        assertEquals("invalid-url", table.reportUrl());
    }

    @Test
    void testReportTrackingTableWithInvalidTimestamp() {
        // Given
        var invalidTimestamp = Timestamp.valueOf("2024-11-24 25:00:00"); // Invalid timestamp
        var table = new ReportTrackingTable(
                id, "ReportName", "http://url", invalidTimestamp, id, "GeneratedBy"
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
                id, "ReportName", "http://url", creationTime, id, null
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
                id, "Report@#%", "http://url", creationTime, id, "GeneratedBy"
        );

        // When
        // Then
        assertEquals("Report@#%", table.reportName());
    }

    @Test
    void testReportTrackingTableWithGeneratedByHavingSpecialCharacters() {
        // Given
        var creationTime = Timestamp.from(Instant.now());
        var table = new ReportTrackingTable(
                id, "ReportName", "http://url", creationTime, id, "GeneratedBy@#%"
        );

        // When
        // Then
        assertEquals("GeneratedBy@#%", table.reportGeneratedBy());
    }
}