package uk.gov.laa.gpfd.utils;

import org.springframework.stereotype.Component;

@Component
public class SqlFormatValidator {

    private final static String validRegex = "^SELECT \\* FROM ANY_REPORT\\.[A-Z0-9_]+$";

    public boolean isSqlFormatValid(String rawSql) {
        return rawSql != null && rawSql.strip().matches(validRegex);
    }
}
