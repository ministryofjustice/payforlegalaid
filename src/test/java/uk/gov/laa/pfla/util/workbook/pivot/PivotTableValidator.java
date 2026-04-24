package uk.gov.laa.pfla.util.workbook.pivot;

import org.apache.poi.xssf.usermodel.XSSFPivotTable;

import static uk.gov.laa.pfla.util.workbook.pivot.PivotTableValidationStrategy.*;

/**
 * Validates an XSSF PivotTable against a set of predefined validation strategies.
 * <p>
 * This validator performs multiple checks on a pivot table to ensure its structural integrity,
 * including cache definition, data source, location, and field configuration validations.
 * The validations are performed in an unspecified order, and all validations are executed
 * regardless of previous failures (unless an exception is thrown).
 * </p>
 */
public class PivotTableValidator {

    private static final PivotTableValidationStrategy[] strategies = {
            new CacheDefinitionValidation(),
            new DataSourceValidation(),
            new LocationValidation(),
            new PivotFieldsValidation(),
            new PivotFieldStyleValidation(),
            new PivotFormatValidation(),
            new PivotCacheFieldValidation(),
            new PivotCacheFieldStyleValidation()
    };

    /**
     * Validates the specified pivot table against all configured validation strategies.
     *
     * @param pivotTable the pivot table to validate (must not be {@code null})
     */
    public void validate(XSSFPivotTable pivotTable) {
        for (var strategy : strategies)
            strategy.validate(pivotTable);
    }
}