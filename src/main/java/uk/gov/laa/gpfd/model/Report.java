package uk.gov.laa.gpfd.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import jakarta.annotation.Nullable;
import java.sql.Timestamp;

import static org.immutables.value.Value.Immutable;

@Immutable
public abstract class Report implements Queryable<ReportQuery, Report>, Identifiable {
    @NotBlank
    public abstract String getName();

    @Nullable
    public abstract String getDescription();

    @NotNull
    public abstract ReportContributor.ReportOwner getOwner();

    @Nullable
    public abstract ReportContributor.ReportCreator getCreator();

    @NotNull
    public abstract ReportOutputType getOutputType();

    @Nullable
    public abstract String getOutputFileName();

    @NotBlank
    public abstract TemplateDocument getTemplateDocument();

    @Nullable
    public abstract Timestamp getCreationTime();

    @Nullable
    public abstract Timestamp getLastDatabaseRefreshDate();

    public abstract int getNumDaysToKeep();

    @Nullable
    public abstract Boolean getActive();
}

