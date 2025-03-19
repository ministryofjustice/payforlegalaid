package uk.gov.laa.gpfd.model;

import org.immutables.value.Value.Immutable;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

@Immutable
public abstract class ReportQuery {
    @Nullable
    public abstract UUID getId();
    @Nullable
    public abstract UUID getReportId();
    @Nullable
    public abstract String getQuery();
    @Nullable
    public abstract String getTabName();
    @Nullable
    public abstract Collection<FieldAttributes> getFieldAttributes();
}
