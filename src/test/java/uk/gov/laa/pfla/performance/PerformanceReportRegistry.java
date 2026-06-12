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
            "small-csv",   "f46b4d3d-c100-429a-bf9a-6c3305dbdbf5",
            "medium-csv",  "f46b4d3d-c100-429a-bf9a-6c3305dbdbf8",
            "large-csv",   "f46b4d3d-c100-429a-bf9a-6c3305dbdbfb",
            "small-excel", "f46b4d3d-c100-429a-bf9a-223305dbdbfb",
            "medium-excel","7073dd13-e325-4863-a05c-a049a815d1f7",
            "large-excel", "a017241a-359f-4fdb-a0cd-7f28f1946ef1"
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