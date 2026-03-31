package uk.gov.laa.gpfd.services.s3;


import lombok.AllArgsConstructor;
import uk.gov.laa.gpfd.exception.FileDownloadException.ReportNotSupportedForDownloadException;

import java.util.Map;
import java.util.UUID;

import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP000;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP012;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP013;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP014;

/*
 Class for activities related to finding the report name for tactical solution reports
 */
public class ReportFileNameResolver {

    @AllArgsConstructor
    private static class FileDetails {
        String folder;
        String prefix;
    }

    private static final String DAILY_FOLDER = "daily";
    private static final String MONTHLY_FOLDER = "monthly";
    // When we add more tactical solution reports - should these details be transitioned to the db?
    private static final Map<UUID, FileDetails> fileMap = Map.of(
            ID_REP000, new FileDetails(MONTHLY_FOLDER, "report_000"),
            ID_REP012, new FileDetails(DAILY_FOLDER, "report_012"),
            ID_REP013, new FileDetails(DAILY_FOLDER, "report_013"),
            ID_REP014, new FileDetails(DAILY_FOLDER, "report_014")
    );

    /**
     * Returns prefix needed to search for file in S3, e.g. reports/daily/report_012
     *
     * @param id UUID of report to build prefix for
     * @return prefix needed to search in S3
     */
    String getS3PrefixFromId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Report ID cannot be null or blank");
        }

        if (fileMap.containsKey(id)) {
            var reportDetails = fileMap.get(id);
            return "reports/" + reportDetails.folder + "/" + reportDetails.prefix;
        } else {
            throw new ReportNotSupportedForDownloadException(id);
        }
    }

}
