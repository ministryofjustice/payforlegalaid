package uk.gov.laa.gpfd.data;

import uk.gov.laa.gpfd.model.ImmutableReportsTracking;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.model.ReportsTracking;

import java.sql.Timestamp;
import java.time.Instant;


public class ReportsTrackingTestDataFactory {
    public static ReportsTracking createBasicReportTracking(Report report) {
        return ImmutableReportsTracking.builderFor(report)
                .creationDate(Timestamp.from(Instant.now()))
                .reportCreator("test-creator@example.com")
                .reportUrl("https://reports.example.com/test-report")
                .build();
    }

    public static ReportsTracking createMinimalReportTracking(Report report) {
        return ImmutableReportsTracking.builderFor(report).build();
    }

    public static ReportsTracking createWithNullOptionalFields(Report report) {
        return ImmutableReportsTracking.builderFor(report)
                .creationDate(null)
                .reportCreator(null)
                .reportUrl(null)
                .build();
    }

    public static ReportsTracking createWithFutureDate(Report report) {
        return ImmutableReportsTracking.builderFor(report)
                .creationDate(Timestamp.from(Instant.now().plusSeconds(3600)))
                .reportCreator("future-creator@example.com")
                .reportUrl("https://reports.example.com/future-report")
                .build();
    }

    public static ReportsTracking createWithLongCreatorName(Report report) {
        return ImmutableReportsTracking.builderFor(report)
                .creationDate(Timestamp.from(Instant.now()))
                .reportCreator("very.long.creator.name.with.many.parts@very.long.domain.example.com")
                .reportUrl("https://reports.example.com/long-creator-report")
                .build();
    }

    public static ReportsTracking createWithSpecialCharacters(Report report) {
        return ImmutableReportsTracking.builderFor(report)
                .creationDate(Timestamp.from(Instant.now()))
                .reportCreator("special!chars#creator@example.com")
                .reportUrl("https://reports.example.com/special-chars-report?param=value&test=1")
                .build();
    }

    public static ReportsTracking createWithDifferentUrlFormat(Report report) {
        return ImmutableReportsTracking.builderFor(report)
                .creationDate(Timestamp.from(Instant.now()))
                .reportCreator("url-test-creator@example.com")
                .reportUrl("http://localhost:8080/reports/test")
                .build();
    }
}
