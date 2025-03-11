package uk.gov.laa.gpfd.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "REPORTS_TRACKING", schema = "GPFD")
public class ReportsTracking {

    @Id
    @Column(name = "ID", nullable = false)
    private UUID id;

    @Column(name = "NAME", nullable = false)
    private String reportName;

    @Column(name = "REPORT_ID", nullable = false)
    private UUID mappingId;

    @Column(name = "CREATION_DATE")
    private Timestamp creationTime;

    @Column(name = "REPORT_DOWNLOADED_BY", nullable = false)
    private String reportDownloadedBy;

    @Column(name = "REPORT_GENERATED_BY", nullable = false)
    private String reportGeneratedBy;

    @Column(name = "REPORT_CREATOR", nullable = false)
    private String reportCreator;

    @Column(name = "REPORT_OWNER", nullable = false)
    private String reportOwner;

    @Column(name = "REPORT_OUTPUT_TYPE", nullable = false)
      private String reportOutputType;

    @Column(name = "TEMPLATE_URL", nullable = false)
    private String templateUrl;

    @Column(name = "REPORT_URL", nullable = false)
    private String reportUrl;

}
