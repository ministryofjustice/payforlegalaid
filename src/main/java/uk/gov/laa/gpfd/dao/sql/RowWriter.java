package uk.gov.laa.gpfd.dao.sql;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static uk.gov.laa.gpfd.dao.sql.RowWriter.StreamRowWriter;

/**
 * A functional interface for writing database rows to an output destination.
 * Implementations handle the formatting and writing of row data.
 *
 * <p>The interface provides a factory method {@link #forStream(OutputStream)}
 * to create default implementations that write to output streams.
 */
public sealed interface RowWriter permits
        StreamRowWriter {

    /**
     * Creates a RowWriter that writes to the specified output stream.
     * The writer formats output as comma-separated values with newline termination.
     *
     * @param stream the output stream to write to (must not be null)
     * @return a new RowWriter instance configured for the given stream
     * @throws NullPointerException if stream is null
     */
    static RowWriter forStream(OutputStream stream) {
        Objects.requireNonNull(stream, "OutputStream cannot be null");
        return new StreamRowWriter(stream);
    }

    /**
     * Writes a single row using the provided value extractor.
     *
     * @param extractor   the value extractor that provides column values
     * @param columnCount the number of columns in the row
     * @throws IOException          if an I/O error occurs while writing
     * @throws NullPointerException if extractor is null
     */
    void writeRow(ValueExtractor extractor, int columnCount) throws IOException;

    /**
     * Stream implementation of RowWriter that writes to an OutputStream.
     * Formats output as CSV with comma separators and newline terminators.
     *
     * @param stream the output stream to write to
     */
    record StreamRowWriter(OutputStream stream) implements RowWriter {
        private static final byte[] COMMA = ",".getBytes(), NEWLINE = "\n".getBytes();

        /**
         * {@inheritDoc}
         *
         * @implSpec The implementation:
         * <ul>
         *   <li>Writes all column values separated by commas</li>
         *   <li>Terminates each row with a newline</li>
         *   <li>Converts SQLExceptions to IOExceptions</li>
         * </ul>
         * @implNote Column values are written in order from 1 to columnCount.
         * Empty values are written as empty strings (not "null").
         */
        @Override
        public void writeRow(ValueExtractor extractor, int columnCount) throws IOException {
            try {
                if (columnCount == 0) {
                    stream.write(NEWLINE);
                    return;
                }
                // Build schema dynamically based on column count
                CsvSchema.Builder schemaBuilder = CsvSchema.builder();
                for (int i = 1; i <= columnCount; i++) {
                    schemaBuilder.addColumn(extractor.extract(i));
                }
                CsvSchema schema = schemaBuilder.build();

                // Create CsvMapper
                CsvMapper csvMapper = new CsvMapper();

                // Collect row values into a List
                List<String> rowValues = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnValue = extractor.extract(i);
                    rowValues.add(columnValue == null ? "" : columnValue);
                }

                // Write row using CsvMapper
                String csvRow = csvMapper.writer(schema).writeValueAsString(rowValues);

                // Write to stream
                stream.write(csvRow.getBytes(StandardCharsets.UTF_8));

            } catch (SQLException e) {
                throw new IOException("Error extracting row data", e);
            }
        }
    }
}
