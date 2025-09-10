package uk.gov.laa.gpfd.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.laa.gpfd.exception.DatabaseReadException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportQuerySqlTest {

    @Test
    void shouldReturnTrueForEqualValues() {
        var sql1 = ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE1");
        var sql2 = ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE1");
        assertEquals(sql1, sql2);
    }

    @Test
    void shouldReturnFalseForDifferentValues() {
        var sql1 = ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE1");
        var sql2 = ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE2");
        assertNotEquals(sql1, sql2);
    }

    @Test
    void shouldReturnFalseForNull() {
        var sql = ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE1");
        assertNotEquals(null, sql);
    }

    @Test
    void shouldReturnTrueForBothNoneInstances() {
        var none1 = ReportQuerySql.ofNullable(null);
        var none2 = ReportQuerySql.ofNullable(null);
        assertEquals(none1, none2);
    }

    @Test
    void shouldReturnFalseWhenComparingNoneWithValue() {
        var none = ReportQuerySql.ofNullable(null);
        var withValue = ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE1");
        assertNotEquals(none, withValue);
        assertNotEquals(withValue, none);
    }

    @Test
    void shouldBeCaseSensitive() {
        var lowerCase = ReportQuerySql.of("select * from any_report.table1");
        var upperCase = ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE1");
        assertNotEquals(lowerCase, upperCase);
    }

    @Test
    void shouldIgnoreWhitespaceDifferences() {
        var sql1 = ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE1");
        var sql2 = ReportQuerySql.of("  SELECT * FROM ANY_REPORT.TABLE1  ");
        assertEquals(sql1, sql2);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT * FROM ANY_REPORT.V_FISH_AND_CHIP_SHOPS",
            "SELECT * FROM ANY_REPORT.V_SOFT_DRINK_SALES    ", // Trailing white space
            "SELECT * FROM ANY_REPORT.V_LAST_14_DAYS_OF_TICKETS", // Numbers allowed
            "SELECT * FROM ANY_REPORT.V",
            "SELECT * FROM ANY_REPORT.TABLENAME",
            "SELECT * FROM ANY_REPORT.V_CLIENTS WHERE id = ?", // New: Simple WHERE
            "SELECT * FROM ANY_REPORT.V_ORDERS WHERE customer_id = ?", // New: With underscore
            "SELECT * FROM ANY_REPORT.V_PRODUCTS WHERE id = ? AND status = ?", // New: Multiple params
    })
    void shouldReturnCreatedObjectWhenSqlIsValid(String sqlToTest) {
        assertDoesNotThrow(() -> ReportQuerySql.of(sqlToTest));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "this isn't even sql",
            "DROP TABLE ANY_REPORT.V_CAT_COLOURS",
            "SELECT * FROM GPFD.V_WHITEBOARD_PEN_TRACKER", // Wrong schema
            "SELECT * FROM ANY_REPORT.V_WHITEBOARD_PEN_TRACKER; DROP TABLE ANY_REPORT.V_CAT_COLOURS",
            "SELECT * FROM ANY_REPORT.    V_CHOCOLATE_BAR_SALES",
            "SELECT * FROM ANY_REPORT.V_CAT_@COLOURS",
            "SELECT * FROM ANY_REPORT.",
            "SELECT * FROM ANY_REPORT.V_CLIENTS WHERE id = 123", // Raw value instead of ?
            "SELECT * FROM ANY_REPORT.V_ORDERS WHERE customer_id = ? OR 1=1", // SQL injection attempt
            "SELECT * FROM ANY_REPORT.V_PRODUCTS WHERE id = ?; DELETE FROM USERS", // Command chaining
            "SELECT * FROM ANY_REPORT.V_INVOICES WHERE date_created > '2023-01-01'", // Literal value
            "SELECT * FROM ANY_REPORT.V_USERS WHERE username = ? AND password = 'password'", // Mixed params/literals
            "SELECT * FROM ANY_REPORT.V_TABLES WHERE col LIKE '%?%'", // Invalid parameter usage
            "SELECT * FROM ANY_REPORT.V_DATA WHERE id IN (?)" // Unsupported syntax
    })
    void shouldThrowExceptionWhenSqlIsInvalid(String sqlToTest) {
        assertThrows(DatabaseReadException.SqlFormatException.class, () -> ReportQuerySql.of(sqlToTest));
    }


}