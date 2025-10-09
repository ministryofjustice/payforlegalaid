package uk.gov.laa.gpfd.services.s3;


import uk.gov.laa.gpfd.exception.ReportNotSupportedForDownloadException;

import java.util.Map;
import java.util.UUID;

import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP000;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP012;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP013;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_TEMP_100MB;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_TEMP_11MB;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_TEMP_1MB;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_TEMP_70MB;

public class ReportFileNameResolver {

    private final static Map<UUID, String> fileMap = Map.of(
            ID_REP000, "report_000.csv",
            ID_REP012, "report_012.csv",
            ID_REP013, "report_013.csv",
            // These are here to test things on dev in short term. can be removed when we have real test examples and won't work on uat/prod
            ID_TEMP_1MB, "oneMbCsv.csv",
            ID_TEMP_11MB, "elevenMbCsv.csv",
            ID_TEMP_70MB, "seventyMbCsv.csv",
            ID_TEMP_100MB, "hundredMbCsv.csv"
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
            return fileMap.get(id);
        } else {
            throw new ReportNotSupportedForDownloadException(id);
        }
    }

}
