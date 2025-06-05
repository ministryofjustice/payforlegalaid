package uk.gov.laa.gpfd.dao.support;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import uk.gov.laa.gpfd.services.excel.editor.CellValueSetter;

public class DirectToExcelRowMapper implements RowCallbackHandler {
    private final Sheet sheet;
    private final Map<String, Integer> columnMapping;
    private final CellValueSetter cellValueSetter;
    private int rowNum = 1;

    public DirectToExcelRowMapper(Sheet sheet,
                                    List<Pair<String, String>> fieldAttributes,
                                    CellValueSetter cellValueSetter) {
        this.sheet = sheet;
        this.cellValueSetter = cellValueSetter;
        this.columnMapping = createColumnMapping(sheet, fieldAttributes);
    }

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        Row row = sheet.createRow(rowNum++);
        ResultSetMetaData metaData = rs.getMetaData();

        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnLabel(i);
            if (columnMapping.containsKey(columnName)) {
                Cell cell = row.createCell(columnMapping.get(columnName));
                Object value = rs.getObject(i);
                cellValueSetter.setCellValue(cell, value != null ? value : "");
            }
        }
    }

    private Map<String, Integer> createColumnMapping(Sheet sheet, List<Pair<String, String>> fieldAttributes) {
        Map<String, Integer> mapping = new HashMap<>();
        Row headerRow = sheet.getRow(0);

        for (Pair<String, String> pair : fieldAttributes) {
            for (Cell headerCell : headerRow) {
                if (pair.getValue().equals(headerCell.getStringCellValue())) {
                    mapping.put(pair.getKey(), headerCell.getColumnIndex());
                    break;
                }
            }
        }
        return mapping;
    }
}
