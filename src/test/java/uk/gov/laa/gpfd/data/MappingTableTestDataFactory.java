package uk.gov.laa.gpfd.data;

import uk.gov.laa.gpfd.model.MappingTable;

public class MappingTableTestDataFactory {

    /**
     * Creates a valid MappingTable record for the first report entry.
     *
     * @return a MappingTable record with data for the first report entry.
     */
    public static MappingTable aValidInvoiceAnalysisReport() {
        return new MappingTable(
                1,
                "CCMS_invoice_analysis-CIS-to-CCMS-import-analysis-2",
                "CCMS_invoice_analysis",
                "CIS-to-CCMS-import-analysis",
                2,
                "SELECT * FROM ANY_REPORT.V_CIS_TO_CCMS_INVOICE_SUMMARY",
                "https://foo.ba.com/:x:/r/sites/Shared%20Documents/General/Monthly%20Accounts/Sharepoint%20base%20reports/General%20CCMS%20Tools/CCMS%20invoice%20analysis.xlsb?d=w7bc78b4b2c94489e899415353a37d234&csf=1&web=1&e=K0h3OD",
                "Chancey Mctavish",
                "Sophia Patel",
                "Summary of invoices in CIS and CCMS by original source IT system",
                "owneremail@email.com"
        );
    }

    /**
     * Creates a valid MappingTable record for the second report entry.
     *
     * @return a MappingTable record with data for the second report entry.
     */
    public static MappingTable aValidBankAccountReport() {
        return new MappingTable(
                2,
                "CCMS_and_CIS_Bank_Account_Report_w_Category_Code_(MNTH)-MAIN-12",
                "CCMS_and_CIS_Bank_Account_Report_w_Category_Code_(MNTH)",
                "MAIN",
                12,
                "SELECT * FROM ANY_REPORT.V_BANK_MONTH",
                "https://foo.bar.com/sites/Shared%20Documents/Forms/AllItems.aspx?id=%2Fsites%2FFinanceSysReference%2FShared%20Documents%2FGeneral%2FMonthly%20Accounts%2F2023%2D06%2FBank%20reporting&viewid=5d6ec327%2D2975%2D4d7c%2Dbd8d%2D6b793c45868b",
                "Daniel Mctavish",
                "Brian Limond",
                "Summary of all payments made by CCMS/CIS and all cash receipts applied to debt in the previous month",
                "secondowneremail@email.com"
        );
    }

}
