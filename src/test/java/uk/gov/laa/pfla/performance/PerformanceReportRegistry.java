package uk.gov.laa.pfla.performance;

import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;

import java.util.*;
import java.util.stream.Collectors;

public class PerformanceReportRegistry {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceReportRegistry.class);

    // Fixed benchmark report IDs (manually curated by file size)
    private static final Map<String, String> REPORT_IDS = Map.of(
            "small-csv",   "c4ba2e89-c106-48a7-8e1d-7c19dbd7710d", //002
            "medium-csv",  "55daf3c1-28f0-4260-9396-2ee6d537abab", //014
            "large-csv",   "c4ba2e89-c106-48a7-8e1d-7c19dbd7710d" //000
    );

    public static void validateReportsExist(List<ReportsGet200ResponseReportListInner> loaded) {
        Set<String> availableIds = loaded.stream()
                .map(ReportsGet200ResponseReportListInner::getId)
                .filter(Objects::nonNull)
                .map(UUID::toString)
                .collect(Collectors.toSet());

        for (String expectedId : REPORT_IDS.values()) {
            if (!availableIds.contains(expectedId)) {
                logger.error(() -> "Missing benchmark report ID: " + expectedId);
                throw new IllegalStateException(
                        "Performance benchmark report ID missing: " + expectedId
                );
            }
        }

        logger.info(() -> "Performance benchmark reports validated successfully");
    }

    public static Optional<String> getReportIdBySizeAndFormat(String size, String format) {
        String key = size.toLowerCase() + "-" + format.toLowerCase();

        return Optional.ofNullable(REPORT_IDS.get(key));
    }
}