package uk.gov.laa.gpfd.utils;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.Select;
import org.springframework.stereotype.Component;

@Component
public class SqlFormatValidator {
    
    /**
     * Checks whether the sql we have pulled from the database matches the format we expect
     *
     * @param rawSql - sql to validate
     * @return true if matches expected format, false otherwise
     */
    public boolean isSqlFormatValid(String rawSql) {
        if (rawSql == null || rawSql.isEmpty()) {
            return false;
        }

        try {
            var parsedSqlStatements = CCJSqlParserUtil.parseStatements(rawSql);

            if (parsedSqlStatements.size() != 1) {
                // Disallow "chaining", e.g. attaching a DROP statement after the SELECT
                return false;
            }

            return parsedSqlStatements.get(0) instanceof Select;

        } catch (JSQLParserException e) {
            return false;
        }
    }
}
