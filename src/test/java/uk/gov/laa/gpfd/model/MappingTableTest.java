package uk.gov.laa.gpfd.model;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MappingTableTest {

    @Test
    void testMappingTableCreationWithValidData() {
        var table = new MappingTable(
                Optional.of(1),
                Optional.of("Report1"),
                Optional.of("ExcelReport.xlsx"),
                Optional.of("CsvName.csv"),
                Optional.of(0),
                Optional.of("SELECT * FROM TABLE"),
                Optional.of("http://base.url"),
                Optional.of("John Doe"),
                Optional.of("Jane Smith"),
                Optional.of("This is a test description"),
                Optional.of("owner@example.com")
        );

        assertNotNull(table);
        assertEquals(1, table.id().orElseThrow());
        assertEquals("Report1", table.reportName().orElseThrow());
        assertEquals("ExcelReport.xlsx", table.excelReport().orElseThrow());
        assertEquals("CsvName.csv", table.csvName().orElseThrow());
        assertEquals(0, table.excelSheetNum().orElseThrow());
        assertEquals("SELECT * FROM TABLE", table.sqlQuery().orElseThrow());
        assertEquals("http://base.url", table.baseUrl().orElseThrow());
        assertEquals("John Doe", table.reportOwner().orElseThrow());
        assertEquals("Jane Smith", table.reportCreator().orElseThrow());
        assertEquals("This is a test description", table.description().orElseThrow());
        assertEquals("owner@example.com", table.ownerEmail().orElseThrow());
    }

    @Test
    void testMappingTableImmutableProperties() throws NoSuchFieldException, IllegalAccessException {
        var table = new MappingTable(
                Optional.of(1), Optional.of("ImmutableTest"), Optional.of("Excel.xlsx"), Optional.of("CSV.csv"), Optional.of(1),
                Optional.of("Query"), Optional.of("http://url"), Optional.of("Owner"), Optional.of("Creator"), Optional.of("Description"), Optional.of("email@example.com")
        );

        var field = table.getClass().getDeclaredField("id");
        field.setAccessible(true);

        assertThrows(IllegalAccessException.class, () -> field.set(table, 2));
    }

    @Test
    void testMappingTableHandlesEmptyStrings() {
        var table = new MappingTable(
                Optional.of(2), Optional.of(""), Optional.of(""), Optional.of(""), Optional.of(0),
                Optional.of(""), Optional.of(""), Optional.of(""), Optional.of(""), Optional.of(""), Optional.of("")
        );

        assertEquals("", table.reportName().orElse(""));
        assertEquals("", table.excelReport().orElse(""));
        assertEquals("", table.csvName().orElse(""));
        assertEquals("", table.sqlQuery().orElse(""));
        assertEquals("", table.baseUrl().orElse(""));
        assertEquals("", table.reportOwner().orElse(""));
        assertEquals("", table.reportCreator().orElse(""));
        assertEquals("", table.description().orElse(""));
        assertEquals("", table.ownerEmail().orElse(""));
    }

    @Test
    void testMappingTableHandlesNullFields() {
        var table = new MappingTable(
                Optional.of(3), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(0),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
        );

        assertFalse(table.reportName().isPresent());
        assertFalse(table.excelReport().isPresent());
        assertFalse(table.csvName().isPresent());
        assertFalse(table.sqlQuery().isPresent());
        assertFalse(table.baseUrl().isPresent());
        assertFalse(table.reportOwner().isPresent());
        assertFalse(table.reportCreator().isPresent());
        assertFalse(table.description().isPresent());
        assertFalse(table.ownerEmail().isPresent());
    }

    @Test
    void testMappingTableEquality() {
        var table1 = new MappingTable(
                Optional.of(4), Optional.of("Report"), Optional.of("Excel.xlsx"), Optional.of("CSV.csv"),
                Optional.of(2), Optional.of("SQL"), Optional.of("http://url"),
                Optional.of("Owner"), Optional.of("Creator"), Optional.of("Description"), Optional.of("email@example.com")
        );
        var table2 = new MappingTable(
                Optional.of(4), Optional.of("Report"), Optional.of("Excel.xlsx"), Optional.of("CSV.csv"),
                Optional.of(2), Optional.of("SQL"), Optional.of("http://url"),
                Optional.of("Owner"), Optional.of("Creator"), Optional.of("Description"), Optional.of("email@example.com")
        );

        assertEquals(table1, table2);
    }

    @Test
    void testMappingTableInequality() {
        var table1 = new MappingTable(
                Optional.of(5), Optional.of("Report1"), Optional.of("Excel.xlsx"), Optional.of("CSV.csv"),
                Optional.of(2), Optional.of("SQL"), Optional.of("http://url"),
                Optional.of("Owner"), Optional.of("Creator"), Optional.of("Description"), Optional.of("email@example.com")
        );
        var table2 = new MappingTable(
                Optional.of(6), Optional.of("Report2"), Optional.of("Excel2.xlsx"), Optional.of("CSV2.csv"),
                Optional.of(3), Optional.of("SQL2"), Optional.of("http://url2"),
                Optional.of("Owner2"), Optional.of("Creator2"), Optional.of("Description2"), Optional.of("email2@example.com")
        );

        assertNotEquals(table1, table2);
    }

    @Test
    void testMappingTableHashCode() {
        var table1 = new MappingTable(
                Optional.of(7), Optional.of("Report"), Optional.of("Excel.xlsx"), Optional.of("CSV.csv"),
                Optional.of(2), Optional.of("SQL"), Optional.of("http://url"),
                Optional.of("Owner"), Optional.of("Creator"), Optional.of("Description"), Optional.of("email@example.com")
        );
        var table2 = new MappingTable(
                Optional.of(7), Optional.of("Report"), Optional.of("Excel.xlsx"), Optional.of("CSV.csv"),
                Optional.of(2), Optional.of("SQL"), Optional.of("http://url"),
                Optional.of("Owner"), Optional.of("Creator"), Optional.of("Description"), Optional.of("email@example.com")
        );

        assertEquals(table1.hashCode(), table2.hashCode());
    }

    @Test
    void testMappingTableHandlesNegativeId() {
        var table = new MappingTable(
                Optional.of(-1), Optional.of("NegativeIdTest"), Optional.of("Excel.xlsx"), Optional.of("CSV.csv"),
                Optional.of(1), Optional.of("Query"), Optional.of("http://url"), Optional.of("Owner"), Optional.of("Creator"),
                Optional.of("Description"), Optional.of("email@example.com")
        );

        assertEquals(-1, table.id().orElseThrow());
    }

    @Test
    void testMappingTableWithSpecialCharacters() {
        var table = new MappingTable(
                Optional.of(9),
                Optional.of("Report@#%"),
                Optional.of("Excel@#%.xlsx"),
                Optional.of("CSV@#%.csv"),
                Optional.of(1),
                Optional.of("SELECT * FROM [TABLE] WHERE Name = 'Test@#%';"),
                Optional.of("http://base.url?param=value&another=value"),
                Optional.of("Owner@#%"),
                Optional.of("Creator@#%"),
                Optional.of("Description@#%"),
                Optional.of("email@#%example.com")
        );

        assertEquals("Report@#%", table.reportName().orElseThrow());
        assertEquals("Excel@#%.xlsx", table.excelReport().orElseThrow());
        assertEquals("CSV@#%.csv", table.csvName().orElseThrow());
        assertEquals("SELECT * FROM [TABLE] WHERE Name = 'Test@#%';", table.sqlQuery().orElseThrow());
        assertEquals("http://base.url?param=value&another=value", table.baseUrl().orElseThrow());
        assertEquals("Owner@#%", table.reportOwner().orElseThrow());
        assertEquals("Creator@#%", table.reportCreator().orElseThrow());
        assertEquals("Description@#%", table.description().orElseThrow());
        assertEquals("email@#%example.com", table.ownerEmail().orElseThrow());
    }

    @Test
    void testMappingTableWithNullQuery() {
        var table = new MappingTable(
                Optional.of(10),
                Optional.of("ReportName"),
                Optional.of("Excel.xlsx"),
                Optional.of("Csv.csv"),
                Optional.of(0),
                Optional.empty(),  // Null SQL query represented as Optional.empty()
                Optional.of("http://url"),
                Optional.of("Owner"),
                Optional.of("Creator"),
                Optional.of("Description"),
                Optional.of("email@example.com")
        );

        assertFalse(table.sqlQuery().isPresent());
    }

    @Test
    void testMappingTableIdBoundaryValues() {
        var table1 = new MappingTable(
                Optional.of(Integer.MIN_VALUE),
                Optional.of("MinID"),
                Optional.of("Excel.xlsx"),
                Optional.of("Csv.csv"),
                Optional.of(0),
                Optional.of("SQL"),
                Optional.of("http://url"),
                Optional.of("Owner"),
                Optional.of("Creator"),
                Optional.of("Description"),
                Optional.of("email@example.com")
        );
        var table2 = new MappingTable(
                Optional.of(Integer.MAX_VALUE),
                Optional.of("MaxID"),
                Optional.of("Excel.xlsx"),
                Optional.of("Csv.csv"),
                Optional.of(0),
                Optional.of("SQL"),
                Optional.of("http://url"),
                Optional.of("Owner"),
                Optional.of("Creator"),
                Optional.of("Description"),
                Optional.of("email@example.com")
        );

        assertEquals(Integer.MIN_VALUE, table1.id().orElseThrow());
        assertEquals(Integer.MAX_VALUE, table2.id().orElseThrow());
    }

    @Test
    void testMappingTableExcelSheetNumBoundaryValues() {
        var table1 = new MappingTable(
                Optional.of(11),
                Optional.of("Report"),
                Optional.of("Excel.xlsx"),
                Optional.of("Csv.csv"),
                Optional.of(Integer.MIN_VALUE),
                Optional.of("SQL"),
                Optional.of("http://url"),
                Optional.of("Owner"),
                Optional.of("Creator"),
                Optional.of("Description"),
                Optional.of("email@example.com")
        );
        var table2 = new MappingTable(
                Optional.of(12),
                Optional.of("Report"),
                Optional.of("Excel.xlsx"),
                Optional.of("Csv.csv"),
                Optional.of(Integer.MAX_VALUE),
                Optional.of("SQL"),
                Optional.of("http://url"),
                Optional.of("Owner"),
                Optional.of("Creator"),
                Optional.of("Description"),
                Optional.of("email@example.com")
        );

        assertEquals(Integer.MIN_VALUE, table1.excelSheetNum().orElseThrow());
        assertEquals(Integer.MAX_VALUE, table2.excelSheetNum().orElseThrow());
    }

    @Test
    void testMappingTableOwnerEmailWithInvalidFormat() {
        var table = new MappingTable(
                Optional.of(13),
                Optional.of("Report"),
                Optional.of("Excel.xlsx"),
                Optional.of("Csv.csv"),
                Optional.of(0),
                Optional.of("SQL"),
                Optional.of("http://url"),
                Optional.of("Owner"),
                Optional.of("Creator"),
                Optional.of("Description"),
                Optional.of("invalid-email")
        );

        assertEquals("invalid-email", table.ownerEmail().orElseThrow());
    }

    @Test
    void testMappingTableOwnerEmailWithEmptyString() {
        var table = new MappingTable(
                Optional.of(14),
                Optional.of("Report"),
                Optional.of("Excel.xlsx"),
                Optional.of("Csv.csv"),
                Optional.of(0),
                Optional.of("SQL"),
                Optional.of("http://url"),
                Optional.of("Owner"),
                Optional.of("Creator"),
                Optional.of("Description"),
                Optional.of("")
        );

        assertEquals("", table.ownerEmail().orElse(""));
    }

    @Test
    void testMappingTableToString() {
        var table = new MappingTable(
                Optional.of(8),
                Optional.of("Report"),
                Optional.of("Excel.xlsx"),
                Optional.of("CSV.csv"),
                Optional.of(2),
                Optional.of("SQL"),
                Optional.of("http://url"),
                Optional.of("Owner"),
                Optional.of("Creator"),
                Optional.of("Description"),
                Optional.of("email@example.com")
        );

        String expected = "MappingTable[id=8, reportName=Report, excelReport=Excel.xlsx, " +
                "csvName=CSV.csv, excelSheetNum=2, sqlQuery=SQL, baseUrl=http://url, " +
                "reportOwner=Owner, reportCreator=Creator, description=Description, ownerEmail=email@example.com]";
        assertEquals(expected, table.toString());
    }
}
