package uk.gov.laa.gpfd.model;

import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;
import uk.gov.laa.gpfd.exception.DatabaseReadException.SqlFormatException;

import jakarta.annotation.Nullable;
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

    private static final String VALID_REGEX = "^SELECT \\* FROM ANY_REPORT\\.[A-Z0-9_]+$";

    @Value.Check
    protected void check() {
        var query = getQuery();

        if (!isSqlFormatValid(query)) {
            throw new SqlFormatException("SQL query invalid for sheet %s (report id %s)".formatted(getTabName(), getReportId()));
        }
    }

    private boolean isSqlFormatValid(String rawSql) {
        return rawSql != null && rawSql.strip().matches(VALID_REGEX);
    }
}
