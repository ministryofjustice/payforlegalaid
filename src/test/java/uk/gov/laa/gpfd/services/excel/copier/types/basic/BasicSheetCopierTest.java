package uk.gov.laa.gpfd.services.excel.copier.types.basic;

import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.services.excel.copier.SheetCopier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class BasicSheetCopierTest {

    @Mock
    private Sheet sourceSheet;

    @Mock
    private Sheet targetSheet;

    @Test
    void constructor_shouldThrowWhenSourceSheetIsNull() {
        assertThrows(NullPointerException.class, () -> new BasicSheetCopier(null, targetSheet));
    }

    @Test
    void constructor_shouldThrowWhenTargetSheetIsNull() {
        assertThrows(NullPointerException.class, () -> new BasicSheetCopier(sourceSheet, null));
    }

    @Test
    void constructor_shouldAcceptValidSheets() {
        assertDoesNotThrow(() -> new BasicSheetCopier(sourceSheet, targetSheet));
    }

    @Test
    void copyAdditionalFeatures_shouldDoNothing() {
        var copier = new BasicSheetCopier(sourceSheet, targetSheet);

        assertDoesNotThrow(copier::copyAdditionalFeatures);

        verifyNoInteractions(sourceSheet, targetSheet);
    }

    @Test
    void inheritance_shouldExtendSheetCopier() {
        var copier = new BasicSheetCopier(sourceSheet, targetSheet);
        assertInstanceOf(SheetCopier.class, copier);
    }

    @Test
    void copySheet_shouldNotModifySheets() {
        var copier = spy(new BasicSheetCopier(sourceSheet, targetSheet));

        copier.copySheet();

        verify(sourceSheet, never()).createRow(anyInt());
        verify(targetSheet, never()).createRow(anyInt());
    }

}