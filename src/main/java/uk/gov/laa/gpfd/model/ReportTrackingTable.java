package uk.gov.laa.gpfd.model;

import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record ReportTrackingTable(
        int id,
        String reportName,
        String reportUrl,
        Timestamp creationTime,
        int mappingId,
        String reportGeneratedBy
) {}