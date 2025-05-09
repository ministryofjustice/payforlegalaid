package uk.gov.laa.gpfd.data;

import java.util.UUID;
import uk.gov.laa.gpfd.model.ReportDetails;

/**
 * Factory class to generate test data for instances of {@link ReportDetails}.
 * This class provides methods to generate valid instances of {@link ReportDetails}
 * with default or custom data, which can be useful for unit tests, mock data generation, or other testing purposes.
 * <p>
 * The generated instances represent a report response and include common report details such as report ID,
 * report name and extension.
 * </p>
 */

public class ReportDetailsTestDataFactory {
  public static ReportDetails aValidReportResponse(UUID id, String reportName, String extension) {
    return ReportDetails.builder()
        .id(id)
        .name(reportName)
        .extension(extension)
        .reportDownloadUrl(ReportsTestDataFactory.TEST_DOWNLOAD_URL)
        .build();
  }

}
