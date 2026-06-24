package uk.gov.laa.pfla.util.workbook;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptyIterator;
import static java.util.Spliterators.spliteratorUnknownSize;

/**
 * An abstract base class for iterating through cells in an Excel Sheet.
 * <p>
 * Provides both iterator and stream capabilities for traversing all cells
 * across all sheets and rows in a workbook. The iteration follows the order:
 * Sheet → Row → Cell.
 * </p>
 */
public abstract class SheetCellIterator<T extends Cell> implements Iterable<T> {

    /**
     * Gets the sheet being iterated over.
     */
    public abstract Iterable<Sheet> sheetIterable();

    /**
     * Creates a new SheetCellIterator for the given workbook.
     *
     * @param workbook the workbook to iterate through
     * @return a new WorkbookCellIterator instance
     * @throws IllegalArgumentException if workbook is null
     */
    public static <T extends Cell> SheetCellIterator<T> ofWorkbook(Workbook workbook) {
        if (workbook == null) {
            throw new IllegalArgumentException("Workbook cannot be null");
        }

        return new SheetCellIterator<T>() {
            @Override
            public Iterable<Sheet> sheetIterable() {
                return workbook;
            }
        };
    }

    /**
     * Returns an iterator for traversing all cells in the workbook.
     *
     * @return a new iterator instance
     * @throws IllegalStateException if the workbook contains no sheets
     */
    @Override
    public final Iterator<T> iterator() {
        return new CellIterator<>(sheetIterable());
    }

    /**
     * Returns a sequential Stream of all cells in the workbook.
     *
     * @return a new stream of cells
     */
    public final Stream<T> stream() {
        return StreamSupport.stream(
                spliteratorUnknownSize(
                        iterator(),
                        Spliterator.ORDERED | Spliterator.NONNULL
                ),
                false
        );
    }

    /**
     * Internal iterator implementation for traversing workbook cells.
     */
    private static final class CellIterator<T extends Cell> implements Iterator<T> {
        private final Iterator<Sheet> sheetIterator;
        private Iterator<Row> rowIterator;
        private Iterator<T> cellIterator;
        private boolean initialized = false;

        /**
         * Creates a new CellIterator for the given workbook.
         *
         * @param iterable the iterable to iterate through
         * @throws IllegalArgumentException if workbook is null
         */
        CellIterator(Iterable<Sheet> iterable) {
            if (iterable == null) {
                throw new IllegalArgumentException("Workbook cannot be null");
            }
            this.sheetIterator = iterable.iterator();
            this.rowIterator = emptyIterator();
            this.cellIterator = emptyIterator();
        }

        /**
         * Checks if there are more cells to iterate over.
         *
         * @return true if there are more cells, false otherwise
         */
        @Override
        public boolean hasNext() {
            if (!initialized) {
                advanceToNextCell();
                initialized = true;
            }

            while (!cellIterator.hasNext()) {
                if (!advanceToNextCell()) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Advances to the next available cell in the workbook.
         *
         * @return true if advancement was successful, false if no more cells
         */
        private boolean advanceToNextCell() {
            while (!rowIterator.hasNext()) {
                if (!sheetIterator.hasNext()) {
                    return false;
                }
                rowIterator = sheetIterator.next().rowIterator();
            }

            while (rowIterator.hasNext()) {
                var row = rowIterator.next();
                cellIterator = (Iterator<T>) row.cellIterator();
                if (cellIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Returns the next cell in the iteration.
         *
         * @return the next cell
         * @throws java.util.NoSuchElementException if there are no more cells
         */
        @Override
        public T next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException("No more cells in workbook");
            }
            return cellIterator.next();
        }
    }
}