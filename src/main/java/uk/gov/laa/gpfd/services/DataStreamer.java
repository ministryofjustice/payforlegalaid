package uk.gov.laa.gpfd.services;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.*;
import org.springframework.jdbc.core.JdbcOperations;
import uk.gov.laa.gpfd.dao.JdbcDataStreamer;
import uk.gov.laa.gpfd.dao.JdbcWorkbookDataStreamer;
import uk.gov.laa.gpfd.exception.TemplateResourceException;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.services.excel.ExcelCreationService;
import uk.gov.laa.gpfd.services.excel.editor.FormulaCalculator;
import uk.gov.laa.gpfd.services.excel.editor.PivotTableRefresher;
import uk.gov.laa.gpfd.services.excel.formatting.CellFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static uk.gov.laa.gpfd.exception.TemplateResourceException.ExcelTemplateCreationException;
import static uk.gov.laa.gpfd.services.excel.editor.PivotTableRefresher.PACKAGE_RELATIONSHIP_TYPE;

/**
 * Provides a contract for streaming data from various sources to an output destination.
 *
 * <p>
 * Implementations of this interface handle the transformation and streaming of data
 * from source systems (databases, APIs, etc.) to various output formats (CSV, Excel, etc.).
 * The interface supports different streaming strategies through factory methods.
 * </p>
 */
public interface DataStreamer {

    /**
     * Creates a new JDBC-based {@link DataStreamer} instance.
     * <p>
     * The returned implementation uses Spring's {@link JdbcOperations} to execute SQL queries
     * and stream results row-by-row to the output destination.
     *
     * @param jdbcOperations The configured JdbcOperations instance. Must not be null.
     * @return A ready-to-use JDBC data streamer
     * @throws IllegalArgumentException if jdbcTemplate is null
     * @see JdbcDataStreamer
     */
    static DataStreamer createJdbcStreamer(JdbcOperations jdbcOperations) {
        return new JdbcDataStreamer(jdbcOperations);
    }

    /**
     * Creates a configured Excel {@link DataStreamer} instance with all required dependencies.
     *
     * @param templateLoader      Service for loading Excel templates (required)
     * @param dataFetcher         DAO for streaming data from source systems (required)
     * @param pivotTableRefresher Component for refreshing pivot tables (required)
     * @param formulaCalculator   Component for calculating formulas (required)
     * @param formatter           Component for formatting cells (required)
     * @return A fully configured Excel data streamer implementation
     * @throws IllegalArgumentException if any argument is null
     */
    static DataStreamer createExcelStreamer(
            TemplateService templateLoader,
            JdbcWorkbookDataStreamer dataFetcher,
            PivotTableRefresher pivotTableRefresher,
            FormulaCalculator formulaCalculator,
            CellFormatter formatter
    ) {
        Objects.requireNonNull(templateLoader, "Template service must not be null");
        Objects.requireNonNull(dataFetcher, "Data fetcher must not be null");
        Objects.requireNonNull(pivotTableRefresher, "Pivot table refresher must not be null");
        Objects.requireNonNull(formulaCalculator, "Formula calculator must not be null");
        Objects.requireNonNull(formatter, "Cell formatter must not be null");

        return new ExcelCreationService(
                templateLoader,
                dataFetcher,
                pivotTableRefresher,
                formulaCalculator,
                formatter
        );
    }

    /**
     * Streams data from the specified query/command to the provided output stream.
     *
     * @param output The target output stream to write data to. Must not be null.
     * @throws IllegalArgumentException if query is null/empty or output is null
     */
    void stream(Report query, OutputStream output);

    interface WorkbookDataStreamer extends DataStreamer {

        /**
         * Streams data from the db into the target workbook, applying all mappings
         * and transformations defined in the report.
         *
         * @param report   The report definition containing data and mapping configuration
         * @param workbook The target workbook to be populated
         * @throws IllegalArgumentException if either argument is null
         */
        void stream(Report report, Workbook workbook);

        /**
         * Streams data from the db directly to an output stream,
         * automatically handling template loading and resource management.
         *
         * @param report The report definition to execute
         * @param output The target output stream for the generated workbook
         * @throws TemplateResourceException if the template cannot be loaded
         * @throws IllegalStateException     if there's an error writing to the stream
         * @throws IllegalArgumentException  if either argument is null
         */
        default void stream(Report report, OutputStream output) {
            Objects.requireNonNull(report, "Report must not be null");
            Objects.requireNonNull(output, "Output stream must not be null");

            try (var analitics = resolveTemplate(report);
                 var targetWorkbook = createEmpty()) {


                stream(report, targetWorkbook);

                int sheetCount = analitics.getNumberOfSheets();
                for (int i = 0; i < 5; i++) {
                    Sheet sourceSheet = analitics.getSheetAt(i);
                    moveSheet(analitics, targetWorkbook, sourceSheet.getSheetName());
                }

                targetWorkbook.write(output);
            } catch (IOException e) {
                throw new ExcelTemplateCreationException(e, "Failed to generate Excel report '%s'", report.getName());
            }
        }

        private void moveSheet(Workbook sourceWorkbook, Workbook targetWorkbook, String sheetName) {
            Sheet sourceSheet = sourceWorkbook.getSheet(sheetName);
            if (sourceSheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in source workbook");
            }

            XSSFWorkbook xssfTargetWorkbook = ((SXSSFWorkbook) targetWorkbook).getXSSFWorkbook();
            XSSFSheet targetSheet = xssfTargetWorkbook.createSheet(sheetName);
            targetSheet.setAutobreaks(false);

            copySheetContent(sourceSheet, targetSheet);
            copyColumnWidths(sourceSheet, targetSheet);
            copyMergedRegions(sourceSheet, targetSheet);

            copyPivotTables(
                    (XSSFWorkbook) sourceWorkbook,
                    (XSSFWorkbook) xssfTargetWorkbook,
                    (XSSFSheet) sourceSheet,
                    targetSheet
            );
        }

        private void copyPivotTables(XSSFWorkbook sourceWorkbook, XSSFWorkbook xssfTargetWorkbook,
                                     XSSFSheet sourceSheet, XSSFSheet targetSheet) {
            List<XSSFPivotTable> pivotTables = sourceSheet.getPivotTables();

            for (XSSFPivotTable pivotTable : pivotTables) {
                AreaReference sourceArea = pivotTable.getPivotCacheDefinition().getPivotArea(sourceWorkbook);

                CellReference topLeft = sourceArea.getFirstCell();
                CellReference bottomRight = sourceArea.getLastCell();
                AreaReference targetArea = new AreaReference(topLeft, bottomRight, SpreadsheetVersion.EXCEL2007);

                XSSFRow row = targetSheet.getRow(0);
                short col = targetArea.getLastCell().getCol();
                for (int i = 0; i <= col; i++) {
                    row.createCell(i);
                }

                XSSFPivotTable targetPivot = targetSheet.createPivotTable(targetArea, topLeft);

                copyPivotTableLayout(pivotTable, targetPivot);


                CTPivotTableDefinition srcDef = pivotTable.getCTPivotTableDefinition();
                CTPivotTableDefinition destDef = targetPivot.getCTPivotTableDefinition();
                destDef.set(srcDef);

                XSSFPivotCacheDefinition sourceCacheDef = pivotTable.getPivotCacheDefinition();
                CTPivotCacheDefinition sourceCTCacheDef = sourceCacheDef.getCTPivotCacheDefinition();

                sourceCacheDef.getCTPivotCacheDefinition().setRefreshOnLoad(false);
                targetPivot.getPivotCacheDefinition().getCTPivotCacheDefinition().setRefreshOnLoad(false);


                XSSFPivotCacheDefinition targetCacheDef = targetPivot.getPivotCacheDefinition();
                CTPivotCacheDefinition targetCTCacheDef = targetCacheDef.getCTPivotCacheDefinition();

                targetCTCacheDef.set(sourceCTCacheDef);

                int newCacheId = xssfTargetWorkbook.getPivotTables().size() + 1;
                targetCTCacheDef.setId(String.valueOf(newCacheId));

                CTPivotTableDefinition pivotDef = targetPivot.getCTPivotTableDefinition();
                pivotDef.setCacheId(newCacheId);

                if (targetCTCacheDef.getCacheSource() != null && targetCTCacheDef.getCacheSource().getWorksheetSource() != null) {
                    String s = targetPivot.getPivotCacheDefinition().getPivotArea(xssfTargetWorkbook).formatAsString();
                    targetCTCacheDef.getCacheSource().getWorksheetSource().setRef(
                            s
                    );
                }

                xssfTargetWorkbook.getRelationParts().stream()
                        .filter(part -> part.getRelationship().getRelationshipType().contains(PACKAGE_RELATIONSHIP_TYPE))
                        .map(part -> (XSSFPivotCacheDefinition) xssfTargetWorkbook.getRelationById(part.getRelationship().getId()))
                        .forEach(cache -> cache.getCTPivotCacheDefinition().setRefreshOnLoad(true));
            }
        }


        private void copyPivotTableLayout(XSSFPivotTable source, XSSFPivotTable target) {
            CTPivotTableDefinition srcDef = source.getCTPivotTableDefinition();
            CTPivotTableDefinition destDef = target.getCTPivotTableDefinition();

            destDef.setPivotFields((CTPivotFields) srcDef.getPivotFields().copy());
            destDef.setRowFields((CTRowFields) srcDef.getRowFields().copy());
            destDef.setColFields(srcDef.getColFields());
            destDef.setDataFields(srcDef.getDataFields());

            destDef.setPivotTableStyleInfo((CTPivotTableStyle) srcDef.getPivotTableStyleInfo().copy());
            destDef.setLocation(srcDef.getLocation());

            if (srcDef.isSetPageFields()) {
                destDef.setPageFields(srcDef.getPageFields());
            }

            destDef.setDataOnRows(srcDef.getDataOnRows());
            destDef.setApplyNumberFormats(srcDef.getApplyNumberFormats());
        }

        private void copySheetContent(Sheet sourceSheet, Sheet targetSheet) {
//            Map<CellStyle, CellStyle> styleCache = new HashMap<>();

            for (int i = 0; i <= sourceSheet.getLastRowNum(); i++) {
                Row sourceRow = sourceSheet.getRow(i);
                if (sourceRow != null) {
                    Row targetRow = targetSheet.createRow(i);
                    targetRow.setHeight(sourceRow.getHeight());
                    targetRow.setZeroHeight(sourceRow.getZeroHeight());
                    targetRow.setHeightInPoints(sourceRow.getHeightInPoints());

                    for (int j = 0; j < sourceRow.getLastCellNum(); j++) {
                        Cell sourceCell = sourceRow.getCell(j);
                        if (sourceCell != null) {
                            Cell targetCell = targetRow.createCell(j);

                            copyCell(sourceCell, targetCell);

//                            CellStyle sourceStyle = sourceCell.getCellStyle();
//                            CellStyle targetStyle;
//
//                            if (styleCache.containsKey(sourceStyle)) {
//                                targetStyle = styleCache.get(sourceStyle);
//                            } else {
//                                targetStyle = targetSheet.getWorkbook().createCellStyle();
//                                targetStyle.cloneStyleFrom(sourceStyle);
//                                copyBorders(sourceStyle, targetStyle);
//                                styleCache.put(sourceStyle, targetStyle);
//                            }

//                            targetCell.setCellStyle(targetStyle);
                        }
                    }
                }
            }
        }

//        private void copyBorders(CellStyle sourceStyle, CellStyle targetStyle) {
//            targetStyle.setBorderTop(sourceStyle.getBorderTop());
//            targetStyle.setTopBorderColor(sourceStyle.getTopBorderColor());
//
//            targetStyle.setBorderBottom(sourceStyle.getBorderBottom());
//            targetStyle.setBottomBorderColor(sourceStyle.getBottomBorderColor());
//
//            targetStyle.setBorderLeft(sourceStyle.getBorderLeft());
//            targetStyle.setLeftBorderColor(sourceStyle.getLeftBorderColor());
//
//            targetStyle.setBorderRight(sourceStyle.getBorderRight());
//            targetStyle.setRightBorderColor(sourceStyle.getRightBorderColor());
//        }

        private void copyCell(Cell sourceCell, Cell targetCell) {
            // Copy cell style
            CellStyle newStyle = targetCell.getSheet().getWorkbook().createCellStyle();
            newStyle.cloneStyleFrom(sourceCell.getCellStyle());
            targetCell.setCellStyle(newStyle);

            // Copy cell value
            switch (sourceCell.getCellType()) {
                case STRING:
                    targetCell.setCellValue(sourceCell.getStringCellValue());
                    break;
                case NUMERIC:
                    targetCell.setCellValue(sourceCell.getNumericCellValue());
                    break;
                case BOOLEAN:
                    targetCell.setCellValue(sourceCell.getBooleanCellValue());
                    break;
                case FORMULA:
                    targetCell.setCellFormula(sourceCell.getCellFormula());
                    break;
                case BLANK:
                    targetCell.setBlank();
                    break;
                case ERROR:
                    targetCell.setCellErrorValue(sourceCell.getErrorCellValue());
                    break;
                default:
                    break;
            }
        }

        private void copyColumnWidths(Sheet sourceSheet, Sheet targetSheet) {
            for (int i = 0; i < sourceSheet.getRow(0).getLastCellNum(); i++) {
                targetSheet.setColumnWidth(i, sourceSheet.getColumnWidth(i));
            }
        }

        private void copyMergedRegions(Sheet sourceSheet, Sheet targetSheet) {
            for (int i = 0; i < sourceSheet.getNumMergedRegions(); i++) {
                targetSheet.addMergedRegion(sourceSheet.getMergedRegion(i));
            }
        }



//        private void copySheet(Sheet sourceSheet, Sheet targetSheet) {
//            // Copy each row
//            for (int i = 0; i <= sourceSheet.getLastRowNum(); i++) {
//                Row sourceRow = sourceSheet.getRow(i);
//                if (sourceRow != null) {
//                    Row targetRow = targetSheet.createRow(i);
//
//                    // Copy each cell
//                    for (int j = 0; j < sourceRow.getLastCellNum(); j++) {
//                        Cell sourceCell = sourceRow.getCell(j);
//                        if (sourceCell != null) {
//                            Cell targetCell = targetRow.createCell(j);
//                            copyCell(sourceCell.getSheet().getWorkbook(), targetCell.getSheet().getWorkbook(), sourceCell, targetCell);
//                        }
//                    }
//                }
//            }
//
//            for (int i = 0; i < sourceSheet.getRow(0).getLastCellNum(); i++) {
//                targetSheet.setColumnWidth(i, sourceSheet.getColumnWidth(i));
//            }
//
//            for (int i = 0; i < sourceSheet.getNumMergedRegions(); i++) {
//                CellRangeAddress mergedRegion = sourceSheet.getMergedRegion(i);
//                targetSheet.addMergedRegion(mergedRegion);
//            }
//
//        }

//
//
//        private void copyCell(Workbook sourceWorkbook, Workbook targetWorkbook, Cell sourceCell, Cell targetCell) {
//            CellStyle sourceStyle = sourceCell.getCellStyle();
//            CellStyle targetStyle = targetWorkbook.createCellStyle();
//            targetStyle.cloneStyleFrom(sourceStyle);
//            targetCell.setCellStyle(targetStyle);
//
//            // Copy cell value based on type
//            switch (sourceCell.getCellType()) {
//                case STRING:
//                    targetCell.setCellValue(sourceCell.getStringCellValue());
//                    break;
//                case NUMERIC:
//                    targetCell.setCellValue(sourceCell.getNumericCellValue());
//                    break;
//                case BOOLEAN:
//                    targetCell.setCellValue(sourceCell.getBooleanCellValue());
//                    break;
//                case FORMULA:
//                    targetCell.setCellFormula(sourceCell.getCellFormula());
//                    break;
//                case BLANK:
//                    targetCell.setBlank();
//                    break;
//                case ERROR:
//                    targetCell.setCellErrorValue(sourceCell.getErrorCellValue());
//                    break;
//                default:
//                    break;
//            }
//        }


        /**
         * Resolves the appropriate template workbook for the given report.
         * Implementations should override this if they need custom template resolution logic.
         *
         * @param report The report requesting the template
         * @return A loaded workbook template
         * @throws TemplateResourceException if the template cannot be loaded
         */
        Workbook resolveTemplate(Report report);
//org.apache.poi.xssf.streaming.SXSSFCell.getCellStyle ()
        Workbook createEmpty();
    }
}

