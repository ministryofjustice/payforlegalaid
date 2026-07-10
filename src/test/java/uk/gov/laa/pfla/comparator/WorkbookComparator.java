package uk.gov.laa.pfla.comparator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;

public interface WorkbookComparator {

    default boolean areWorkbooksEqual(Workbook workbook1, Workbook workbook2) {
        if (workbook1 == null || workbook2 == null) return workbook1 == workbook2;

        if (workbook1.getNumberOfSheets() != workbook2.getNumberOfSheets()) return false;

        var workbook2Sheets = createSheetMap(workbook2);

        for (var i = 0; i < workbook1.getNumberOfSheets(); i++) {
            var sheet1 = workbook1.getSheetAt(i);
            var sheet2 = workbook2Sheets.get(sheet1.getSheetName());

            if (sheet2 == null || !areSheetsEqual(sheet1, sheet2)) return false;
        }

        return true;
    }

    private Map<String, Sheet> createSheetMap(Workbook workbook) {
        return range(0, workbook.getNumberOfSheets())
                .mapToObj(workbook::getSheetAt)
                .collect(toMap(Sheet::getSheetName, identity(), (a, b) -> b, HashMap::new));
    }

    private boolean areSheetsEqual(Sheet sheet1, Sheet sheet2) {

        var row1 = sheet1.getRow(0);
        var row2 = sheet2.getRow(0);

        if ((row1 == null) != (row2 == null)) return false;
        if ((row2 == null) && (row1 == null)) return true;
        if (Objects.requireNonNull(row1).getPhysicalNumberOfCells() != Objects.requireNonNull(row2).getPhysicalNumberOfCells()) return false;

        for (var cellIndex = 0; cellIndex <= row1.getLastCellNum(); cellIndex++) {
            var cell1 = row1.getCell(cellIndex);
            var cell2 = row2.getCell(cellIndex);

            if ((cell1 == null) != (cell2 == null)) return false;
            if (cell1 == null) continue;

            if (!areCellsEqual(cell1, cell2)) return false;
        }

        if (sheet1.getPhysicalNumberOfRows() != sheet2.getPhysicalNumberOfRows()) return false;

        return true;
    }

    private boolean areCellsEqual(Cell cell1, Cell cell2) {
        if (cell1.getCellType() != cell2.getCellType()) return false;

        return switch (cell1.getCellType()) {
            case STRING -> Objects.equals(cell1.getStringCellValue(), cell2.getStringCellValue());
            case NUMERIC -> cell1.getNumericCellValue() == cell2.getNumericCellValue();
            case BOOLEAN -> cell1.getBooleanCellValue() == cell2.getBooleanCellValue();
            case FORMULA -> Objects.equals(cell1.getCellFormula(), cell2.getCellFormula());
            case BLANK, _NONE -> true;
            default -> throw new IllegalStateException("Unsupported cell type: " + cell1.getCellType());
        };
    }

}