package uk.gov.laa.gpfd.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.SqlFormatException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportQueryTest {

    private static final UUID reportId = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");
    private static final UUID queryId = UUID.fromString("0d4da9ec-b0b3-4371-af10-2d74360dccd1");

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT * FROM ANY_REPORT.V_CCMS_STATEMENT_FOR_ACCOUNTS",
            "SELECT * FROM ANY_REPORT.V_CCMS_STATEMENT_FOR_ACCOUNTS    ", //Trailing white space
            "SELECT * FROM ANY_REPORT.V_CCMS_STATEMENT_FOR_ACCOUNTS_14_DAYS", //Numbers allowed
            "SELECT * FROM ANY_REPORT.V",
            "SELECT * FROM ANY_REPORT.TABLENAME",
    })
    void givenValidSql_shouldReturnCreatedObject(String sqlToTest) {

        ImmutableReportQuery createdObject = ImmutableReportQuery.builder()
                .reportId(reportId)
                .id(queryId)
                .tabName("Marshmallow_Types")
                .query(sqlToTest)
                .build();

        assertNotNull(createdObject);
        assertEquals(reportId, createdObject.getReportId());
        assertEquals(queryId, createdObject.getId());
        assertEquals("Marshmallow_Types", createdObject.getTabName());
        assertEquals(sqlToTest, createdObject.getQuery());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "this isn't even sql",
            "DROP TABLE ANY_REPORT.V_CCMS_STATEMENT_FOR_ACCOUNTS", // Patently wrong SQL for selecting
            "SELECT * FROM ANY_REPORT.V_CCMS_STATEMENT_FOR_ACCOUNTS; DROP TABLE ANY_REPORT.V_CCMS_STATEMENT_FOR_ACCOUNTS", // Command chaining
            "SELECT * FROM ANY_REPORT.", // Must have a table name
    })
    void givenInvalidSql_shouldThrowExceptionWhenBuildingObject(String sqlToTest) throws DatabaseReadException {
        var builder = ImmutableReportQuery.builder()
                .reportId(reportId)
                .id(queryId)
                .tabName("Marshmallow_Types")
                .query(sqlToTest);

        assertThrows(SqlFormatException.class, builder::build);
    }
}
