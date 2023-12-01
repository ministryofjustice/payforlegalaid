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
    private String sqlString;
    private String baseUrl;
    private String reportPeriod;
    private String reportOwner;
    private String reportCreator;
    private String description;
    private int excelSheetNum;
    private String csvName;


    public MappingTableModel(int id, String reportName, String sqlString, String baseUrl, String reportPeriod, String reportOwner, String reportCreator, String description, int excelSheetNum, String csvName) {
        this.id = id;
        this.reportName = reportName;
        this.sqlString = sqlString;
        this.baseUrl = baseUrl;
        this.reportPeriod = reportPeriod;
        this.reportOwner = reportOwner;
        this.reportCreator = reportCreator;
        this.description = description;
        this.excelSheetNum = excelSheetNum;
        this.csvName = csvName;
    }


}
