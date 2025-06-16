package uk.gov.laa.gpfd.services.excel.copier.copier.basic;

import org.apache.poi.ss.usermodel.Sheet;
import uk.gov.laa.gpfd.services.excel.copier.SheetCopier;
import uk.gov.laa.gpfd.services.excel.copier.copier.xssf.XSSFSheetCopier;

/**
 * A basic implementation of {@link SheetCopier} that handles copying standard Excel sheet content
 * without any special features.
 *
 * <p>This implementation does not handle any specialized features like pivot tables or charts,
 * which are delegated to specialized copiers like {@link XSSFSheetCopier}.</p>
 */
public class BasicSheetCopier extends SheetCopier {

    /**
     * Constructs a new BasicSheetCopier for copying between source and target sheets.
     *
     * @param sourceSheet the source sheet to copy from (must not be null)
     * @param targetSheet the target sheet to copy to (must not be null)
     * @throws IllegalArgumentException if either sheet is null
     */
    public BasicSheetCopier(Sheet sourceSheet, Sheet targetSheet) {
        super(sourceSheet, targetSheet);
    }

    /**
     * Implementation of additional feature copying for basic sheets.
     *
     * <p>This implementation is intentionally empty as {@code BasicSheetCopier}
     * doesn't handle any special features beyond the standard content copying
     * provided by the parent class.</p>
     */
    @Override
    protected void copyAdditionalFeatures() {
        // No additional features to copy for basic sheets
    }

}