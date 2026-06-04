package uk.gov.laa.gpfd.services.excel.copier;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SheetCopierTest {

    private static final class TestSheetCopier extends SheetCopier {
        private boolean additionalFeaturesCalled;

        TestSheetCopier(Sheet sourceSheet, Sheet targetSheet) {
            super(sourceSheet, targetSheet);
        }

        @Override
        protected void copyAdditionalFeatures() {
            additionalFeaturesCalled = true;
        }
    }

    @Test
    void constructor_shouldRejectNullSourceSheet() throws IOException {
        try (var workbook = new XSSFWorkbook()) {
            var targetSheet = workbook.createSheet("Target");
            assertThrows(NullPointerException.class, () -> new TestSheetCopier(null, targetSheet));
        }
    }

    @Test
    void constructor_shouldRejectNullTargetSheet() throws IOException {
        try (var workbook = new XSSFWorkbook()) {
            var sourceSheet = workbook.createSheet("Source");
            assertThrows(NullPointerException.class, () -> new TestSheetCopier(sourceSheet, null));
        }
    }

    @Test
    void copySheet_shouldCopyContentAndInvokeAdditionalFeatures() throws IOException {
        try (var workbook = new XSSFWorkbook()) {
            var sourceSheet = workbook.createSheet("Source");
            var targetSheet = workbook.createSheet("Target");
            var sourceRow = sourceSheet.createRow(0);
            sourceRow.createCell(0).setCellValue("Test Value");

            var copier = new TestSheetCopier(sourceSheet, targetSheet);
            copier.copySheet();

            var copiedRow = targetSheet.getRow(0);
            assertEquals("Test Value", copiedRow.getCell(0).getStringCellValue());
            assertEquals(true, copier.additionalFeaturesCalled);
        }
    }
}
