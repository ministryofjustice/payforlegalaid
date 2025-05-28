package uk.gov.laa.gpfd.dao.sql;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.laa.gpfd.dao.sql.ChannelRowHandler.SheetChannelRowHandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SheetChannelRowHandlerTest {

    private Workbook workbook;
    private Sheet sheet;
    private SheetChannelRowHandler handler;
    private ResultSet resultSet;
    private ResultSetMetaData metaData;

    @BeforeEach
    void setUp() throws SQLException {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("TestSheet");

        var headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Name");
        headerRow.createCell(1).setCellValue("Age");
        headerRow.createCell(2).setCellValue("Email");

        var projection = new HashMap<String, Integer>();
        projection.put("name", 0);
        projection.put("age", 1);

        handler = new SheetChannelRowHandler(sheet, projection);

        resultSet = mock(ResultSet.class);
        metaData = mock(ResultSetMetaData.class);
        when(resultSet.getMetaData()).thenReturn(metaData);
    }

    @AfterEach
    void tearDown() throws Exception {
        workbook.close();
    }

    @Test
    void processRow_shouldCreateRowWithMappedColumns() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(3);
        when(metaData.getColumnLabel(1)).thenReturn("name");
        when(metaData.getColumnLabel(2)).thenReturn("age");
        when(metaData.getColumnLabel(3)).thenReturn("unmapped_column");

        when(resultSet.getObject(1)).thenReturn("John Doe");
        when(resultSet.getObject(2)).thenReturn(30);
        when(resultSet.getObject(3)).thenReturn("unmapped_value");

        handler.processRow(resultSet);

        var createdRow = sheet.getRow(1);
        assertNotNull(createdRow, "Row should be created");

        assertEquals("John Doe", createdRow.getCell(0).getStringCellValue());
        assertEquals(30, createdRow.getCell(1).getNumericCellValue());
        assertNull(createdRow.getCell(2), "Unmapped column should not be written");
    }

    @Test
    void processRow_shouldHandleNullValues() throws SQLException {
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnLabel(1)).thenReturn("name");
        when(metaData.getColumnLabel(2)).thenReturn("age");

        when(resultSet.getObject(1)).thenReturn(null);
        when(resultSet.getObject(2)).thenReturn(null);

        handler.processRow(resultSet);

        var createdRow = sheet.getRow(1);
        assertEquals("", createdRow.getCell(0).getStringCellValue());
        assertEquals("", createdRow.getCell(1).getStringCellValue());
    }

    @Test
    void processRow_shouldHandleSQLException() throws SQLException {
        when(resultSet.getMetaData()).thenThrow(new SQLException("Test exception"));

        assertThrows(SQLException.class, () -> handler.processRow(resultSet));
    }

    @Test
    void close_shouldDoNothing() {
        assertDoesNotThrow(() -> handler.close());
    }
}
