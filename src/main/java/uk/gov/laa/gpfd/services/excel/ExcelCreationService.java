package uk.gov.laa.gpfd.services.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import uk.gov.laa.gpfd.dao.stream.StreamingDao;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.services.DataStreamer;
import uk.gov.laa.gpfd.services.TemplateService;
import uk.gov.laa.gpfd.services.excel.editor.FormulaCalculator;
import uk.gov.laa.gpfd.services.excel.editor.PivotTableRefresher;
import uk.gov.laa.gpfd.services.excel.editor.SheetDataWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static uk.gov.laa.gpfd.exception.TemplateResourceException.ExcelTemplateCreationException;
import static uk.gov.laa.gpfd.services.excel.util.SheetUtils.findSheetByName;

/**
 * The class is a Spring component responsible for generating Excel workbooks
 * based on a provided {@link Report} and its associated template. It uses a combination of services to
 * load templates, fetch data, and write data to the appropriate sheets in the workbook.
 *
 * <p>This class leverages dependency injection to access the required services:
 * <ul>
 *     <li>{@link TemplateService} for loading Excel templates.</li>
 *     <li>{@link StreamingDao<Map<String, Object>>} for fetching data from the database based on report queries.</li>
 *     <li>{@link SheetDataWriter} for writing the fetched data to the appropriate sheets in the workbook.</li>
 * </ul>
 */
@Slf4j
public record ExcelCreationService(
        TemplateService templateLoader,
        StreamingDao<Map<String, Object>> dataFetcher,
        SheetDataWriter sheetDataWriter,
        PivotTableRefresher pivotTableRefresher,
        FormulaCalculator formulaCalculator
) implements DataStreamer {

    /**
     * Builds an Excel workbook based on the provided {@link Report}. This method loads the template
     * associated with the report, updates it with data fetched from the database, and returns the
     * final workbook.
     *
     * @param report the report containing the template ID, queries, and field attributes
     * @param output The target output stream to write data to. Must not be null.
     */
    @Override
    public void stream(Report report, OutputStream output) {
        log.debug("Retrieving template for report: {}", report.getName());
        try(var workbook = templateLoader.findTemplateById(report.getTemplateSecureDocumentId())) {
            log.debug("Updating template with data for report: {}", report.getName());
            updateTemplateWithData(workbook, report);
            workbook.write(output);
            log.debug("Successfully built Excel workbook for report: {}", report.getName());
        } catch (IOException e) {
            throw new ExcelTemplateCreationException(e, "Failed to generate Excel report '%s' due to I/O error", report.getName());
        }
    }

    /**
     * Updates the provided workbook with data fetched from the database based on the queries defined
     * in the {@link Report}. This method iterates through the report's queries, finds the corresponding
     * sheets in the workbook, and writes the fetched data to those sheets.
     *
     * @param workbook the workbook to update with data
     * @param report   the report containing the queries and field attributes
     */
    private void updateTemplateWithData(Workbook workbook, Report report) {
        for (var query : report.extractAllMappings()) {
            findSheetByName(workbook, query.getSheetName())
                    .ifPresent(sheet -> sheetDataWriter.writeDataToSheet(
                            sheet,
                            dataFetcher.queryForStream(query.getQuery()),
                            query.getFieldAttributes()
                    ));
        }

        pivotTableRefresher.refreshPivotTables(workbook);
        formulaCalculator.evaluateAllFormulaCells(workbook);
    }

}
