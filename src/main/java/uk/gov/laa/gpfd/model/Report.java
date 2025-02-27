package uk.gov.laa.gpfd.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * A class representing the data in the GPFD REPORTS Table.
 */
public class Report {
    private UUID reportId;
    private String name;
    private String templateSecureDocumentId;
    Timestamp reportCreationTime;
    Timestamp lastDatabaseRefreshDate;
    private String description;
    private int numDaysToKeep;
    private ReportOutputType reportOutputType;
    private String reportCreatorName;
    private String reportCreatorEmail;
    private UUID reportOwnerId;
    private String reportOwnerName;
    private String reportOwnerEmail;
    private String fileName;
    private Boolean active;
    private Collection<ReportQuery> queries;
}