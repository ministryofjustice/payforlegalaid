package uk.gov.laa.gpfd.utils;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;

import static org.apache.poi.ooxml.util.PackageHelper.open;

/**
 * A factory interface for creating {@link Workbook} instances from input streams.
 * Provides functional composition capabilities for adding transformations and fallback behavior.
 *
 * <p>Implementations of this interface are responsible for parsing Excel workbook data
 * from various sources while supporting secure processing and error recovery patterns.</p>
 *
 * @see Workbook
 * @see SecurityPolicy
 */
public interface WorkbookFactory {

    /**
     * Creates a new XSSF Workbook instance from an input stream, with robust fallback handling.
     *
     * <p>This method guarantees to always return a valid Workbook instance, handling all error cases
     * by returning a fresh empty workbook.</p>
     */
    static Workbook newWorkbook(InputStream input) {
        if (null == input) {
            return new XSSFWorkbook();
        }

        try {
            if (input.available() == 0) {
                return new XSSFWorkbook(open(input, true));
            }
        } catch (Exception e) {
            return new XSSFWorkbook();
        }

        try {
            return new XSSFWorkbook(input);
        } catch (IOException e) {
            try { input.close(); } catch (IOException ignored) {}
            return new XSSFWorkbook();
        }
    }

    /**
     * A factory that implement the "BiGGrid" strategy. This specialized workbook factory produces workbooks optimized
     * for handling very large datasets while maintaining controlled memory usage.
     *
     * <p>The created SXSSFWorkbooks only keep a configurable window of rows in memory at any time,
     * significantly reducing memory consumption compared to standard XSSF workbooks. This makes
     * it suitable for processing large Excel files without risking out-of-memory errors.
     */
    interface SXSSFWorkbookFactory extends WorkbookFactory {

        /**
         * Creates a new streaming workbook from the input stream.
         *
         * @param input the input stream containing XSSF workbook data
         * @return a workbook implementation
         * @throws IOException if an I/O error occurs or the input data is malformed
         * @throws IllegalStateException if the input does not contain an XSSF workbook
         * @throws IllegalArgumentException if the input stream is null
         */
        SXSSFWorkbook create(InputStream input) throws IOException;
    }

    /**
     * Creates a new Workbook instance from the provided input stream.
     *
     * @param input the input stream containing workbook data. The stream will be consumed
     *              but not closed by this method. Implementations should not buffer the entire
     *              stream if possible.
     * @return a fully parsed Workbook instance
     * @throws IOException              if an I/O error occurs or if the input data is malformed
     * @throws IllegalArgumentException if the input stream is null
     */
    Workbook create(InputStream input) throws IOException;

    /**
     * Converts this workbook factory into a streaming factory that produces {@link SXSSFWorkbook} instances
     * with a specified row access window size. This enables memory-efficient processing of large Excel files
     * by keeping only a subset of rows in memory at any given time.
     *
     * @param rowAccessWindowSize the number of rows to keep in memory
     * @return a new {@link SXSSFWorkbookFactory} instance
     */
    default SXSSFWorkbookFactory asStreamed(int rowAccessWindowSize) {
        var self = this;

        return (InputStream input) -> {
            var workbook = self.create(input);
            if (workbook instanceof XSSFWorkbook xssf) {
                return new SXSSFWorkbook(xssf, rowAccessWindowSize, true, true);
            }
            throw new IllegalStateException("Expected XSSF workbook but got: " + workbook.getClass().getSimpleName());
        };
    }

    /**
     * Returns a new WorkbookFactory that applies the specified transformation to the input
     * stream before creating the workbook. This enables secure processing patterns like
     * ZIP bomb protection without modifying the core factory logic.
     *
     * <p>The transformation is applied before the factory's create method is called,
     * allowing for input stream decoration or validation.</p>
     *
     * @param transformer the security policy to apply to the input stream
     * @return a new WorkbookFactory instance with the transformation applied
     * @throws NullPointerException if the transformer is null
     * @see SecurityPolicy#zipBombProtection(double)
     */
    default WorkbookFactory withTransformation(SecurityPolicy<InputStream> transformer) {
        return (InputStream input) -> create(transformer.apply(input));
    }

    /**
     * Returns a new WorkbookFactory that attempts to use this factory first, and if that
     * fails, falls back to the specified alternative factory. This enables graceful
     * degradation when dealing with multiple workbook formats or versions.
     *
     * <p>Exceptions thrown by the primary factory will be caught and the fallback will be
     * attempted. If both factories fail, the exception from the fallback factory will be
     * thrown.</p>
     *
     * @param fallback the alternative factory to use if this factory fails
     * @return a new WorkbookFactory instance with fallback behavior
     * @throws NullPointerException if the fallback factory is null
     */
    default WorkbookFactory withFallback(WorkbookFactory fallback) {
        return (InputStream input) -> {
            try {
                return create(input);
            } catch (Exception e) {
                return fallback.create(input);
            }
        };
    }
}
