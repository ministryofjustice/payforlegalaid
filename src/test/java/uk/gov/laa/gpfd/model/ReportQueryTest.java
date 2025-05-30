package uk.gov.laa.gpfd.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.laa.gpfd.exception.DatabaseReadException.SqlFormatException;

class ReportQueryTest {

    private static final UUID reportId = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");
    private static final UUID queryId = UUID.fromString("0d4da9ec-b0b3-4371-af10-2d74360dccd1");


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
    void givenValidSql_shouldReturnCreatedObject(String sqlToTest) {

        ImmutableReportQuery createdObject = ImmutableReportQuery.builder()
                .reportId(reportId)
                .id(queryId)
                .sheetName("Marshmallow_Types")
                .query(ReportQuerySql.of(sqlToTest))
                .build();

        assertNotNull(createdObject);
        assertEquals(reportId, createdObject.getReportId());
        assertEquals(queryId, createdObject.getId());
        assertEquals("Marshmallow_Types", createdObject.getSheetName());
        assertEquals(ReportQuerySql.of(sqlToTest), createdObject.getQuery());
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
    void givenInvalidSql_shouldThrowExceptionWhenBuildingObject(String sqlToTest) {
        var builder = ImmutableReportQuery.builder()
                .reportId(reportId)
                .id(queryId)
                .sheetName("Marshmallow_Types");

        assertThrows(SqlFormatException.class, () -> builder.query(ReportQuerySql.of(sqlToTest)).build());
    }
}
