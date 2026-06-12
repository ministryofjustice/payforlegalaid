package uk.gov.laa.pfla.assertion;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

import java.util.Locale;


public class WorkbookHelper {

    public static Sheet getRequiredSheet(Workbook wb, String name) {
        Sheet s = wb.getSheet(name);
        if (s == null) throw new AssertionError("Missing sheet: " + name);
        return s;
    }

    public static String safeRender(Cell cell) {
        if (cell == null) return "<null>";
        DataFormatter fmt = new DataFormatter(Locale.UK, true);
        return fmt.formatCellValue(cell);
    }

    public static Cell getCellByAddress(Sheet sheet, String address) {
        int col = CellReference.convertColStringToIndex(address.replaceAll("\\d", ""));
        int row = Integer.parseInt(address.replaceAll("\\D", "")) - 1;
        Row sheetRow = sheet.getRow(row);
        return (sheetRow != null) ? sheetRow.getCell(col) : null;
    }
}