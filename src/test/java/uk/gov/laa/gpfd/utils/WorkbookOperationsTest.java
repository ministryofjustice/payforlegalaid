package uk.gov.laa.gpfd.utils;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

import static java.util.Map.of;
import static java.util.stream.Stream.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static uk.gov.laa.gpfd.exception.ReportGenerationException.InvalidWorkbookTypeException;
import static uk.gov.laa.gpfd.exception.ReportGenerationException.SheetNotFoundException;

class WorkbookOperationsTest implements WorkbookOperations {

    private static final String VALID_SHEET = "TestSheet";

    private static Stream<Arguments> nullParametersProvider() throws IOException {
        try (var validSource = new XSSFWorkbook();
             var validTarget = new SXSSFWorkbook()) {

            validSource.createSheet(VALID_SHEET);

            return of(
                    Arguments.of(null, validTarget, VALID_SHEET),
                    Arguments.of(validSource, null, VALID_SHEET),
                    Arguments.of(validSource, validTarget, null)
            );
        }
    }

    @Test
    void transferSheet_happyPath() throws IOException {
        try (var source = new XSSFWorkbook(); var target = new SXSSFWorkbook()) {
            source.createSheet(VALID_SHEET);

            transferSheet(source, target, VALID_SHEET);

            assertNotNull(target.getXSSFWorkbook().getSheet(VALID_SHEET));
        }
    }

    @Test
    void transferSheet_throwsWhenSheetMissing() throws IOException {
        try (var source = new XSSFWorkbook(); var target = new SXSSFWorkbook()) {

            assertThrows(SheetNotFoundException.class, () -> transferSheet(source, target, "MissingSheet"));
        }
    }

    @Test
    void transferSheet_throwsWhenTargetNotSXSSF() throws IOException {
        try (var source = new XSSFWorkbook(); var invalidTarget = new XSSFWorkbook()) {
            source.createSheet(VALID_SHEET);

            assertThrows(InvalidWorkbookTypeException.class, () -> transferSheet(source, invalidTarget, VALID_SHEET));
        }
    }

    @Test
    void shouldHandleEmptyTemplate() {
        Workbook workbook = mock(Workbook.class);
        sortWorkbookToTemplate(workbook, new LinkedHashMap<>());

        verifyNoInteractions(workbook);
    }

    @Test
    void shouldHandleNullWorkbook() {
        var templateOrder = new LinkedHashMap<>(of(
                "Extra1", 0,
                "Extra2", 1
        ));

        assertThrows(NullPointerException.class, () -> sortWorkbookToTemplate(null, templateOrder));
    }

    @Test
    void shouldHandleNullTemplate() {
        assertThrows(NullPointerException.class, () -> sortWorkbookToTemplate(null, null));
    }

    @Test
    void shouldMaintainOriginalOrderForSheetsNotInTemplate() throws IOException {
        try (var source = new XSSFWorkbook()) {
            source.createSheet("Extra1");
            source.createSheet("Extra2");

            var templateOrder = new LinkedHashMap<>(of(
                    "Extra1", 0,
                    "Extra2", 1
            ));

            sortWorkbookToTemplate(source, templateOrder);

            assertEquals(0, source.getSheetIndex("Extra1"));
            assertEquals(1, source.getSheetIndex("Extra2"));
        }
    }

    @Test
    void shouldSortSheetsGivenExpectedOrder() throws IOException {
        try (var source = new XSSFWorkbook()) {
            source.createSheet("Extra1");
            source.createSheet("Extra2");

            var templateOrder = new LinkedHashMap<>(of(
                    "Extra1", 1,
                    "Extra2", 0
            ));


            sortWorkbookToTemplate(source, templateOrder);

            assertEquals(1, source.getSheetIndex("Extra1"));
            assertEquals(0, source.getSheetIndex("Extra2"));
        }
    }

    @ParameterizedTest
    @MethodSource("nullParametersProvider")
    void transferSheet_throwsWhenNullParams(Workbook source, Workbook target, String sheetName) throws IOException {
        try (var unusedSheet = new SXSSFWorkbook()) {

            assertThrows(NullPointerException.class, () -> transferSheet(source, target, sheetName));
        }
    }
}