package uk.gov.laa.gpfd.model;

import lombok.Builder;

import java.sql.Timestamp;
import java.util.UUID;
//TODO - This class will be a bean, dont forget to annotate it with  @org.springframework.beans.factory.annotation.Autowired to make sonarlint happy
// Bean guide: https://www.baeldung.com/spring-bean

/**
 * A class representing the data in the MOJFIN 'Report Tracking' Table.
 */
@Builder
public record ReportTrackingTable(
        UUID id,
        String reportName,
        String reportUrl,
        Timestamp creationTime,
        UUID mappingId,
        String reportGeneratedBy
) {}