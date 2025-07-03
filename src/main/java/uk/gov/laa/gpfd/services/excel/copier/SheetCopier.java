package uk.gov.laa.gpfd.services.excel.copier;

import org.apache.poi.ss.usermodel.Sheet;
import uk.gov.laa.gpfd.services.excel.copier.types.basic.SheetContentCopier;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * An abstract base class for copying content between Excel sheets.
 * Provides the core copying algorithm while allowing subclasses to implement sheet-specific features.
 */
public abstract class SheetCopier {
    /**
     * The standard copying operation that handles basic sheet content.
     * Uses all available {@link SheetContentCopier} implementations.
     */
    private static final BiConsumer<Sheet, Sheet> copyAll = SheetContentCopier::copyAll;

    /** The source sheet to copy from */
    protected final Sheet sourceSheet;

    /** The target sheet to copy to */
    protected final Sheet targetSheet;

    /**
     * Creates a new SheetCopier instance.
     *
     * @param sourceSheet the source sheet to copy from (must not be null)
     * @param targetSheet the target sheet to copy to (must not be null)
     * @throws IllegalArgumentException if either sheet parameter is null
     */
    protected SheetCopier(Sheet sourceSheet, Sheet targetSheet) {
        Objects.requireNonNull(sourceSheet, "SourceSheet must not be null");
        Objects.requireNonNull(targetSheet, "TargetSheet stream must not be null");
        this.sourceSheet = sourceSheet;
        this.targetSheet = targetSheet;
    }

    /**
     * Executes the complete sheet copying process.
     *
     * @throws IllegalStateException if any error occurs during copying
     */
    public final void copySheet() {
        copyAll.accept(sourceSheet, targetSheet);
        copyAdditionalFeatures();
    }

    /**
     * Hook method for subclasses to implement sheet-specific feature copying.
     * Called after standard content has been copied.
     */
    protected abstract void copyAdditionalFeatures();
}