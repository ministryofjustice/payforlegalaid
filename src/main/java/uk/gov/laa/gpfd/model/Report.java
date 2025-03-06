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
public class Report {
    private UUID id;
    private String name;
    private String templateSecureDocumentId;
    Timestamp reportCreationTime;
    Timestamp lastDatabaseRefreshDate;
    private int numDaysToKeep;
    private UUID reportOutputType;
    private UUID reportCreatorId;
    private String reportCreatorName;
    private String reportCreatorEmail;
    private UUID reportOwnerId;
    private String reportOwnerName;
    private String reportOwnerEmail;
    private String fileName;
    private Boolean active;
    private String extension;
}
