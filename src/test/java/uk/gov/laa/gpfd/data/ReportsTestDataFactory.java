package uk.gov.laa.gpfd.data;

import uk.gov.laa.gpfd.model.ImmutableReport;
import uk.gov.laa.gpfd.model.ImmutableReportOutputType;
import uk.gov.laa.gpfd.model.ImmutableReportQuery;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.model.ReportQuery;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ReportsTestDataFactory {

    public static Report createTestReport() {
        return createTestReport(UUID.randomUUID());
    }

    public static Report createTestReport(UUID reportId) {
        return ImmutableReport.builder()
                .reportId(reportId)
                .name("Test Report")
                .templateSecureDocumentId("doc-123")
                .reportCreationTime(Timestamp.from(Instant.now()))
                .lastDatabaseRefreshDate(Timestamp.from(Instant.now()))
                .description("Test Description")
                .numDaysToKeep(30)
                .reportOutputType(ImmutableReportOutputType.builder()
                        .description("foo")
                        .id(reportId)
                        .extension("xlsx")
                        .build())
                .reportCreatorName("Creator Name")
                .reportCreatorEmail("creator@example.com")
                .reportOwnerId(UUID.randomUUID())
                .reportOwnerName("Owner Name")
                .reportOwnerEmail("owner@example.com")
                .fileName("test_report.pdf")
                .active(true)
                .queries(Collections.emptyList())
                .build();
    }

    public static Report createTestReportWithQuery() {
        var query1 = ImmutableReportQuery.builder()
                .tabName("Sheet1")
                .query("SELECT * FROM ANY_REPORT.DATA")
                .build();
        return createTestReport(UUID.randomUUID().toString(), List.of(query1) );
    }

    public static Report createTestReport(String secureDocumentId, Collection<ReportQuery> queries) {
        return ImmutableReport.builder()
                .reportId(UUID.randomUUID())
                .name("Test Report")
                .templateSecureDocumentId(secureDocumentId)
                .reportCreationTime(Timestamp.from(Instant.now()))
                .lastDatabaseRefreshDate(Timestamp.from(Instant.now()))
                .description("Test Description")
                .numDaysToKeep(30)
                .reportOutputType(ImmutableReportOutputType.builder()
                        .description("foo")
                        .id(UUID.randomUUID())
                        .extension("csv")
                        .build())
                .reportCreatorName("Creator Name")
                .reportCreatorEmail("creator@example.com")
                .reportOwnerId(UUID.randomUUID())
                .reportOwnerName("Owner Name")
                .reportOwnerEmail("owner@example.com")
                .fileName("test_report.pdf")
                .active(true)
                .queries(queries)
                .build();
    }

}