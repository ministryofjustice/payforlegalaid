package uk.gov.laa.gpfd.model;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MappingTableTest {

    @Test
    void testMappingTableCreationWithValidData() {
        var table = new MappingTable(
                1,
                "Report1",
                "ExcelReport.xlsx",
                "CsvName.csv",
                0,
                "SELECT * FROM TABLE",
                "http://base.url",
                "John Doe",
                "Jane Smith",
                "This is a test description",
                "owner@example.com"
        );

        assertNotNull(table);
        assertEquals(1, table.id());
        assertEquals("Report1", table.reportName());
        assertEquals("ExcelReport.xlsx", table.excelReport());
        assertEquals("CsvName.csv", table.csvName());
        assertEquals(0, table.excelSheetNum());
        assertEquals("SELECT * FROM TABLE", table.sqlQuery());
        assertEquals("http://base.url", table.baseUrl());
        assertEquals("John Doe", table.reportOwner());
        assertEquals("Jane Smith", table.reportCreator());
        assertEquals("This is a test description", table.description());
        assertEquals("owner@example.com", table.ownerEmail());
    }

    @Test
    void testMappingTableImmutableProperties() {
        var table = new MappingTable(
                1, "ImmutableTest", "Excel.xlsx", "CSV.csv", 1,
                "Query", "http://url", "Owner", "Creator", "Description", "email@example.com"
        );

        assertThrows(IllegalAccessException.class, () -> {
            table.getClass().getDeclaredField("id").set(table, 2);
        });
    }

    @Test
    void testMappingTableHandlesEmptyStrings() {
        var table = new MappingTable(
                2, "", "", "", 0, "", "", "", "", "", ""
        );

        assertEquals("", table.reportName());
        assertEquals("", table.excelReport());
        assertEquals("", table.csvName());
        assertEquals("", table.sqlQuery());
        assertEquals("", table.baseUrl());
        assertEquals("", table.reportOwner());
        assertEquals("", table.reportCreator());
        assertEquals("", table.description());
        assertEquals("", table.ownerEmail());
    }

    @Test
    void testMappingTableHandlesNullFields() {
        var table = new MappingTable(
                3, null, null, null, 0, null, null, null, null, null, null
        );

        assertNull(table.reportName());
        assertNull(table.excelReport());
        assertNull(table.csvName());
        assertNull(table.sqlQuery());
        assertNull(table.baseUrl());
        assertNull(table.reportOwner());
        assertNull(table.reportCreator());
        assertNull(table.description());
        assertNull(table.ownerEmail());
    }

    @Test
    void testMappingTableEquality() {
        var table1 = new MappingTable(
                4, "Report", "Excel.xlsx", "CSV.csv", 2, "SQL", "http://url",
                "Owner", "Creator", "Description", "email@example.com"
        );
        var table2 = new MappingTable(
                4, "Report", "Excel.xlsx", "CSV.csv", 2, "SQL", "http://url",
                "Owner", "Creator", "Description", "email@example.com"
        );

        assertEquals(table1, table2);
    }

    @Test
    void testMappingTableInequality() {
        var table1 = new MappingTable(
                5, "Report1", "Excel.xlsx", "CSV.csv", 2, "SQL", "http://url",
                "Owner", "Creator", "Description", "email@example.com"
        );
        var table2 = new MappingTable(
                6, "Report2", "Excel2.xlsx", "CSV2.csv", 3, "SQL2", "http://url2",
                "Owner2", "Creator2", "Description2", "email2@example.com"
        );

        assertNotEquals(table1, table2);
    }

    @Test
    void testMappingTableHashCode() {
        var table1 = new MappingTable(
                7, "Report", "Excel.xlsx", "CSV.csv", 2, "SQL", "http://url",
                "Owner", "Creator", "Description", "email@example.com"
        );
        var table2 = new MappingTable(
                7, "Report", "Excel.xlsx", "CSV.csv", 2, "SQL", "http://url",
                "Owner", "Creator", "Description", "email@example.com"
        );

        assertEquals(table1.hashCode(), table2.hashCode());
    }

    @Test
    void testMappingTableToString() {
        var table = new MappingTable(
                8, "Report", "Excel.xlsx", "CSV.csv", 2, "SQL", "http://url",
                "Owner", "Creator", "Description", "email@example.com"
        );

        String expected = "MappingTable[id=8, reportName=Report, excelReport=Excel.xlsx, " +
                "csvName=CSV.csv, excelSheetNum=2, sqlQuery=SQL, baseUrl=http://url, " +
                "reportOwner=Owner, reportCreator=Creator, description=Description, ownerEmail=email@example.com]";
        assertEquals(expected, table.toString());
    }

    @Test
    void testMappingTableHandlesNegativeId() {
        var table = new MappingTable(
                -1, "NegativeIdTest", "Excel.xlsx", "CSV.csv", 1,
                "Query", "http://url", "Owner", "Creator", "Description", "email@example.com"
        );

        assertEquals(-1, table.id());
    }

    @Test
    void testMappingTableWithSpecialCharacters() {
        var table = new MappingTable(
                9, "Report@#%", "Excel@#%.xlsx", "CSV@#%.csv", 1,
                "SELECT * FROM [TABLE] WHERE Name = 'Test@#%';",
                "http://base.url?param=value&another=value",
                "Owner@#%", "Creator@#%", "Description@#%", "email@#%example.com"
        );

        assertEquals("Report@#%", table.reportName());
        assertEquals("Excel@#%.xlsx", table.excelReport());
        assertEquals("CSV@#%.csv", table.csvName());
        assertEquals("SELECT * FROM [TABLE] WHERE Name = 'Test@#%';", table.sqlQuery());
        assertEquals("http://base.url?param=value&another=value", table.baseUrl());
        assertEquals("Owner@#%", table.reportOwner());
        assertEquals("Creator@#%", table.reportCreator());
        assertEquals("Description@#%", table.description());
        assertEquals("email@#%example.com", table.ownerEmail());
    }

    @Test
    void testMappingTableWithNullQuery() {
        var table = new MappingTable(
                10, "ReportName", "Excel.xlsx", "Csv.csv", 0,
                null, "http://url", "Owner", "Creator", "Description", "email@example.com"
        );

        assertNull(table.sqlQuery());
    }

    @Test
    void testMappingTableIdBoundaryValues() {
        var table1 = new MappingTable(
                Integer.MIN_VALUE, "MinID", "Excel.xlsx", "Csv.csv", 0,
                "SQL", "http://url", "Owner", "Creator", "Description", "email@example.com"
        );
        var table2 = new MappingTable(
                Integer.MAX_VALUE, "MaxID", "Excel.xlsx", "Csv.csv", 0,
                "SQL", "http://url", "Owner", "Creator", "Description", "email@example.com"
        );

        assertEquals(Integer.MIN_VALUE, table1.id());
        assertEquals(Integer.MAX_VALUE, table2.id());
    }

    @Test
    void testMappingTableExcelSheetNumBoundaryValues() {
        var table1 = new MappingTable(
                11, "Report", "Excel.xlsx", "Csv.csv", Integer.MIN_VALUE,
                "SQL", "http://url", "Owner", "Creator", "Description", "email@example.com"
        );
        var table2 = new MappingTable(
                12, "Report", "Excel.xlsx", "Csv.csv", Integer.MAX_VALUE,
                "SQL", "http://url", "Owner", "Creator", "Description", "email@example.com"
        );

        assertEquals(Integer.MIN_VALUE, table1.excelSheetNum());
        assertEquals(Integer.MAX_VALUE, table2.excelSheetNum());
    }

    @Test
    void testMappingTableOwnerEmailWithInvalidFormat() {
        var table = new MappingTable(
                13, "Report", "Excel.xlsx", "Csv.csv", 0,
                "SQL", "http://url", "Owner", "Creator", "Description", "invalid-email"
        );

        assertEquals("invalid-email", table.ownerEmail());
    }

    @Test
    void testMappingTableOwnerEmailWithEmptyString() {
        var table = new MappingTable(
                14, "Report", "Excel.xlsx", "Csv.csv", 0,
                "SQL", "http://url", "Owner", "Creator", "Description", ""
        );

        assertEquals("", table.ownerEmail());
    }
}
