package uk.gov.laa.gpfd.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportsTracking {

    private UUID id;

    private String name;

    private UUID reportId;

    private Timestamp creationDate;

    private String reportDownloadedBy;

    private String reportCreator;

    private String reportOwner;

    private String reportOutputType;

    private String templateUrl;

    private String reportUrl;

}
