package uk.gov.laa.gpfd.model;

import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.model.excel.ExcelSheet;
import uk.gov.laa.gpfd.model.excel.ExcelMappingProjection;

/**
 * Defines a complete mapping between a data query, its Excel presentation structure,
 * and associated field attributes for report generation.
 * <p>
 * Implementations must ensure consistent mapping between SQL result sets and Excel output,
 * following the formatting rules defined by {@link ReportQuerySql} and {@link ExcelSheet}.
 *
 * @see ReportQuerySql For SQL query formatting requirements
 * @see ExcelMappingProjection For field-level mapping implementations
 * @see ExcelSheet For Excel presentation specifications
 */
public interface Mapping {
    /**
     * Gets the parameterized SQL query for data retrieval.
     *
     * @return the validated SQL query, never {@code null}
     * @throws DatabaseReadException.SqlFormatException if query validation fails
     */
    ReportQuerySql getQuery();

    /**
     * Gets the Excel worksheet configuration for report output.
     * <p>
     * The sheet definition must:
     * <ul>
     *   <li>Contain field mappings corresponding to the SQL query columns</li>
     *   <li>Define all required formatting rules and headers</li>
     *   <li>Specify the target worksheet name and position</li>
     * </ul>
     *
     * @return the Excel sheet configuration, never {@code null}
     */
    ExcelSheet getExcelSheet();
}