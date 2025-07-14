package uk.gov.laa.gpfd.services.excel.copier;

import org.apache.poi.ss.usermodel.Sheet;
import uk.gov.laa.gpfd.services.excel.copier.types.basic.SheetContentCopier;
import uk.gov.laa.gpfd.services.excel.formatting.Formatting;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * An abstract base class for copying content between Excel sheets.
 * Provides the core copying algorithm while allowing subclasses to implement sheet-specific features.
 */
public abstract class SheetCopier {

    private static final Function<Formatting, BiConsumer<Sheet, Sheet>> createContentCopier = SheetContentCopier::copyAll;

    /**
     * The standard copying operation that handles basic sheet content.
     * Uses all available {@link SheetContentCopier} implementations.
     */
    private final BiConsumer<Sheet, Sheet> copyAll;

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
    protected SheetCopier(Formatting cellFormatter, Sheet sourceSheet, Sheet targetSheet) {
        Objects.requireNonNull(sourceSheet, "SourceSheet must not be null");
        Objects.requireNonNull(targetSheet, "TargetSheet stream must not be null");
        this.sourceSheet = sourceSheet;
        this.targetSheet = targetSheet;
        this.copyAll = createContentCopier.apply(cellFormatter);
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