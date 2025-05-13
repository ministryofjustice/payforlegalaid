package uk.gov.laa.gpfd.utils;

import org.springframework.stereotype.Component;

@Component
public class SqlFormatValidator {

    private static final String VALID_REGEX = "^SELECT \\* FROM ANY_REPORT\\.[A-Z0-9_]+$";

    /**
     * Checks whether the sql we have pulled from the database matches the format we expect
     *
     * @param rawSql - sql to validate
     * @return true if matches expected format, false otherwise
     */
    public boolean isSqlFormatValid(String rawSql) {
        return rawSql != null && rawSql.strip().matches(VALID_REGEX);
    }
}
