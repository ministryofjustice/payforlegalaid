package uk.gov.laa.gpfd.model;

//TODO - This class will be a bean, dont forget to annotate it with  @org.springframework.beans.factory.annotation.Autowired to make sonarlint happy
// Bean guide: https://www.baeldung.com/spring-bean

/**
 * A class representing the data in the MOJFIN 'CSV - SQL Mapping' Table. A subset of this data will eventually be
 * returned to the user via the /reports endpoint, in the form of a ReportListResponse
 */
public record MappingTable(
        int id,
        String reportName,
        String excelReport,
        String csvName,
        int excelSheetNum,
        String sqlQuery,
        String baseUrl,
        String reportOwner,
        String reportCreator,
        String description,
        String ownerEmail
) {}
