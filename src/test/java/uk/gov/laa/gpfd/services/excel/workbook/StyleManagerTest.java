package uk.gov.laa.gpfd.services.excel.workbook;

import org.apache.poi.ss.usermodel.CellStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StyleManagerTest {

    @Mock
    private CellStyle mockCellStyle1;

    @Mock
    private CellStyle mockCellStyle2;

    private StyleManager styleManager;

    @BeforeEach
    void setUp() {
        styleManager = StyleManager.create();
    }

    @Test
    void create_shouldReturnNewDefaultStyleManagerInstance() {
        var manager = StyleManager.create();
        assertNotNull(manager);
        assertInstanceOf(StyleManager.DefaultStyleManager.class, manager);
    }

    @Test
    void setColumnStyle_shouldStoreStyleForValidColumnAndKey() {
        when(mockCellStyle1.getIndex()).thenReturn((short) 5);

        styleManager.setColumnStyle(0, "header", mockCellStyle1);
        var styleIndex = styleManager.getColumnStyle(0, "header");

        assertEquals(5, styleIndex);
    }

    @Test
    void getColumnStyle_shouldReturnNegativeOneForInvalidColumnIndex() {
        assertEquals(-1, styleManager.getColumnStyle(-1, "key"));
        assertEquals(-1, styleManager.getColumnStyle(100000, "key"));
    }

    @Test
    void getColumnStyle_shouldReturnNegativeOneForUnknownKey() {
        assertEquals(-1, styleManager.getColumnStyle(0, "nonexistent"));
    }

    @Test
    void getColumnStyle_shouldReturnDifferentStylesForDifferentKeys() {
        when(mockCellStyle1.getIndex()).thenReturn((short) 10);
        when(mockCellStyle2.getIndex()).thenReturn((short) 20);

        styleManager.setColumnStyle(0, "key1", mockCellStyle1);
        styleManager.setColumnStyle(0, "key2", mockCellStyle2);

        assertEquals(10, styleManager.getColumnStyle(0, "key1"));
        assertEquals(20, styleManager.getColumnStyle(0, "key2"));
    }

    @Test
    void getColumnStyle_shouldReturnDifferentStylesForDifferentColumns() {
        when(mockCellStyle1.getIndex()).thenReturn((short) 30);
        when(mockCellStyle2.getIndex()).thenReturn((short) 40);

        styleManager.setColumnStyle(0, "key", mockCellStyle1);
        styleManager.setColumnStyle(1, "key", mockCellStyle2);

        assertEquals(30, styleManager.getColumnStyle(0, "key"));
        assertEquals(40, styleManager.getColumnStyle(1, "key"));
    }

    @Test
    void getColumnStyle_shouldHandleStyleIndexConversionCorrectly() {
        when(mockCellStyle1.getIndex()).thenReturn((short) (Short.MAX_VALUE + 1));

        styleManager.setColumnStyle(0, "highValue", mockCellStyle1);
        var styleIndex = styleManager.getColumnStyle(0, "highValue");

        assertEquals(Short.MAX_VALUE + 1, styleIndex);
    }

    @Test
    void setColumnStyle_shouldAllowStyleIndexZero() {
        when(mockCellStyle1.getIndex()).thenReturn((short) 0);

        styleManager.setColumnStyle(0, "zeroIndex", mockCellStyle1);
        var styleIndex = styleManager.getColumnStyle(0, "zeroIndex");

        assertEquals(0, styleIndex);
    }

}