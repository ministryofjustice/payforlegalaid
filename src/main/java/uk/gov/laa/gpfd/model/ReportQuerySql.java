package uk.gov.laa.gpfd.model;

import org.immutables.value.Value;

import java.util.regex.Pattern;

import java.util.Objects;

import static org.immutables.value.Value.Style.BuilderVisibility;
import static org.immutables.value.Value.Style.ImplementationVisibility;
import static uk.gov.laa.gpfd.exception.DatabaseReadException.SqlFormatException;

/**
 * A value object representing a validated SQL query for report generation.
 * Ensures the query follows the strict format: `SELECT * FROM ANY_REPORT.TABLE_NAME`.
 */
@Value.Immutable
@Value.Style(
        of = "of",
        visibility = ImplementationVisibility.PACKAGE,
        builderVisibility = BuilderVisibility.PACKAGE
)
public abstract class ReportQuerySql {

    private static final Pattern VALID_REGEX = Pattern.compile(
            "^SELECT\\s+\\*\\s+FROM\\s+ANY_REPORT\\.[A-Z0-9_]+" +
                    "(?:\\s+WHERE\\s+[A-Z0-9_]+\\s*=\\s*\\?(?:\\s+AND\\s+[A-Z0-9_]+\\s*=\\s*\\?)*)?$",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * The underlying SQL query string.
     * @return A non-null, validated SQL string.
     */
    @Value.Parameter
    public abstract String value();

    /**
     * Validates the SQL query format.
     * @throws IllegalArgumentException if the SQL is invalid.
     */
    @Value.Check
    protected void validate() {
        Objects.requireNonNull(value(), "SQL query must not be null");
        String stripped = value().strip();
        if (!VALID_REGEX.matcher(stripped).matches()) {
            throw new SqlFormatException(
                    "SQL must be in format: 'SELECT * FROM ANY_REPORT.TABLE_NAME' (got: '" + stripped + "')");
        }
    }

    /**
     * Factory method to create a validated ReportQuerySql.
     * @param sql The SQL query string.
     * @return A validated ReportQuerySql instance.
     * @throws IllegalArgumentException if validation fails.
     */
    public static ReportQuerySql of(String sql) {
        return ImmutableReportQuerySql.of(sql);
    }

    @Override
    public String toString() {
        return value();
    }

}
