package uk.gov.laa.gpfd.services.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import uk.gov.laa.gpfd.dao.ReportViewsDao;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.model.ReportQuery;
import uk.gov.laa.gpfd.services.TemplateService;
import uk.gov.laa.gpfd.services.excel.editor.SheetDataWriter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static uk.gov.laa.gpfd.services.excel.util.SheetUtils.findSheetByName;

/**
 * The class is a Spring component responsible for generating Excel workbooks
 * based on a provided {@link Report} and its associated template. It uses a combination of services to
 * load templates, fetch data, and write data to the appropriate sheets in the workbook.
 *
 * <p>This class leverages dependency injection to access the required services:
 * <ul>
 *     <li>{@link TemplateService} for loading Excel templates.</li>
 *     <li>{@link ReportViewsDao} for fetching data from the database based on report queries.</li>
 *     <li>{@link SheetDataWriter} for writing the fetched data to the appropriate sheets in the workbook.</li>
 * </ul>
 */
@Slf4j
@Component
public record ExcelCreationService(
        TemplateService templateLoader,
        ReportViewsDao dataFetcher,
        SheetDataWriter sheetDataWriter
) {

    /**
     * Builds an Excel workbook based on the provided {@link Report}. This method loads the template
     * associated with the report, updates it with data fetched from the database, and returns the
     * final workbook.
     *
     * @param report the report containing the template ID, queries, and field attributes
     * @return the generated {@link Workbook} with data populated
     */
    public Workbook buildExcel(Report report) {
        log.debug("Retrieving template for report: {}", report.getName());
        var workbook = templateLoader.findTemplateById(report.getTemplateSecureDocumentId());
        log.debug("Updating template with data for report: {}", report.getName());
        updateTemplateWithData(workbook, report);
        log.debug("Successfully built Excel workbook for report: {}", report.getName());
        return workbook;
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
        var futures = report.getQueries().parallelStream()
                .flatMap(query -> findSheetByName(workbook, query.getTabName())
                        .stream()
                        .map(sheet -> new SheetToQuery(sheet, query)))
                .map(sheetToQuery -> fetchData(sheetToQuery)
                        .thenCompose(data -> writeDataToSheet(sheetToQuery, data)))
                .toList();

        allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    /**
     * Fetches data from the database asynchronously for a given SheetToQuery pair.
     */
    private CompletableFuture<List<Map<String, Object>>> fetchData(SheetToQuery sheetToQuery) {
        return supplyAsync(() -> dataFetcher.callDataBase(sheetToQuery.reportQuery().getQuery()));
    }

    /**
     * Writes data to the sheet asynchronously after fetching it.
     */
    private CompletableFuture<Void> writeDataToSheet(SheetToQuery sheetToQuery, List<Map<String, Object>> data) {
        return runAsync(() -> sheetDataWriter.writeDataToSheet(
                sheetToQuery.sheet(),
                data,
                sheetToQuery.reportQuery().getFieldAttributes()
        ));
    }

    private record SheetToQuery(Sheet sheet, ReportQuery reportQuery) {}

}
