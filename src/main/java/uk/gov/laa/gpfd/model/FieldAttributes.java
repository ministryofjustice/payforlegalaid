package uk.gov.laa.gpfd.model;

import javax.annotation.Nullable;
import java.util.UUID;

import static org.immutables.value.Value.Immutable;

@Immutable
public abstract class FieldAttributes {
    @Nullable
    public abstract UUID getId();

    public abstract UUID getReportQueryId();

    public abstract String getSourceName();

    public abstract String getMappedName();

    @Nullable
    public abstract String getFormat();

    @Nullable
    public abstract String getFormatType();

    public abstract double getColumnWidth();
}
