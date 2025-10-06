package uk.gov.laa.gpfd.services.s3;


import uk.gov.laa.gpfd.exception.ReportNotSupportedForDownloadException;

import java.util.UUID;

public class ReportFileNameResolver {

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

        return switch (id.toString()) {
            case "523f38f0-2179-4824-b885-3a38c5e149e8" -> "report_000.csv";
            case "cc55e276-97b0-4dd8-a919-26d4aa373266" -> "report_012.csv";
            case "aca2120c-8f82-45a8-a682-8dedfb7997a7" -> "report_013.csv";

            // TODO these are here to test things on dev in short term. can be removed when we have real test examples and won't work on uat/prod
            case "00d5a89d-a28f-44b5-ae26-070ce86b0dae" -> "oneMbCsv.csv";
            case "0548ee0a-3532-4b50-8fed-372cef9bf493" -> "elevenMbCsv.csv";
            case "d7306ae6-6a29-4fcc-98b6-4a77c55881c1" -> "seventyMbCsv.csv";
            case "0dda98c9-e949-4816-9c4a-fbbf2af1295d" -> "hundredMbCsv.csv";

            default -> throw new ReportNotSupportedForDownloadException(id);
        };

    }

}
