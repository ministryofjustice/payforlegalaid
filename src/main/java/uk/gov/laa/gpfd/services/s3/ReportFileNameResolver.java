package uk.gov.laa.gpfd.services.s3;


import lombok.AllArgsConstructor;
import uk.gov.laa.gpfd.exception.FileDownloadException.ReportNotSupportedForDownloadException;

import java.util.Map;
import java.util.UUID;

import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP000;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP012;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP013;

public class ReportFileNameResolver {

    @AllArgsConstructor
    private static class FileDetails {
        String fileName;
        String folder;
        String prefix;
    }

    private static final String DAILY_FOLDER = "daily";
    private static final String MONTHLY_FOLDER = "monthly";
    private static final Map<UUID, FileDetails> fileMap = Map.of(
            ID_REP000, new FileDetails("report_000.csv", MONTHLY_FOLDER, "report_000"),
            ID_REP012, new FileDetails("report_012.csv", DAILY_FOLDER, "report_012"),
            ID_REP013, new FileDetails("report_013.csv", DAILY_FOLDER, "report_013")
    );

    /**
     * Fetch the file name for a given report ID
     *
     * @param id - report ID
     * @return - completed filename for this report
     */
    String getFileNameFromId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Report ID cannot be null or blank");
        }

        if (fileMap.containsKey(id)) {
            return fileMap.get(id).fileName;
        } else {
            throw new ReportNotSupportedForDownloadException(id);
        }
    }

    /**
     * Fetch the folder for a given report ID
     *
     * @param id - report ID
     * @return - completed filename for this report
     */
    String getFolderFromId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Report ID cannot be null or blank");
        }

        if (fileMap.containsKey(id)) {
            return fileMap.get(id).folder;
        } else {
            throw new ReportNotSupportedForDownloadException(id);
        }
    }

    String getPrefixFromId(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Report ID cannot be null or blank");
        }

        if (fileMap.containsKey(id)) {
            return "reports/" + fileMap.get(id).folder + "/" + fileMap.get(id).prefix;
        } else {
            throw new ReportNotSupportedForDownloadException(id);
        }
    }

}
