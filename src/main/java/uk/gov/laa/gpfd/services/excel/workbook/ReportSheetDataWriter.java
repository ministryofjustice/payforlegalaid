package uk.gov.laa.gpfd.services.excel.workbook;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.streaming.SheetDataWriter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellType;
import sun.misc.Unsafe;
import uk.gov.laa.gpfd.model.ReportQuery;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * A high-performance Excel sheet data writer that extends {@link SheetDataWriter} with optimized
 * cell writing capabilities using low-level memory operations.
 *
 * <p>This implementation provides significant performance improvements for writing Excel files by:</p>
 * <ul>
 *   <li>Using direct memory access to internal writer state</li>
 *   <li>Pre-caching common XML tags and values</li>
 *   <li>Implementing optimized number formatting</li>
 *   <li>Providing efficient string escaping</li>
 * </ul>
 * <p><strong>Formatting Approach:</strong></p>
 * <p>Unlike the original implementation which maintained individual cell formatting assignments,
 * this optimized version uses a column-based formatting strategy. Instead of storing formatting
 * information with each cell, we:</p>
 *
 * <ol>
 * <li>Determine the column index of each cell being written</li>
 * <li>Apply formatting based on pre-defined column styles</li>
 * <li>Maintain a column-to-style mapping for efficient lookup</li>
 * </ol>
 * <p>This approach provides several advantages:</p>
 * <ul>
 * <li><strong>Reduced Memory Usage:</strong> Eliminates per-cell formatting storage</li>
 * <li><strong>Improved Performance:</strong> Faster style lookups through column indexing</li>
 * <li><strong>Consistent Formatting:</strong> Ensures uniform styling across entire columns</li>
 * <li><strong>Simplified Style Management:</strong> Centralizes style definitions at column level</li>
 * </ul>
 * <p>Note that this design assumes formatting consistency within columns, which is typical
 * for most data-oriented Excel files.</p>
 */
public final class ReportSheetDataWriter extends SheetDataWriter implements Closeable {
    /**
     * Unsafe instance for low-level memory operations.
     */
    private static final Unsafe UNSAFE = getUnsafe();

    /**
     * Memory offset for the row number field.
     */
    private static final long ROWNUM_OFFSET;

    static {
        try {
            ROWNUM_OFFSET = UNSAFE.objectFieldOffset(SheetDataWriter.class.getDeclaredField("_rownum"));

        } catch (Exception e) {
            throw new Error("Failed to initialize", e);
        }
    }

    /**
     * Shared strings table reference (null if using inline strings).
     */
    private final SharedStringsTable _sharedStringSource;
    private final ReportQuery report;
    private final StyleManager styleManager;

    /**
     * Creates a new ReportSheetDataWriter with the specified shared strings table.
     *
     * @param sharedStringsTable the shared strings table to use
     * @throws IOException if the writer cannot be initialized
     */
    public ReportSheetDataWriter(SharedStringsTable sharedStringSource, ReportQuery report, StyleManager styleManager) throws IOException {
        super(sharedStringSource);
        this._sharedStringSource = sharedStringSource;
        this.report = report;
        this.styleManager = styleManager;
    }

    /**
     * Gets the Unsafe
     *
     * @return the Unsafe instance
     * @throws Error if Unsafe is not available
     */
    private static Unsafe getUnsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            throw new Error("Unsafe not available", e);
        }
    }

    /**
     * Writes a cell to the output stream with optimized formatting.
     *
     * <p><strong>Performance Note:</strong> This method employs {@code sun.misc.Unsafe} for low-level memory
     * access to achieve optimal performance in high-volume spreadsheet generation scenarios. The operation:
     * <pre>
     *   int rownum = UNSAFE.getInt(this, ROWNUM_OFFSET);
     * </pre>
     *
     * <p>This approach provides several performance advantages:
     * <ul>
     *   <li><strong>Reduced Overhead:</strong> Eliminates method call overhead that would be incurred by
     *       traditional getter methods, which is significant when processing millions of cells.</li>
     *
     *   <li><strong>Memory Efficiency:</strong> Avoids creating temporary objects or boxed primitives that
     *       trigger garbage collection pressure during large file generation.</li>
     * </ul>
     *
     * @param columnIndex the column index (0-based)
     * @param cell        the cell to write
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void writeCell(int columnIndex, Cell cell) throws IOException {
        if (cell == null) {
            return;
        }
        int rownum = UNSAFE.getInt(this, ROWNUM_OFFSET);
        String ref = new CellReference(rownum, columnIndex).formatAsString();
        _out.write("<c");
        writeXml("r", ref);
        int columnStyle = styleManager.getColumnStyle(columnIndex, report.getExcelSheet().getName());
        if (columnStyle != -1) {
            writeXml("s", Integer.toString(columnStyle));
        }

        // The following code replicates org.apache.poi.xssf.streaming.SheetDataWriter.writeCell internals
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case BLANK: {
                _out.write('>');
                break;
            }
            case FORMULA: {
                switch(cell.getCachedFormulaResultType()) {
                    case NUMERIC:
                        writeXml("t", "n");
                        break;
                    case STRING:
                        writeXml("t", STCellType.STR.toString());
                        break;
                    case BOOLEAN:
                        writeXml("t", "b");
                        break;
                    case ERROR:
                        writeXml("t", "e");
                        break;
                }
                _out.write("><f>");
                outputEscapedString(cell.getCellFormula());
                _out.write("</f>");
                switch (cell.getCachedFormulaResultType()) {
                    case NUMERIC:
                        double nval = cell.getNumericCellValue();
                        if (!Double.isNaN(nval)) {
                            _out.write("<v>");
                            _out.write(Double.toString(nval));
                            _out.write("</v>");
                        }
                        break;
                    case STRING:
                        String value = cell.getStringCellValue();
                        if(value != null && !value.isEmpty()) {
                            _out.write("<v>");
                            outputEscapedString(value);
                            _out.write("</v>");
                        }
                        break;
                    case BOOLEAN:
                        _out.write("><v>");
                        _out.write(cell.getBooleanCellValue() ? "1" : "0");
                        _out.write("</v>");
                        break;
                    case ERROR: {
                        FormulaError error = FormulaError.forInt(cell.getErrorCellValue());

                        _out.write("><v>");
                        outputEscapedString(error.getString());
                        _out.write("</v>");
                        break;
                    }
                }
                break;
            }
            case STRING: {
                if (_sharedStringSource != null) {
                    RichTextString rt = cell.getRichStringCellValue();
                    int sRef = _sharedStringSource.addSharedStringItem(rt);

                    writeXml("t", STCellType.S.toString());
                    _out.write("><v>");
                    _out.write(String.valueOf(sRef));
                    _out.write("</v>");
                } else {
                    writeXml("t", "inlineStr");
                    _out.write("><is><t");
                    if (checkLeadingTrailingSpaces(cell.getStringCellValue())) {
                        writeXml("xml:space", "preserve");
                    }
                    _out.write(">");
                    outputEscapedString(cell.getStringCellValue());
                    _out.write("</t></is>");
                }
                break;
            }
            case NUMERIC: {
                writeXml("t", "n");
                _out.write("><v>");
                _out.write(Double.toString(cell.getNumericCellValue()));
                _out.write("</v>");
                break;
            }
            case BOOLEAN: {
                writeXml("t", "b");
                _out.write("><v>");
                _out.write(cell.getBooleanCellValue() ? "1" : "0");
                _out.write("</v>");
                break;
            }
            case ERROR: {
                FormulaError error = FormulaError.forInt(cell.getErrorCellValue());

                writeXml("t", "e");
                _out.write("><v>");
                outputEscapedString(error.getString());
                _out.write("</v>");
                break;
            }
            default: {
                throw new IllegalStateException("Invalid cell type: " + cellType);
            }
        }
        _out.write("</c>");
    }

    /**
     * Replicates private org.apache.poi.xssf.streaming.SheetDataWriter.writeAttribute internals
     */
    private void writeXml(String name, String value) throws IOException {
        _out.write(' ');
        _out.write(name);
        _out.write("=\"");
        _out.write(value);
        _out.write('\"');
    }

    /**
     * Checks if a string has leading or trailing whitespace.
     */
    private boolean checkLeadingTrailingSpaces(String str) {
        if (str == null || str.isEmpty()) return false;
        char first = str.charAt(0);
        char last = str.charAt(str.length() - 1);
        return first <= ' ' || last <= ' ';
    }


}