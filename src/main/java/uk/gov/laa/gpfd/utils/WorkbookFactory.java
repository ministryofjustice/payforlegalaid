package uk.gov.laa.gpfd.utils;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;

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