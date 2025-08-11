package uk.gov.laa.gpfd.services.excel.copier.types.basic;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SheetContentCopierTest {

    private static final BiConsumer<Sheet, Sheet> copyAll = SheetContentCopier::copyAll;

    private static XSSFWorkbook workbook;

    @Test
    void should_copy_all_cells_on_sheet() {
        workbook = new XSSFWorkbook();
        var sourceSheet = workbook.createSheet("source");
        var sourceRow1 = sourceSheet.createRow(0);
        var sourceRow2 = sourceSheet.createRow(1);
        sourceRow1.createCell(0).setCellValue(true);
        sourceRow1.createCell(1).setCellValue("hi");
        sourceRow1.createCell(3).setCellValue(234);
        sourceRow2.createCell(8).setCellValue(94.4);
        sourceRow2.createCell(4).setCellFormula("A4+B9");
        sourceRow2.createCell(3).setCellValue(LocalDate.of(2024, 3, 1));

        var targetSheet = workbook.createSheet("target");
        copyAll.accept(sourceSheet, targetSheet);

        for (var row : sourceSheet) {
            for (var cell : row) {
                var targetCell = targetSheet.getRow(cell.getRowIndex()).getCell(cell.getColumnIndex());
                // It's suffices to check the cell exists and is same type here
                // The value is written by and hence tested in the CellValueCopier handler
                assertEquals(cell.getCellType(), targetCell.getCellType());
            }
        }

    }

    @Test
    void should_copy_row_properties() {
        workbook = new XSSFWorkbook();
        var sourceSheet = workbook.createSheet("source");
        var sourceRow1 = sourceSheet.createRow(0);
        var sourceRow2 = sourceSheet.createRow(1);

        sourceRow1.setHeight((short) 12.1);
        sourceRow1.setZeroHeight(false);
        sourceRow2.setHeightInPoints(32);
        sourceRow2.setZeroHeight(true);

        var targetSheet = workbook.createSheet("target");
        copyAll.accept(sourceSheet, targetSheet);

        var targetRow1 = targetSheet.getRow(0);
        assertEquals((short) 12.1, targetRow1.getHeight());
        assertFalse(targetRow1.getZeroHeight());

        var targetRow2 = targetSheet.getRow(1);
        assertEquals(32, targetRow2.getHeightInPoints());
        assertTrue(targetRow2.getZeroHeight());

    }

    @Test
    void should_copy_cell_styles() {
        workbook = new XSSFWorkbook();
        var sourceSheet = workbook.createSheet("source");
        var sourceRow = sourceSheet.createRow(0);
        var sourceCellA1 = sourceRow.createCell(0);
        var cellStyleA1 = workbook.createCellStyle();
        var cellColour = new XSSFColor();

        cellStyleA1.setDataFormat(3);
        cellColour.setIndexed(43);
        cellStyleA1.setFillBackgroundColor(cellColour);
        sourceCellA1.setCellStyle(cellStyleA1);

        var sourceCellA2 = sourceRow.createCell(1);
        var cellStyleA2 = workbook.createCellStyle();
        cellStyleA2.setBorderBottom(BorderStyle.DASH_DOT);
        cellStyleA2.setBorderTop(BorderStyle.DOTTED);
        cellStyleA2.setBorderLeft(BorderStyle.MEDIUM);
        cellStyleA2.setBorderRight(BorderStyle.THICK);
        sourceCellA2.setCellStyle(cellStyleA2);

        var targetSheet = workbook.createSheet("target");
        copyAll.accept(sourceSheet, targetSheet);

        var targetCellA1Style = targetSheet.getRow(0).getCell(0).getCellStyle();
        assertEquals(3, targetCellA1Style.getDataFormat());
        assertEquals(cellColour, targetCellA1Style.getFillBackgroundColorColor());

        var targetCellA2Style = targetSheet.getRow(0).getCell(1).getCellStyle();
        assertEquals(BorderStyle.DASH_DOT, targetCellA2Style.getBorderBottom());
        assertEquals(BorderStyle.DOTTED, targetCellA2Style.getBorderTop());
        assertEquals(BorderStyle.MEDIUM, targetCellA2Style.getBorderLeft());
        assertEquals(BorderStyle.THICK, targetCellA2Style.getBorderRight());


    }

    @Test
    void should_copy_borders_when_underlying_field_is_set_incorrectly() {
        // Test issue whereby Apache POI cares about the value of the borderApply field
        // Even though Excel does not care and so will not set it correctly
        // So this test is that we copy the border even if POI wrongly thinks it is hidden

        workbook = new XSSFWorkbook();
        var sourceSheet = workbook.createSheet("source");
        var sourceRow = sourceSheet.createRow(0);
        var sourceCellA1 = sourceRow.createCell(0);
        var cellStyleA1 = workbook.createCellStyle();

        cellStyleA1.setBorderBottom(BorderStyle.DASH_DOT);
        cellStyleA1.setBorderTop(BorderStyle.DOTTED);
        cellStyleA1.setBorderLeft(BorderStyle.MEDIUM);
        cellStyleA1.setBorderRight(BorderStyle.THICK);
        // This is the key bit in terms of testing we are avoiding the border bug!
        cellStyleA1.getCoreXf().setApplyBorder(false);
        sourceCellA1.setCellStyle(cellStyleA1);

        var targetSheet = workbook.createSheet("target");
        copyAll.accept(sourceSheet, targetSheet);

        var targetCellA1Style = targetSheet.getRow(0).getCell(0).getCellStyle();
        assertEquals(BorderStyle.DASH_DOT, targetCellA1Style.getBorderBottom());
        assertEquals(BorderStyle.DOTTED, targetCellA1Style.getBorderTop());
        assertEquals(BorderStyle.MEDIUM, targetCellA1Style.getBorderLeft());
        assertEquals(BorderStyle.THICK, targetCellA1Style.getBorderRight());

    }

    @Test
    void should_copy_cell_widths() {
        workbook = new XSSFWorkbook();
        var sourceSheet = workbook.createSheet("source");
        sourceSheet.createRow(0).createCell(0);
        sourceSheet.createRow(0).createCell(1);

        sourceSheet.setColumnWidth(0, 12);
        sourceSheet.setColumnWidth(1, 3);

        var targetSheet = workbook.createSheet("target");
        copyAll.accept(sourceSheet, targetSheet);

        assertEquals(12, targetSheet.getColumnWidth(0));
        assertEquals(3, targetSheet.getColumnWidth(1));
    }

    @Test
    void should_copy_merged_regions() {
        workbook = new XSSFWorkbook();
        var sourceSheet = workbook.createSheet("source");

        sourceSheet.addMergedRegion(new CellRangeAddress(0, 1, 3, 4));
        sourceSheet.addMergedRegion(new CellRangeAddress(4, 6, 0, 3));

        var targetSheet = workbook.createSheet("target");
        copyAll.accept(sourceSheet, targetSheet);

        assertEquals(2, targetSheet.getNumMergedRegions());
        var targetMergedRegions = targetSheet.getMergedRegions();
        assertTrue(targetMergedRegions.containsAll(List.of(
                new CellRangeAddress(0, 1, 3, 4),
                new CellRangeAddress(4, 6, 0, 3)
        )));

    }

}
