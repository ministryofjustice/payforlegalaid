package uk.gov.laa.gpfd.data;

import uk.gov.laa.gpfd.model.ImmutableReportOutputType;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.model.ReportOutputType;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class ReportsTestDataFactory {
    public static Report aCCMSInvoiceAnalysisExcelReport (){
        return Report.builder()
                .name("CCMS Invoice Analysis (CIS to CCMS)")
                .reportId(UUID.fromString("b36f9bbb-1178-432c-8f99-8090e285f2d3"))
                .templateSecureDocumentId( "00000000-0000-0000-0000-000000000000")
                .reportOwnerEmail("owneremail@email.com")
                .reportCreationTime(new Timestamp(System.currentTimeMillis()))
                .reportOwnerId(UUID.fromString("bd098666-94e4-4b0e-822c-8e5dfb04c908"))
                .numDaysToKeep(30)
                .active(true)
                .reportOwnerName("Chancey Mctavish")
                .reportOutputType(ImmutableReportOutputType.builder().extension("xls").description("Sample description").build())
                .build();
    }

    public static Report aCCMSInvoiceAnalysisCSVReport () {
        return Report.builder()
                .name("CCMS Invoice Analysis (CIS to CCMS)")
                .reportId(UUID.fromString("b36f9bbb-1178-432c-8f99-8090e285f2d3"))
                .templateSecureDocumentId( "00000000-0000-0000-0000-000000000000")
                .reportOwnerEmail("owneremail@email.com")
                .reportCreationTime(new Timestamp(System.currentTimeMillis()))
                .reportOwnerId(UUID.fromString("bd098666-94e4-4b0e-822c-8e5dfb04c908"))
                .numDaysToKeep(30)
                .active(true)
                .reportOwnerName("William Moore")
                .reportOwnerEmail("William.Moore@Justicedept.gov.uk")
                .templateSecureDocumentId("00000000-0000-0000-0000-000000000000")
                .reportOutputType(ImmutableReportOutputType.builder().extension("csv").description("Sample description").build())
                .build();

    }
    public static Report invalidReportData () {
        return Report.builder().build();
    }

}
