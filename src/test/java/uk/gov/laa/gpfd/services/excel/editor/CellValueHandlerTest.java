package uk.gov.laa.gpfd.services.excel.editor;

import org.apache.poi.ss.usermodel.Cell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CellValueHandlerTest {

    private Cell cell;

    @BeforeEach
    void setUp() {
        cell = mock(Cell.class);
    }

    @ParameterizedTest
    @MethodSource("provideHandlerValues")
    void shouldSetCellValueUsingTheCorrectHandler(CellValueHandler handler, Object value, Verifier verifier) {
        handler.accept(cell, value);

        verifier.verify(cell, value);
    }

    @Test
    void shouldSetEmptyStringForEmptyHandler() {
        CellValueHandler.EMPTY.accept(cell, null);

        verify(cell).setCellValue("");
    }

    @Test
    void shouldUseObjectHandlerForUnknownObjectType() {
        var custom = new Object() {
            @Override
            public String toString() {
                return "custom-value";
            }
        };

        CellValueHandler.OBJECT.accept(cell, custom);

        verify(cell).setCellValue("custom-value");
    }

    @Test
    void shouldContainAllHandlersInTheStaticMap() {
        Assertions.assertSame(CellValueHandler.STRING, CellValueHandler.CellValueHandlerMap.get(String.class));
        Assertions.assertSame(CellValueHandler.INTEGER, CellValueHandler.CellValueHandlerMap.get(Integer.class));
        Assertions.assertSame(CellValueHandler.DOUBLE, CellValueHandler.CellValueHandlerMap.get(Double.class));
        Assertions.assertSame(CellValueHandler.BIG_DECIMAL, CellValueHandler.CellValueHandlerMap.get(BigDecimal.class));
        Assertions.assertSame(CellValueHandler.BOOLEAN, CellValueHandler.CellValueHandlerMap.get(Boolean.class));
        Assertions.assertSame(CellValueHandler.TIMESTAMP, CellValueHandler.CellValueHandlerMap.get(Timestamp.class));
        Assertions.assertSame(CellValueHandler.OBJECT, CellValueHandler.CellValueHandlerMap.get(Object.class));
    }

    private static Stream<Arguments> provideHandlerValues() {
        return Stream.of(
                Arguments.of(CellValueHandler.STRING, "1232", (Verifier) (cell, value) -> verify(cell).setCellValue(1232.0)),
                Arguments.of(CellValueHandler.STRING, "1232.12", (Verifier) (cell, value) -> verify(cell).setCellValue(1232.12)),
                Arguments.of(CellValueHandler.STRING, "Test Value", (Verifier) (cell, value) -> verify(cell).setCellValue((String) value)),
                Arguments.of(CellValueHandler.NUMBER, 123, (Verifier) (cell, value) -> verify(cell).setCellValue(123.0)),
                Arguments.of(CellValueHandler.INTEGER, 3, (Verifier) (cell, value) -> verify(cell).setCellValue(((Number) value).doubleValue())),
                Arguments.of(CellValueHandler.DOUBLE, 123.12d, (Verifier) (cell, value) -> verify(cell).setCellValue((Double) value)),
                Arguments.of(CellValueHandler.BIG_DECIMAL, new BigDecimal("123.45"), (Verifier) (cell, value) -> verify(cell).setCellValue(((BigDecimal) value).doubleValue())),
                Arguments.of(CellValueHandler.BOOLEAN, true, (Verifier) (cell, value) -> verify(cell).setCellValue((Boolean) value)),
                Arguments.of(CellValueHandler.TIMESTAMP, new Timestamp(1_000L), (Verifier) (cell, value) -> verify(cell).setCellValue((Timestamp) value))
        );
    }

    @FunctionalInterface
    private interface Verifier {
        void verify(Cell cell, Object value);
    }
}