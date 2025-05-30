package uk.gov.laa.gpfd.model;

import jakarta.annotation.Nullable;
import org.immutables.value.Value;

import java.sql.Timestamp;
import java.util.UUID;

@Value.Immutable
public abstract class ReportsTracking implements Identifiable {

    @Nullable
    public abstract String getName();

    @Nullable
    public abstract UUID getReportId();

    @Nullable
    public abstract Timestamp getCreationDate();

    @Nullable
    public abstract String getReportCreator();

    @Nullable
    public abstract String getReportOwner();

    @Nullable
    public abstract String getReportOutputType();

    @Nullable
    public abstract String getTemplateUrl();

    @Nullable
    public abstract String getReportUrl();

}
