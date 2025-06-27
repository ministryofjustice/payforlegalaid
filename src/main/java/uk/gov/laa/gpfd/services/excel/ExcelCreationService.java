package uk.gov.laa.gpfd.services.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import uk.gov.laa.gpfd.dao.JdbcWorkbookDataStreamer;
import uk.gov.laa.gpfd.model.Mapping;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.services.TemplateService;
import uk.gov.laa.gpfd.services.DataStreamer.WorkbookDataStreamer;
import uk.gov.laa.gpfd.services.excel.formatting.CellFormatter;

import java.util.Map;

/**
 * The class is a Spring component responsible for generating Excel workbooks
 * based on a provided {@link Report} and its associated template. It uses a combination of services to
 * load templates, fetch data, and write data to the appropriate sheets in the workbook.
 *
 * <p>This class leverages dependency injection to access the required services:
 * <ul>
 *     <li>{@link TemplateService} for loading Excel templates.</li>
 *     <li>{@link JdbcWorkbookDataStreamer<Map<String, Object>>} for fetching data from the database based on report queries.</li>
 * </ul>
 */
@Slf4j
public record ExcelCreationService(
        TemplateService templateLoader,
        JdbcWorkbookDataStreamer jdbcWorkbookDataStreamer,
        CellFormatter formatter
) implements WorkbookDataStreamer {

    @Override
    public Workbook resolveTemplate(Report report) {
        return templateLoader.findTemplateById(report.getTemplateDocument());
    }

    @Override
    public Workbook createEmpty(Report report) {
        return templateLoader.createEmpty(report);
    }

    /**
     * Updates the provided workbook with data fetched from the database based on the queries defined
     * in the {@link Report}. This method iterates through the report's queries, finds the corresponding
     * sheets in the workbook, and writes the fetched data to those sheets.
     *
     * @param workbook the workbook to update with data
     * @param report   the report containing the queries and field attributes
     */
    @Override
    public void stream(Report report, Workbook workbook) {
        for (var query : report.extractAllMappings()) {
            var sheet = workbook.createSheet(query.getExcelSheet().getName());
            setupSheetHeader(sheet, query);
            jdbcWorkbookDataStreamer.queryToSheet(sheet, query);
        }
    }

    void setupSheetHeader(Sheet sheet, Mapping query) {
        var headerRow = sheet.createRow(0);
        var columnIndex = 0;

        for (var column : query.getExcelSheet().getFieldAttributes()) {
            var cell = headerRow.createCell(columnIndex++);
            cell.setCellValue(column.getMappedName());

            formatter.applyFormatting(sheet, cell, column);
        }
    }

}
