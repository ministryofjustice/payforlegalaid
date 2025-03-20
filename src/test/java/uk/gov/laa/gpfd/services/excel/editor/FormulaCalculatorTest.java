package uk.gov.laa.gpfd.services.excel.editor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

class FormulaCalculatorTest implements FormulaCalculator {

    @Test
    void shouldEvaluateAllFormulaCells_ValidWorkbook() {
        // Given
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet("Sheet1");
        var row = sheet.createRow(0);
        var formulaCell = row.createCell(0);
        formulaCell.setCellFormula("1+1");

        // When
        evaluateAllFormulaCells(workbook, "Sheet1");

        // Then
        assertEquals(2.0, formulaCell.getNumericCellValue());
    }

    @Test
    void shouldEvaluateAllFormulaCells_NullWorkbook() {
        // Given

        // When
        var exception = assertThrows(IllegalArgumentException.class, () -> evaluateAllFormulaCells(null, "Sheet1"));

        // Then
        assertEquals("Workbook cannot be null", exception.getMessage());
    }

    @Test
    void shouldEvaluateAllFormulaCellsWhenNoSheetNamesProvided() {
        // Given
        var workbook = new XSSFWorkbook();

        // When
        var exception = assertThrows(IllegalArgumentException.class, () -> evaluateAllFormulaCells(workbook));

        // Then
        assertEquals("At least one sheet name must be provided", exception.getMessage());
    }

    @Test
    void shouldEvaluateAllFormulaCellsWhenSheetNotFound() {
        // Given
        var workbook = new XSSFWorkbook();

        // When
        var exception = assertThrows(IllegalArgumentException.class, () -> evaluateAllFormulaCells(workbook, "NonExistentSheet"));

        // Then
        assertEquals("Sheet 'NonExistentSheet' not found", exception.getMessage());
    }

    @Test
    void shouldEvaluateAllFormulaCellsWereNonFormulaCellsIgnored() {
        // Given
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet("Sheet1");
        var row = sheet.createRow(0);
        var nonFormulaCell = row.createCell(0);
        nonFormulaCell.setCellValue("Hello");

        // When
        evaluateAllFormulaCells(workbook, "Sheet1");

        // Then
        assertEquals("Hello", nonFormulaCell.getStringCellValue());
    }

    @Test
    void shouldEvaluateAllFormulaCellsInMultipleSheets() {
        // Given
        var workbook = new XSSFWorkbook();
        var sheet1 = workbook.createSheet("Sheet1");
        var sheet2 = workbook.createSheet("Sheet2");

        var row1 = sheet1.createRow(0);
        var formulaCell1 = row1.createCell(0);
        formulaCell1.setCellFormula("1+1");

        var row2 = sheet2.createRow(0);
        var formulaCell2 = row2.createCell(0);
        formulaCell2.setCellFormula("2+2");

        // When
        evaluateAllFormulaCells(workbook, "Sheet1", "Sheet2");

        // Then
        assertEquals(2.0, formulaCell1.getNumericCellValue());
        assertEquals(4.0, formulaCell2.getNumericCellValue());
    }

}