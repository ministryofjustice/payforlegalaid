package uk.gov.laa.gpfd.model;

import jakarta.validation.constraints.NotBlank;
import org.immutables.value.Value;

import jakarta.annotation.Nullable;
import java.sql.Timestamp;
import java.util.UUID;

@Value.Immutable
public abstract class Report implements Queryable<ReportQuery, Report>, Identifiable {
    @NotBlank
    public abstract String getName();
    @NotBlank
    public abstract String getTemplateSecureDocumentId();
    @Nullable
    public abstract Timestamp getReportCreationTime();
    @Nullable
    public abstract Timestamp getLastDatabaseRefreshDate();
    @Nullable
    public abstract String getDescription();

    public abstract int getNumDaysToKeep();
    @Nullable
    public abstract ReportOutputType getReportOutputType();
    @Nullable
    public abstract String getReportCreatorName();
    @Nullable
    public abstract String getReportCreatorEmail();
    @Nullable
    public abstract UUID getReportOwnerId();
    @Nullable
    public abstract String getReportOwnerName();
    @Nullable
    public abstract String getReportOwnerEmail();
    @Nullable
    public abstract String getFileName();
    @Nullable
    public abstract Boolean getActive();
}

