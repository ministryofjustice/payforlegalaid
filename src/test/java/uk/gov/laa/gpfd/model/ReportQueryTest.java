package uk.gov.laa.gpfd.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.DatabaseReadException.SqlFormatException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportQueryTest {

    private static final UUID reportId = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");
    private static final UUID queryId = UUID.fromString("0d4da9ec-b0b3-4371-af10-2d74360dccd1");

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT * FROM ANY_REPORT.V_FISH_AND_CHIP_SHOPS",
            "SELECT * FROM ANY_REPORT.V_SOFT_DRINK_SALES    ", //Trailing white space
            "SELECT * FROM ANY_REPORT.V_LAST_14_DAYS_OF_TICKETS", //Numbers allowed
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
            "DROP TABLE ANY_REPORT.V_CAT_COLOURS", // Patently wrong SQL for selecting
            "SELECT * FROM GPFD.V_WHITEBOARD_PEN_TRACKER", // Wrong schema
            "SELECT * FROM ANY_REPORT.V_WHITEBOARD_PEN_TRACKER; DROP TABLE ANY_REPORT.V_CAT_COLOURS", // Command chaining
            "SELECT * FROM ANY_REPORT.    V_CHOCOLATE_BAR_SALES", // Weird spacing is fishy
            "SELECT * FROM ANY_REPORT.V_CAT_@COLOURS", // Flag special characters
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
