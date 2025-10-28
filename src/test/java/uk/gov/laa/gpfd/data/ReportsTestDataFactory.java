package uk.gov.laa.gpfd.data;

import uk.gov.laa.gpfd.model.FileExtension;
import uk.gov.laa.gpfd.model.ImmutableReport;
import uk.gov.laa.gpfd.model.ImmutableReportCreator;
import uk.gov.laa.gpfd.model.ImmutableReportOutputType;
import uk.gov.laa.gpfd.model.ImmutableReportOwner;
import uk.gov.laa.gpfd.model.ImmutableReportQuery;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.model.ReportQuery;
import uk.gov.laa.gpfd.model.ReportQuerySql;
import uk.gov.laa.gpfd.model.excel.*;

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

    public static Report createTestReportForTacticalSol(UUID reportId) {
        return ImmutableReport.builder()
                .id(reportId)
                .name("Test Report")
                .templateDocument(ExcelTemplate.fromString("00000000-0000-0000-0000-000000000000"))
                .creationTime(Timestamp.from(Instant.now()))
                .lastDatabaseRefreshDate(Timestamp.from(Instant.now()))
                .description("REP0000 - Test Description")
                .numDaysToKeep(30)
                .outputType(ImmutableReportOutputType.builder()
                        .description("foo")
                        .id(reportId)
                        .fileExtension(FileExtension.S3STORAGE)
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
                        .fieldAttributes(List.of(
                                ImmutableExcelMappingProjection.builder()
                                        .excelColumn(ImmutableExcelColumn.builder()
                                                .name("Field 1")
                                                .format(ImmutableColumnFormat.builder()
                                                        .columnWidth(10)
                                                        .formatType("Foo")
                                                        .formatType(".00")
                                                        .build())
                                                .build())
                                        .build()
                        ))
                        .build())
                .query(ReportQuerySql.of("SELECT * FROM ANY_REPORT.DATA"))
                .build();
        return createTestReport(UUID.randomUUID().toString(), List.of(query1) );
    }

    public static Report createTestReportWithMultipleFieldAttributes() {
        var query1 = ImmutableReportQuery.builder()
                .excelSheet(ImmutableExcelSheet.builder()
                        .name("Sheet1")
                        .index(0)
                        .fieldAttributes(List.of(
                                ImmutableExcelMappingProjection.builder()
                                        .excelColumn(ImmutableExcelColumn.builder()
                                                .name("Field 1")
                                                .format(ImmutableColumnFormat.builder()
                                                        .columnWidth(10)
                                                        .formatType("Foo")
                                                        .formatType(".00")
                                                        .build())
                                                .build())
                                        .build(),
                                ImmutableExcelMappingProjection.builder()
                                        .excelColumn(ImmutableExcelColumn.builder()
                                                .name("Field 2")
                                                .format(ImmutableColumnFormat.builder()
                                                        .columnWidth(10)
                                                        .formatType("Foo")
                                                        .formatType(".00")
                                                        .build())
                                                .build())
                                        .build()
                        ))
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
