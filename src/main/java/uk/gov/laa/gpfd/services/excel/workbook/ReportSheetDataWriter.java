package uk.gov.laa.gpfd.services.excel.workbook;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.streaming.SheetDataWriter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellType;
import uk.gov.laa.gpfd.model.ReportQuery;

import java.io.Closeable;
import java.io.IOException;

/**

 * A high-performance Excel sheet data writer that extends {@link SheetDataWriter}
 * with optimised cell writing capabilities.
 *
 * <p><strong>Row Tracking Strategy:</strong></p>
 * <p>This implementation maintains its own row counter instead of relying on
 * internal state from {@link SheetDataWriter}. This avoids reflection and
 * unsupported APIs, making the implementation stable across JDK and POI versions.</p>
 *
 * <p><strong>Important:</strong> The caller must ensure that {@link #startRow(int)}
 * is invoked correctly to keep row state in sync.</p>
 */
public final class ReportSheetDataWriter extends SheetDataWriter implements Closeable {

    private final SharedStringsTable _sharedStringSource;
    private final ReportQuery report;
    private final StyleManager styleManager;

    /**

     * Tracks the current row number explicitly.
     */
    private int currentRow = -1;

    public ReportSheetDataWriter(SharedStringsTable sharedStringSource,
                                 ReportQuery report,
                                 StyleManager styleManager) throws IOException {
        super(sharedStringSource);
        this._sharedStringSource = sharedStringSource;
        this.report = report;
        this.styleManager = styleManager;
    }

    /**
     * Must be called when a new row starts.
     */
    public void startRow(int rownum) {
        this.currentRow = rownum;
    }

    @Override
    @SuppressWarnings("java/missing-case-in-switch")
    public void writeCell(int columnIndex, Cell cell) throws IOException {
        if (cell == null) {
            return;
        }

        if (currentRow < 0) {
            throw new IllegalStateException("Row not initialized. Call startRow() first.");
        }

        String ref = new CellReference(currentRow, columnIndex).formatAsString();

        _out.write("<c");
        writeXml("r", ref);

        int columnStyle = styleManager.getColumnStyle(columnIndex, report.getExcelSheet().getName());
        if (columnStyle != -1) {
            writeXml("s", Integer.toString(columnStyle));
        }

        CellType cellType = cell.getCellType();
        switch (cellType) {
            case BLANK: {
                _out.write('>');
                break;
            }
            case STRING: {
                if (_sharedStringSource != null) {
                    RichTextString rt = cell.getRichStringCellValue();
                    int sRef = _sharedStringSource.addSharedStringItem(rt);

                    writeXml("t", STCellType.S.toString());
                    _out.write("><v>" + sRef + "</v>");
                } else {
                    writeXml("t", "inlineStr");
                    _out.write("><is><t>");
                    outputEscapedString(cell.getStringCellValue());
                    _out.write("</t></is>");
                }
                break;
            }
            case NUMERIC: {
                writeXml("t", "n");
                _out.write("><v>" + cell.getNumericCellValue() + "</v>");
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
            case FORMULA:
            default:
                throw new IllegalStateException("Unsupported or unhandled cell type: " + cellType);
        }

        _out.write("</c>");

    }

    private void writeXml(String name, String value) throws IOException {
        _out.write(' ');
        _out.write(name);
        _out.write("=");
        _out.write(value);
        _out.write('"');
    }
}