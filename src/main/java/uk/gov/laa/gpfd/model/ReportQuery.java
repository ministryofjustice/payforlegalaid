package uk.gov.laa.gpfd.model;

import jakarta.annotation.Nullable;
import org.immutables.value.Value.Immutable;

import java.util.Collection;
import java.util.UUID;

@Immutable
public abstract class ReportQuery implements Mapping, Identifiable {
    @Nullable
    public abstract UUID getReportId();

    @Nullable
    public abstract ReportQuerySql getQuery();

    @Nullable
    public abstract String getSheetName();

    @Nullable
    public abstract Collection<FieldAttributes> getFieldAttributes();
}
