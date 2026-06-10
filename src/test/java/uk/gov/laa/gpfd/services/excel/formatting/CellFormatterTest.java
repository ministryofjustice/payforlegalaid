package uk.gov.laa.gpfd.services.excel.formatting;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.laa.gpfd.model.excel.ExcelMappingProjection;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CellFormatterTest {

    private Cell cell;
    private Sheet sheet;
    private ExcelMappingProjection mappingProjection;

    @BeforeEach
    void setUp() {
        cell = mock(Cell.class);
        sheet = mock(Sheet.class);
        mappingProjection = mock(ExcelMappingProjection.class);
    }

    @Test
    void applyFormatting_shouldInvokeAllFormattingStrategies() {
        var strategy1 = mock(Formatting.class);
        var strategy2 = mock(Formatting.class);
        var formatter = (CellFormatter) (sheet1, cell1, projection) -> {
        };

        formatter.applyFormatting(List.of(strategy1, strategy2), sheet, cell, mappingProjection);

        verify(strategy1).apply(sheet, cell, mappingProjection);
        verify(strategy2).apply(sheet, cell, mappingProjection);
    }
}