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
            "small-csv",   "00000000-0000-0000-0000-000000000001",
            "medium-csv",  "00000000-0000-0000-0000-000000000002",
            "large-csv",   "00000000-0000-0000-0000-000000000003",
            "small-excel", "00000000-0000-0000-0000-000000000004",
            "medium-excel","00000000-0000-0000-0000-000000000005",
            "large-excel", "00000000-0000-0000-0000-000000000006"
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