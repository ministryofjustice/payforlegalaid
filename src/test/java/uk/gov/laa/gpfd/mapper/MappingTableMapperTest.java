package uk.gov.laa.gpfd.mapper;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MappingTableMapperTest {

    @Test
    public void shouldMapToMappingTable() {
        // Given
        Map<String, Object> map = new HashMap<>(){{
            put("ID", new BigDecimal(1));
            put("REPORT_NAME", "Test Report");
            put("SQL_QUERY", "SELECT * FROM TEST");
            put("BASE_URL", "https://example.com");
            put("REPORT_OWNER", "John Doe");
            put("REPORT_CREATOR", "Jane Smith");
            put("REPORT_DESCRIPTION", "Test Report Description");
            put("EXCEL_REPORT", "Test Excel Report");
            put("EXCEL_SHEET_NUM", new BigDecimal(2));
            put("CSV_NAME", "test_report.csv");
            put("OWNER_EMAIL", "john.doe@example.com");
        }};


        // When
        var result = MappingTableMapper.mapToMappingTable(map);

        // Then
        assertEquals(1, result.id());
        assertEquals("Test Report", result.reportName());
        assertEquals("SELECT * FROM TEST", result.sqlQuery());
        assertEquals("https://example.com", result.baseUrl());
        assertEquals("John Doe", result.reportOwner());
        assertEquals("Jane Smith", result.reportCreator());
        assertEquals("Test Report Description", result.description());
        assertEquals("Test Excel Report", result.excelReport());
        assertEquals(2, result.excelSheetNum());
        assertEquals("test_report.csv", result.csvName());
        assertEquals("john.doe@example.com", result.ownerEmail());
    }

}