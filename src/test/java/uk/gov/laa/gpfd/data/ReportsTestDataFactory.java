package uk.gov.laa.gpfd.data;

import uk.gov.laa.gpfd.model.Report;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class ReportsTestDataFactory {
    public static Report aCCMSInvoiceAnalysisExcelReport (){
        return new Report(
        UUID.fromString("b36f9bbb-1178-432c-8f99-8090e285f2d3"),
        "CCMS Invoice Analysis (CIS to CCMS)",
        "00000000-0000-0000-0000-000000000000",
        new Timestamp(System.currentTimeMillis()),
        null,
        30,
        UUID.fromString("bd098666-94e4-4b0e-822c-8e5dfb04c908"),
        null,
        "Summary of invoices in CIS and CCMS by original source IT system",
        null,
        null,
        UUID.fromString("00000000-0000-0000-0000-000000000001"),
        "Chancey Mctavish",
        "owneremail@email.com",
        "CCMS_invoice_analysis",
        "Y",
        "xlsx"
        );
    }

    public static Report aCCMSInvoiceAnalysisCSVReport () {
        return new Report(
                UUID.fromString("f46b4d3d-c100-429a-bf9a-6c3305dbdbf5"),
                "CIS to CCMS payment value Not Defined",
                "00000000-0000-0000-0000-000000000000",
                new Timestamp(System.currentTimeMillis()),
                null,
                60,
                UUID.fromString("6ebd27ac-4d83-485d-a4fd-3e45f9a53484"),
                null,
                "Details of invoices transferred from CIS to CCMS by Legal Aid Scheme",
                null,
                null,
                UUID.fromString("00000000-0000-0000-0000-000000000003"),
                "William Moore",
                "William.Moore@Justicedept.gov.uk",
                "CIS_TO_CCMS_PAYMENT_VALUE_NOT_DEFINED",
                "Y",
                "csv"
        );

    }
    public static Report invalidReportData () {
        return new Report(
                null,
                null,
                null,
                null,
                null,
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "Y",
                null
        );

    }

}
