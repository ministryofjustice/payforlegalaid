package uk.gov.laa.gpfd.model;

import jakarta.annotation.Nullable;

import java.sql.Timestamp;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.immutables.value.Value.Derived;
import static org.immutables.value.Value.Immutable;

@Immutable
public abstract class ReportsTracking implements Identifiable {

    public abstract Report getReport();

    @Derived
    public UUID getId() {
        return randomUUID();
    }

    @Nullable
    public abstract Timestamp getCreationDate();

    @Nullable
    public abstract String getReportCreator();

    @Nullable
    public abstract String getReportUrl();

    @Derived
    public String getReportOwner() {
        return getReport().getOwner().getName();
    }

    @Derived
    public String getReportOutputType() {
        return getReport().getOutputType().getExtension();
    }

    @Derived
    public String getTemplateUrl() {
        return getReport().getTemplateDocument().getIdAsString();
    }

    @Derived
    public String getReportName() {
        return getReport().getName();
    }

    @Derived
    public UUID getReportId() {
        return getReport().getId();
    }

    public static ImmutableReportsTracking.Builder builderFor(Report report) {
        return ImmutableReportsTracking.builder().report(report);
    }
}
