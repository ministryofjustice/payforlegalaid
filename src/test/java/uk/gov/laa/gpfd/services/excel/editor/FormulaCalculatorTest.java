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
        evaluateAllFormulaCells(workbook);

        // Then
        assertEquals(2.0, formulaCell.getNumericCellValue());
    }

    @Test
    void shouldEvaluateAllFormulaCells_NullWorkbook() {
        // Given

        // When
        var exception = assertThrows(IllegalArgumentException.class, () -> evaluateAllFormulaCells(null));

        // Then
        assertEquals("Workbook cannot be null", exception.getMessage());
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
        evaluateAllFormulaCells(workbook);

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
        evaluateAllFormulaCells(workbook);

        // Then
        assertEquals(2.0, formulaCell1.getNumericCellValue());
        assertEquals(4.0, formulaCell2.getNumericCellValue());
    }

}