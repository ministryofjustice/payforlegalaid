package uk.gov.laa.pfla.auth.service.builders;

import uk.gov.laa.pfla.auth.service.models.MappingTableModel;

public class MappingTableModelTestBuilder {

    // Default Values to make building test data easier
    public static final int DEFAULT_ID = 1;
    public static final String DEFAULT_REPORT_NAME = "Excel_Report_Name-CSV-NAME-sheetnumber";
    public static final String DEFAULT_REPORT_PERIOD = "01/08/2023 - 01/09/2023";
    public static final String DEFAULT_REPORT_OWNER = "Chancey Mctavish";
    public static final String DEFAULT_REPORT_CREATOR = "Barry Gibb";
    public static final String DEFAULT_REPORT_DESCRIPTION= "List all unpaid AP invoices and all outstanding AR debts at the end of the previous month. Detailed data, one row per invoice";
    public static final String DEFAULT_BASE_URL = "www.sharepoint.com/the-folder-we're-using";
    public static final String DEFAULT_SQL_STRING = "SELECT * FROM SOMETHING";

    public static final String DEFAULT_CSV_NAME = "Excel_Report_Name-CSV-NAME-sheetnumber";

    public static final int DEFAULT_EXCEL_SHEET_NUM = 9;



    private int id = DEFAULT_ID;
    private String reportName = DEFAULT_REPORT_NAME;

    private String sqlString = DEFAULT_SQL_STRING;
    private String baseUrl = DEFAULT_BASE_URL;

    private String reportPeriod = DEFAULT_REPORT_PERIOD;
    private String reportOwner = DEFAULT_REPORT_OWNER;
    private String reportCreator = DEFAULT_REPORT_CREATOR;
    private String description = DEFAULT_REPORT_DESCRIPTION;

    private int excelSheetNum = DEFAULT_EXCEL_SHEET_NUM;
    private String csvName = DEFAULT_CSV_NAME;


    public MappingTableModelTestBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public MappingTableModelTestBuilder withReportName(String reportName) {
        this.reportName = reportName;
        return this;
    }


    public MappingTableModelTestBuilder withSql(String sql) {
        this.sqlString = sql;
        return this;
    }

    public MappingTableModelTestBuilder withBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public MappingTableModelTestBuilder withReportPeriod(String reportPeriod) {
        this.reportPeriod = reportPeriod;
        return this;
    }

    public MappingTableModelTestBuilder withReportOwner(String reportOwner) {
        this.reportOwner = reportOwner;
        return this;
    }

    public MappingTableModelTestBuilder withReportCreator(String reportCreator) {
        this.reportCreator = reportCreator;
        return this;
    }

    public MappingTableModelTestBuilder withReportDescription(String description) {
        this.description = description;
        return this;
    }

    public MappingTableModelTestBuilder withExcelSheetNum(int excelSheetNum) {
        this.excelSheetNum = excelSheetNum;
        return this;
    }
    public MappingTableModelTestBuilder withCsvName(String csvName) {
        this.csvName = csvName;
        return this;
    }


    public MappingTableModel createMappingTableModel() {
        return new MappingTableModel(id, reportName, sqlString, baseUrl, reportPeriod, reportOwner, reportCreator, description, excelSheetNum, csvName );
    }
}