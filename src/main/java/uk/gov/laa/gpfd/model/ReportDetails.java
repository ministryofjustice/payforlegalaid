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
 * A class containing the data in the GPFD REPORTS Table plus derived attributes.
 */
public class ReportDetails {
    private UUID id;
    private String name;
    private String templateSecureDocumentId;
    Timestamp reportCreationDate;
    Timestamp lastDatabaseRefreshDate;
    private Integer numDaysToKeep;
    private UUID reportOutputType;
    private String description;
    private UUID reportOwnerId;
    private String reportOwnerName;
    private String reportOwnerEmail;
    private String fileName;
    private Boolean active;
    private String extension;
    private String reportDownloadUrl;
}