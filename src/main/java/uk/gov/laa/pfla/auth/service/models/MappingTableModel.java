package uk.gov.laa.pfla.auth.service.models;

import lombok.Data;

//TODO - This class will be a bean, dont forget to annotate it with  @org.springframework.beans.factory.annotation.Autowired to make sonarlint happy
// Bean guide: https://www.baeldung.com/spring-bean

/**
 * A class representing the data in the MOJFIN 'CSV - SQL Mapping' Table. A subset of this data will eventually be
 * returned to the user via the /reports endpoint, in the form of a ReportListResponse
 */
@Data
public class MappingTableModel {
    public MappingTableModel() {
        //no args constructor needed for ModelMapper
    }

    private int id;
    private String reportName;
    private String excelReport;
    private String csvName;
    private int excelSheetNum;
    private String sqlQuery;
    private String baseUrl;
    private String reportOwner;
    private String reportCreator;
    private String description;
    private String ownerEmail;


    public MappingTableModel(int id, String reportName, String excelReport, String csvName, int excelSheetNum, String sqlQuery, String baseUrl, String reportOwner, String reportCreator, String description, String ownerEmail) {
        this.id = id;
        this.reportName = reportName;
        this.excelReport = excelReport;
        this.csvName = csvName;
        this.excelSheetNum = excelSheetNum;
        this.sqlQuery = sqlQuery;
        this.baseUrl = baseUrl;
        this.reportOwner = reportOwner;
        this.reportCreator = reportCreator;
        this.description = description;
        this.ownerEmail = ownerEmail;
    }
}
