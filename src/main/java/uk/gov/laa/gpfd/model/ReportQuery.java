package uk.gov.laa.gpfd.model;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.Select;
import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;
import uk.gov.laa.gpfd.exception.SqlFormatException;

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

    @Value.Check
    protected void check() {
        String query = getQuery();

        if (!isSqlFormatValid(query)) {
            throw new SqlFormatException("SQL format invalid for sheet %s (report id %s)".formatted(getTabName(), getReportId()));
        }
    }

    private boolean isSqlFormatValid(String rawSql) {
        if (rawSql == null || rawSql.isEmpty()) {
            return false;
        }

        try {
            var parsedSqlStatements = CCJSqlParserUtil.parseStatements(rawSql);

            if (parsedSqlStatements.size() != 1) {
                // Disallow "chaining", e.g. attaching a DROP statement after the SELECT
                return false;
            }

            return parsedSqlStatements.get(0) instanceof Select;

        } catch (JSQLParserException e) {
            return false;
        }
    }
}
