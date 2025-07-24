package uk.gov.laa.gpfd.model;

import java.util.regex.Pattern;
import java.util.Objects;
import java.util.Optional;

import static uk.gov.laa.gpfd.exception.DatabaseReadException.SqlFormatException;

/**
 * A value object representing a validated SQL query for report generation.
 * Ensures the query follows the strict format: `SELECT * FROM ANY_REPORT.TABLE_NAME`.
 */
public final class ReportQuerySql {

    private static final Pattern VALID_REGEX = Pattern.compile(
            "^SELECT\\s+\\*\\s+FROM\\s+ANY_REPORT\\.[A-Z0-9_]+" +
                    "(?:\\s+WHERE\\s+[A-Z0-9_]+\\s*=\\s*\\?(?:\\s+AND\\s+[A-Z0-9_]+\\s*=\\s*\\?)*)?$",
            Pattern.CASE_INSENSITIVE
    );

    private static final ReportQuerySql NONE = new ReportQuerySql(null);

    private final String value;

    private ReportQuerySql(String value) {
        this.value = value;
    }

    /**
     * Factory method for creating validated instances from nullable input
     */
    public static ReportQuerySql ofNullable(String sql) {
        return Optional.ofNullable(sql)
                .map(ReportQuerySql::of)
                .orElse(NONE);
    }

    /**
     * Factory method for creating validated instances from non-null input
     * @throws IllegalArgumentException if validation fails
     */
    public static ReportQuerySql of(String sql) {
        return new ReportQuerySql(validate(sql));
    }

    /**
     * Checks if this instance represents an actual query
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * Gets the validated SQL string if present
     * @throws IllegalStateException if called on NONE instance
     */
    public String value() {
        if (value == null) {
            throw new IllegalStateException("No query present");
        }
        return value;
    }

    private static String validate(String sql) {
        Objects.requireNonNull(sql, "SQL query must not be null");
        var stripped = sql.strip();

        if (!VALID_REGEX.matcher(stripped).matches()) {
            throw new SqlFormatException(
                    "SQL must be in format: 'SELECT * FROM ANY_REPORT.TABLE_NAME' (got: '" + stripped + "')");
        }

        return stripped;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (ReportQuerySql) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return isPresent() ? value : "[NO_QUERY]";
    }
}