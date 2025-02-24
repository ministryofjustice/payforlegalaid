package uk.gov.laa.gpfd.model;

import java.sql.Timestamp;
import java.util.UUID;

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
    private String reportGeneratedBy;
    private String reportUrl;
}
