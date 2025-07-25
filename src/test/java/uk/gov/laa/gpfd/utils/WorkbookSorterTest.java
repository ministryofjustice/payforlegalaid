package uk.gov.laa.gpfd.utils;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import uk.gov.laa.gpfd.services.excel.workbook.ReportWorkbook;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.utils.WorkbookSorter.create;
import static uk.gov.laa.gpfd.utils.WorkbookSorter.defaultSorter;
import static uk.gov.laa.gpfd.utils.WorkbookSorter.standardPreprocessor;

class WorkbookSorterTest {

    @Test
    void create_shouldUseCustomSorter() {
        var workbook = mock(Workbook.class);
        var entry = Map.entry("Test", 0);
        var template = new LinkedHashMap<String, Integer>() {{
            put(entry.getKey(), entry.getValue());
        }};

        BiConsumer<Workbook, Map.Entry<String, Integer>> sorter = mock(BiConsumer.class);

        create(Function.identity(), sorter).sort(workbook, template);

        verify(sorter).accept(workbook, entry);
    }

    @Test
    void standardPreprocessor_shouldHandleSXSSFWorkbook() {
        var sxssf = mock(SXSSFWorkbook.class);
        var xssf = mock(XSSFWorkbook.class);
        when(sxssf.getXSSFWorkbook()).thenReturn(xssf);

        var result = standardPreprocessor().apply(sxssf);

        assertSame(xssf, result);
    }

    @Test
    void standardPreprocessor_shouldHandleReportWorkbook() {
        var report = mock(ReportWorkbook.class);
        var xssf = mock(XSSFWorkbook.class);
        when(report.getXSSFWorkbook()).thenReturn(xssf);

        var result = standardPreprocessor().apply(report);

        assertSame(xssf, result);
    }

    @Test
    void standardPreprocessor_shouldIgnoreUnknownWorkbookTypes() {
        var unknown = mock(Workbook.class);

        var result = standardPreprocessor().apply(unknown);

        assertSame(unknown, result);
    }

    @Test
    void sort_shouldSkipMissingSheets() {
        var workbook = mock(Workbook.class);
        when(workbook.getSheet("Missing")).thenReturn(null);
        var template = new LinkedHashMap<String, Integer>(){{
            put("Missing", 0);
        }};

        defaultSorter().sort(workbook, template);

        verify(workbook, never()).setSheetOrder(anyString(), anyInt());
    }

    @Test
    void sort_shouldMaintainOrderForSamePositions() {
        var workbook = mock(Workbook.class);
        var sheet = mock(Sheet.class);
        when(workbook.getSheet("Test")).thenReturn(sheet);
        when(sheet.getSheetName()).thenReturn("Test");
        var template = new LinkedHashMap<String, Integer>() {{
            put("Test", 0);
        }};

        assertDoesNotThrow(() -> defaultSorter().sort(workbook, template));
    }
}