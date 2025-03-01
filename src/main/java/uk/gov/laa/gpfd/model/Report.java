package uk.gov.laa.gpfd.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * A class representing the data in the GPFD REPORTS Table.
 */
public class Report {
    private UUID id;
    private String name;
    private String templateSecureDocumentId;
    Timestamp reportCreationDate;
    Timestamp lastDatabaseRefreshDate;
    private int numDaysToKeep;
    private UUID reportOutputType;
    private String description;
    private UUID reportOwnerId;
    private String reportOwnerName;
    private String reportOwnerEmail;
    private String fileName;
    private String active;
    private String extension;
}
