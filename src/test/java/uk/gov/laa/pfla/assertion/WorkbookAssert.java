package uk.gov.laa.pfla.assertion;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.Assertions;
import uk.gov.laa.pfla.comparator.WorkbookComparator;
import uk.gov.laa.pfla.util.workbook.SheetCellIterator;
import uk.gov.laa.pfla.util.workbook.pivot.PivotTableValidator;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.apache.poi.ss.usermodel.CellType.ERROR;
import static org.apache.poi.ss.usermodel.CellType.FORMULA;
import static org.apache.poi.xssf.usermodel.XSSFRelation.PIVOT_CACHE_DEFINITION;
import static org.apache.poi.xssf.usermodel.XSSFRelation.PIVOT_TABLE;

public abstract class WorkbookAssert extends AbstractAssert<WorkbookAssert, Workbook> {

    private final WorkbookComparator workbookComparator;

    public WorkbookAssert(Workbook actual) {
        super(actual, WorkbookAssert.class);
        this.workbookComparator = new WorkbookComparator() {};
    }

    public static WorkbookAssert assertThat(Workbook actual) {
        return new WorkbookAssert(actual) {};
    }

    public void isEqualTo(Workbook expected) {
        isNotNull();

        if (!workbookComparator.areWorkbooksEqual(actual, expected)) {
            failWithMessage("Workbook does not match the expected result");
        }
    }

    public void hasAllFormulasEvaluated() {
        SheetCellIterator.ofWorkbook(actual).stream()
                .filter((Cell cell) -> FORMULA == cell.getCellType())
                .filter((Cell cell) -> ERROR == cell.getCachedFormulaResultType())
                .forEach((Cell cell) -> {
                    var sheet = ((XSSFCell)cell).getSheet();
                    failWithMessage("Formula error at %s.%s - Formula: '%s' resulted in error",
                            sheet.getSheetName(),
                            cell.getAddress().formatAsString(),
                            cell.getCellFormula());
                });
    }

    public void hasValidPivotTables() {
        var xssfWorkbook = (XSSFWorkbook) actual;
        var validator = new PivotTableValidator();

        // Workbook has relationship with the pivot cache
        // validate these relationships as if invalid will corrupt the pivots.
        xssfWorkbook.getRelationParts().stream()
                .filter(part -> part.getRelationship().getRelationshipType().equals(PIVOT_CACHE_DEFINITION.getRelation()))
                .map(part -> (XSSFPivotCacheDefinition) xssfWorkbook.getRelationById(part.getRelationship().getId()))
                .forEach(Assertions::assertNotNull);

        // For each sheet, find any pivot tables it has on it and validate the contents
        StreamSupport.stream(xssfWorkbook.spliterator(), false)
            .map(sheet -> (XSSFSheet) sheet)
            .flatMap(sheet -> (sheet.getRelationParts().stream())
            .filter(part -> part.getRelationship().getRelationshipType().equals(PIVOT_TABLE.getRelation()))
            .map(part -> (XSSFPivotTable) sheet.getRelationById(part.getRelationship().getId())))
            .forEach(validator::validate);

    }

    public void validateRequiredCells(Sheet sheet, List<CellCheck> cellChecks) {
        isNotNull();

        for (CellCheck cellCheck : cellChecks) {
            Cell cell = WorkbookHelper.getCellByAddress(sheet, cellCheck.address());
            String where = String.format("%s %s", sheet.getSheetName(), cellCheck.address());
            if (!cellCheck.namedValidator().predicate().test(cell, actual)) {
                String rendered = WorkbookHelper.safeRender(cell);
                throw new AssertionError(
                        "Validation failed at " + where +
                                " value: " + rendered + " namedValidator: " + cellCheck.namedValidator().name()
                );
            }
        }
    }
}
