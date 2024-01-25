package uk.gov.laa.pfla.auth.service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

//TODO - This class will be a bean, dont forget to annotate it with  @org.springframework.beans.factory.annotation.Autowired to make sonarlint happy
// Bean guide: https://www.baeldung.com/spring-bean

/**
 * A class representing the data in the MOJFIN 'CSV - SQL Mapping' Table. A subset of this data will eventually be
 * returned to the user via the /reports endpoint, in the form of a ReportListResponse
 */
@Data
@Builder
@AllArgsConstructor
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


}
