package uk.gov.laa.gpfd.services;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import uk.gov.laa.gpfd.dao.ReportDao;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.services.excel.ExcelCreationService;

import java.util.UUID;

/**
 * The class is a Spring component responsible for creating Excel workbooks
 * based on a given report ID. It combines the functionality of {@link ExcelCreationService} to build
 * the workbook and {@link ReportDao} to fetch the report details.
 *
 * <p>This service acts as a bridge between the data layer ({@link ReportDao}) and the Excel creation
 * layer ({@link ExcelCreationService}), providing a streamlined way to generate Excel files for reports.
 */
@Component
public record ExcelService(
        ExcelCreationService excelCreationService,
        ReportDao reportDao
) {

    /**
     * Creates an Excel workbook for the report associated with the provided ID. This method fetches
     * the report details using {@link ReportDao} and builds the workbook using {@link ExcelCreationService}.
     *
     * @param id the unique identifier (UUID) of the report
     * @return a {@link Pair} containing the {@link Report} and the generated {@link Workbook}
     * @throws RuntimeException if the report is not found for the provided ID
     */
    public Pair<Report, Workbook> createExcel(UUID id) {
        return reportDao.fetchReportById(id)
                .map(report -> {
                    Workbook workbook = excelCreationService.buildExcel(report);
                    return Pair.of(report, workbook);
                })
                .orElseThrow(() -> new RuntimeException("Report not found for ID: " + id));
    }
}