package uk.gov.laa.gpfd.dao.sql;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.jdbc.core.RowCallbackHandler;
import uk.gov.laa.gpfd.model.FieldProjection;
import uk.gov.laa.gpfd.services.excel.editor.CellValueSetter;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static uk.gov.laa.gpfd.dao.sql.ChannelRowHandler.SheetChannelRowHandler;
import static uk.gov.laa.gpfd.dao.sql.ChannelRowHandler.StreamChannelRowHandler;
import static uk.gov.laa.gpfd.dao.sql.ValueExtractor.ofHeader;
import static uk.gov.laa.gpfd.dao.sql.ValueExtractor.ofRow;

/**
 * A handler for processing database result set rows and writing them to an output channel.
 * Handles both header and data row writing with proper formatting.
 *
 * @see RowCallbackHandler
 * @see AutoCloseable
 */
public sealed interface ChannelRowHandler extends
        RowCallbackHandler,
        AutoCloseable
        permits SheetChannelRowHandler, StreamChannelRowHandler {

    /**
     * Creates a {@code ChannelRowHandler} that writes to the specified output stream.
     *
     * @param stream the output stream to write to (must not be {@code null})
     * @return a new {@code ChannelRowHandler} instance configured for the given stream
     * @throws NullPointerException if {@code stream} is {@code null}
     */
    static ChannelRowHandler forStream(OutputStream stream) {
        Objects.requireNonNull(stream, "OutputStream cannot be null");
        return new StreamChannelRowHandler(stream);
    }

    /**
     * Creates a ChannelRowHandler for the given sheet with field projections.
     *
     * @param sheet The Excel sheet to process
     * @param fieldAttributes List of field projections that map database columns to sheet headers
     * @return A ChannelRowHandler configured for the given sheet and field mappings
     * @throws NullPointerException if either sheet or fieldAttributes is null
     */
    static ChannelRowHandler forSheet(Sheet sheet, List<FieldProjection> fieldAttributes) {
        var mapping = new HashMap<String, Integer>();
        var headerRow = sheet.getRow(0);

        for (var pair : fieldAttributes) {
            for (var headerCell : headerRow) {
                if (pair.getMappedName().equals(headerCell.getStringCellValue())) {
                    mapping.put(pair.getSourceName(), headerCell.getColumnIndex());
                    break;
                }
            }
        }
        return new SheetChannelRowHandler(sheet, mapping);
    }

    /**
     * Implementation of ChannelRowHandler that processes database result sets into Excel sheet rows.
     * Maps database columns to Excel columns based on the provided projection mapping.
     */
    final class SheetChannelRowHandler implements ChannelRowHandler, CellValueSetter {
        private final Sheet sheet;
        private final Map<String, Integer> projection;
        private int rowNum = 1;

        /**
         * Constructs a new SheetChannelRowHandler for the given sheet and column projection.
         *
         * @param sheet The Excel sheet to write to
         * @param projection Mapping of database column names to Excel column indices
         */
        public SheetChannelRowHandler(Sheet sheet, Map<String, Integer> projection) {
            this.sheet = sheet;
            this.projection = projection;
        }

        /**
         * Processes a single row from the result set and writes it to the Excel sheet.
         * Only columns that exist in the projection mapping will be written.
         *
         * @param rs The result set containing the data to process
         * @throws SQLException if a database access error occurs
         */
        @Override
        public void processRow(ResultSet rs) throws SQLException {
            var row = sheet.createRow(rowNum++);
            var metaData = rs.getMetaData();
            var columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                var columnName = metaData.getColumnLabel(i);

                if (projection.containsKey(columnName)) {
                    var cell = row.createCell(projection.get(columnName), CellType.BLANK);
                    var value = rs.getObject(i);
                    setCellValue(cell, value != null ? value : "");
                }
            }
        }

        /**
         * Closes this handler. This implementation does nothing.
         */
        @Override
        public void close() {}

    }


    /**
     * Stream-based implementation of {@link ChannelRowHandler} that writes to an {@link OutputStream}.
     */
    final class StreamChannelRowHandler implements ChannelRowHandler {
        private final AtomicBoolean headerWritten = new AtomicBoolean(false);
        private final OutputStream stream;
        private final RowWriter rowWriter;

        /**
         * Constructs a new handler for the specified output stream.
         *
         * @param stream the output stream to write to (must not be {@code null})
         * @throws NullPointerException if {@code stream} is {@code null}
         */
        public StreamChannelRowHandler(OutputStream stream) {
            this.stream = Objects.requireNonNull(stream, "OutputStream cannot be null");
            this.rowWriter = RowWriter.forStream(stream);
        }

        /**
         * Processes a result set row by writing either:
         * <ul>
         *   <li>Header row (if this is the first row processed)</li>
         *   <li>Data row (for subsequent calls)</li>
         * </ul>
         * Automatically flushes the output stream after writing.
         *
         * @param rs the result set containing the current row data
         * @throws SQLException if a database access error occurs or if writing to the stream fails
         */
        @Override
        public void processRow(ResultSet rs) throws SQLException {
            try {
                var metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                writeHeaderIfNeeded(metaData, columnCount);
                writeRowData(rs, columnCount);

                stream.flush();
            } catch (IOException e) {
                throw new SQLException("Error writing to output stream", e);
            }
        }

        private void writeHeaderIfNeeded(ResultSetMetaData metaData, int columnCount) throws IOException {
            if (headerWritten.compareAndSet(false, true)) {
                rowWriter.writeRow(ofHeader(metaData), columnCount);
            }
        }

        private void writeRowData(ResultSet rs, int columnCount) throws IOException {
            rowWriter.writeRow(ofRow(rs), columnCount);
        }

        /**
         * Closes the underlying output stream.
         * After calling this method, the handler should not be used.
         *
         * @throws IOException if an I/O error occurs while closing the stream
         */
        @Override
        public void close() throws IOException {
            stream.close();
        }
    }
}
