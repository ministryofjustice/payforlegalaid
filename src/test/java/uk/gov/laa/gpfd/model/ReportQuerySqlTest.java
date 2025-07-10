package uk.gov.laa.gpfd.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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

}