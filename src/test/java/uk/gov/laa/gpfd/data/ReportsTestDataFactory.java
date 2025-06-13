package uk.gov.laa.gpfd.data;

import uk.gov.laa.gpfd.model.*;
import uk.gov.laa.gpfd.model.excel.ExcelTemplate;
import uk.gov.laa.gpfd.model.excel.ImmutableExcelSheet;

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
                .id(reportId)
                .name("Test Report")
                .templateDocument(ExcelTemplate.fromString("b36f9bbb-1178-432c-8f99-8090e285f2d3"))
                .creationTime(Timestamp.from(Instant.now()))
                .lastDatabaseRefreshDate(Timestamp.from(Instant.now()))
                .description("Test Description")
                .numDaysToKeep(30)
                .outputType(ImmutableReportOutputType.builder()
                        .description("foo")
                        .id(reportId)
                        .fileExtension(FileExtension.XLSX)
                        .build())
                .creator(ImmutableReportCreator.newBuilder()
                        .withId(UUID.randomUUID())
                        .withName("Owner Name")
                        .withEmail("owner@example.com")
                        .create())
                .owner(ImmutableReportOwner.newBuilder()
                        .withName("Creator Name")
                        .withEmail("creator@example.com")
                        .create())
                .outputFileName("test_report")
                .active(true)
                .queries(Collections.emptyList())
                .build();
    }

    public static Report createTestReportWithQuery() {
        var query1 = ImmutableReportQuery.builder()
                .excelSheet(ImmutableExcelSheet.builder()
                        .name("Sheet1")
                        .build())
                .query(ReportQuerySql.of("SELECT * FROM ANY_REPORT.DATA"))
                .build();
        return createTestReport(UUID.randomUUID().toString(), List.of(query1) );
    }

    public static Report createTestReport(String secureDocumentId, Collection<ReportQuery> queries) {
        return ImmutableReport.builder()
                .id(UUID.randomUUID())
                .name("Test Report")
                .templateDocument(ExcelTemplate.fromString(secureDocumentId))
                .creationTime(Timestamp.from(Instant.now()))
                .lastDatabaseRefreshDate(Timestamp.from(Instant.now()))
                .description("Test Description")
                .numDaysToKeep(30)
                .outputType(ImmutableReportOutputType.builder()
                        .description("foo")
                        .id(UUID.randomUUID())
                        .fileExtension(FileExtension.CSV)
                        .build())
                .creator(ImmutableReportCreator.newBuilder()
                        .withId(UUID.randomUUID())
                        .withName("Owner Name")
                        .withEmail("owner@example.com")
                        .create())
                .owner(ImmutableReportOwner.newBuilder()
                        .withName("Creator Name")
                        .withEmail("creator@example.com")
                        .create())
                .outputFileName("test_report")
                .active(true)
                .queries(queries)
                .build();
    }

}
