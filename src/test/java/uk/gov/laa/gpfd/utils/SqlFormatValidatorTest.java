package uk.gov.laa.gpfd.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.laa.gpfd.exception.DatabaseReadException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SqlFormatValidatorTest {

    private final SqlFormatValidator sqlFormatValidator = new SqlFormatValidator();

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT * FROM ANY_REPORT.V_CCMS_STATEMENT_FOR_ACCOUNTS",
            "SELECT * FROM ANY_REPORT.V_CCMS_STATEMENT_FOR_ACCOUNTS    ", //Trailing white space
            "SELECT * FROM ANY_REPORT.V_CCMS_STATEMENT_FOR_ACCOUNTS_14_DAYS", //Numbers allowed
            "SELECT * FROM ANY_REPORT.V",
            "SELECT * FROM ANY_REPORT.TABLENAME",
    })
    void whenFormatIsValid_shouldReturnTrue(String sqlToTest) throws DatabaseReadException {
        assertTrue(sqlFormatValidator.isSqlFormatValid(sqlToTest));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "DROP TABLE ANY_REPORT.V_CCMS_STATEMENT_FOR_ACCOUNTS", // Patently wrong SQL for selecting
            "SELECT * FROM GPFD.V_CCMS_STATEMENT_FOR_ACCOUNTS", // Wrong schema
            "SELECT * FROM ANY_REPORT.V_CCMS_STATEMENT_FOR_ACCOUNTS; DROP TABLE ANY_REPORT.V_CCMS_STATEMENT_FOR_ACCOUNTS", // Command chaining
            "SELECT * FROM ANY_REPORT.    V_CCMS_STATEMENT_FOR_ACCOUNTS", // Weird spacing is fishy
            "SELECT * FROM ANY_REPORT.V_CCMS_@NAME", // Flag special characters
            "SELECT * FROM ANY_REPORT.", // Must have a table name
    })
    void whenFormatIsInvalid_shouldReturnFalse(String sqlToTest) throws DatabaseReadException {
        assertFalse(sqlFormatValidator.isSqlFormatValid(sqlToTest));
    }

}
