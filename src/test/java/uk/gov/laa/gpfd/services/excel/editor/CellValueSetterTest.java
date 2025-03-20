package uk.gov.laa.gpfd.services.excel.editor;

import org.apache.poi.ss.usermodel.Cell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CellValueSetterTest implements CellValueSetter {

    private Cell cell;

    @BeforeEach
    void setUp() {
        cell = mock(Cell.class);
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void shouldSetCellValueCorrectly(Object value, Verifier verifier) {
        setCellValue(cell, value);

        verifier.verify(cell, value);
    }

    @FunctionalInterface
    private interface Verifier {
        void verify(Cell cell, Object value);
    }

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of("Test Value", (Verifier) (cell, value) -> verify(cell).setCellValue((String) value)),
                Arguments.of(Double.valueOf("123.12"), (Verifier) (cell, value) -> verify(cell).setCellValue(((Number) value).doubleValue())),
                Arguments.of(Integer.valueOf("3"), (Verifier) (cell, value) -> verify(cell).setCellValue(((Number) value).doubleValue())),
                Arguments.of(new BigDecimal("123.45"), (Verifier) (cell, value) -> verify(cell).setCellValue(((BigDecimal) value).doubleValue())),
                Arguments.of(true, (Verifier) (cell, value) -> verify(cell).setCellValue((Boolean) value)),
                Arguments.of(new Timestamp(System.currentTimeMillis()), (Verifier) (cell, value) -> verify(cell).setCellValue((Timestamp) value))
        );
    }

}