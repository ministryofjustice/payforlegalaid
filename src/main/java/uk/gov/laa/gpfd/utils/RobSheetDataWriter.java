package uk.gov.laa.gpfd.utils;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SheetDataWriter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellType;

import java.io.IOException;
import java.io.Writer;
import java.util.TreeMap;

public class RobSheetDataWriter extends SheetDataWriter {
    private SharedStringsTable _sharedStringSource;

    public RobSheetDataWriter() throws IOException {
    }

    public RobSheetDataWriter(Writer writer) throws IOException {
        super(writer);
    }

    public RobSheetDataWriter(SharedStringsTable sharedStringsTable) throws IOException {
        super(sharedStringsTable);
        _sharedStringSource = sharedStringsTable;
    }

    //org.apache.poi.xssf.streaming.SXSSFCell.getDefaultCellStyleFromColumn ()	313,388 ms (89.1%)	313,388 ms (89.3%)
    @SneakyThrows
    @Override
    public void writeCell(int columnIndex, Cell cell) throws IOException {
        if (cell == null) {
            return;
        }
        var _rownumAccess = SheetDataWriter.class.getDeclaredField("_rownum");
        _rownumAccess.setAccessible(true);
        int _rownum = (int) _rownumAccess.get(this);
        String ref = new CellReference(_rownum, columnIndex).formatAsString();
        _out.write("<c");
        writeAttribute("r", ref);
//          Bin the logic
//        CellStyle cellStyle = cell.getCellStyle();
//        if (cellStyle.getIndex() != 0) {
            // need to convert the short to unsigned short as the indexes can be up to 64k
            // ideally we would use int for this index, but that would need changes to some more
            // APIs
//            writeAttribute("s", Integer.toString(cellStyle.getIndex() & 0xffff));
//        }
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case BLANK: {
                _out.write('>');
                break;
            }
            case FORMULA: {
                switch(cell.getCachedFormulaResultType()) {
                    case NUMERIC:
                        writeAttribute("t", "n");
                        break;
                    case STRING:
                        writeAttribute("t", STCellType.STR.toString());
                        break;
                    case BOOLEAN:
                        writeAttribute("t", "b");
                        break;
                    case ERROR:
                        writeAttribute("t", "e");
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

                    writeAttribute("t", STCellType.S.toString());
                    _out.write("><v>");
                    _out.write(String.valueOf(sRef));
                    _out.write("</v>");
                } else {
                    writeAttribute("t", "inlineStr");
                    _out.write("><is><t");
                    if (hasLeadingTrailingSpaces(cell.getStringCellValue())) {
                        writeAttribute("xml:space", "preserve");
                    }
                    _out.write(">");
                    outputEscapedString(cell.getStringCellValue());
                    _out.write("</t></is>");
                }
                break;
            }
            case NUMERIC: {
                writeAttribute("t", "n");
                _out.write("><v>");
                _out.write(Double.toString(cell.getNumericCellValue()));
                _out.write("</v>");
                break;
            }
            case BOOLEAN: {
                writeAttribute("t", "b");
                _out.write("><v>");
                _out.write(cell.getBooleanCellValue() ? "1" : "0");
                _out.write("</v>");
                break;
            }
            case ERROR: {
                FormulaError error = FormulaError.forInt(cell.getErrorCellValue());

                writeAttribute("t", "e");
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

    private void writeAttribute(String name, String value) throws IOException {
        _out.write(' ');
        _out.write(name);
        _out.write("=\"");
        _out.write(value);
        _out.write('\"');
    }

    boolean hasLeadingTrailingSpaces(String str) {
        if (str != null && str.length() > 0) {
            char firstChar = str.charAt(0);
            char lastChar  = str.charAt(str.length() - 1);
            return Character.isWhitespace(firstChar) || Character.isWhitespace(lastChar) ;
        }
        return false;
    }
}
