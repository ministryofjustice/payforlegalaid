package uk.gov.laa.gpfd.mapper;

import org.springframework.stereotype.Component;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;

/**
 * Utility class responsible for mapping a {@link Report} object to a {@link ReportsGet200ResponseReportListInner} object.
 * <p>
 * This class provides a method to transform a {@link Report} entity (which holds CSV to SQL mapping details)
 * into a response object that can be returned in an API response.
 * </p>
 */
@Component
public final class ReportsGet200ResponseReportListInnerMapper implements ResourceResponseMapper<Report, ReportsGet200ResponseReportListInner> {

    /**
     * Maps a {@link Report} object to a {@link ReportsGet200ResponseReportListInner} object.
     * <p>
     * This method transforms a given {@link Report} entity into a response object used by the service layer
     * to expose report details such as report name, SQL query, and other metadata to the client.
     * </p>
     *
     * @param reportData the {@link Report} object containing the data to be mapped
     * @return a {@link ReportsGet200ResponseReportListInner} object populated with data from the provided {@link Report}
     */
    public ReportsGet200ResponseReportListInner map(Report reportData) {
        var result = new ReportsGet200ResponseReportListInner();
        result.setId(reportData.getId());
        result.setReportName(reportData.getName());
        result.description(reportData.getDescription());

        return result;
    }
}
