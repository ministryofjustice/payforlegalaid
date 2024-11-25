package uk.gov.laa.gpfd.model;

import lombok.Builder;

import java.sql.Timestamp;
//TODO - This class will be a bean, dont forget to annotate it with  @org.springframework.beans.factory.annotation.Autowired to make sonarlint happy
// Bean guide: https://www.baeldung.com/spring-bean

/**
 * A class representing the data in the MOJFIN 'Report Tracking' Table.
 */
@Builder
public record ReportTrackingTable(
        int id,
        String reportName,
        String reportUrl,
        Timestamp creationTime,
        int mappingId,
        String reportGeneratedBy
) {}