package uk.gov.laa.gpfd.integration.verifier;

import org.springframework.jdbc.core.JdbcOperations;

import java.util.function.Function;

/**
 * A utility class for verifying database state in tests.
 * <p>
 * Provides type-safe methods to count rows in database tables and verify
 * database contents.
 * </p>
 */
public class DatabaseVerifier {
    /**
     * The default database schema name (GPFD) used for table qualification.
     */
    private static final String DEFAULT_SCHEMA = "GPFD";

    /**
     * Enumerates tables available for verification.
     */
    public enum Table {
        REPORTS("REPORTS"),
        REPORT_QUERIES("REPORT_QUERIES"),
        REPORT_GROUPS("REPORT_GROUPS");

        private final String name;

        Table(String name) {
            this.name = name;
        }

        /**
         * Gets the fully qualified table name (schema.table) format.
         *
         * @return the qualified table name in the format "GPFD.table_name"
         */
        public String getQualifiedName() {
            return DEFAULT_SCHEMA + "." + name;
        }
    }

    /**
     * Creates a function that counts rows in the specified table.
     * <p>
     * The returned function can be applied to a {@link JdbcOperations} instance
     * to execute the count query.
     * </p>
     */
    public static Function<JdbcOperations, Long> rowCountFor(Table table) {
        return jdbc -> jdbc.queryForObject(
                "SELECT COUNT(*) FROM " + table.getQualifiedName(),
                Long.class
        );
    }
}