package uk.gov.laa.pfla.assertion;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import uk.gov.laa.pfla.enums.FieldTypeEnum;

import java.util.Locale;

public class FieldValidators {

    public static final NamedValidator NUMERIC = new NamedValidator(
            FieldTypeEnum.NUMERIC.name(),
        (cell, wb) -> cell != null && switch (cell.getCellType()) {
            case NUMERIC -> true;
            case FORMULA -> isFormulaNumeric(cell);
            default -> false;
        }
    );

    public static final NamedValidator CURRENCY_GBP = new NamedValidator(
            FieldTypeEnum.CURRENCY_GBP.name(),
            (cell, wb) -> {
                if (cell == null) return false;
                DataFormatter fmt = new DataFormatter(Locale.UK, true);
                FormulaEvaluator eval = wb.getCreationHelper().createFormulaEvaluator();

                String rendered = cell.getCellType() == CellType.FORMULA
                        ? fmt.formatCellValue(cell, eval)
                        : fmt.formatCellValue(cell);

                return rendered != null && rendered.trim().startsWith("£");
            }
    );

    public static final NamedValidator STRING_NONBLANK = new NamedValidator(
            FieldTypeEnum.STRING_NONBLANK.name(),
            (cell, wb) -> {
                if (cell == null) return false;
                return switch (cell.getCellType()) {
                    case STRING -> {
                        String s = cell.getStringCellValue();
                        yield s != null && !s.trim().isEmpty();
                    }
                    case FORMULA -> {
                        DataFormatter fmt = new DataFormatter(Locale.UK, true);
                        String s = fmt.formatCellValue(cell);
                        yield s != null && !s.trim().isEmpty();
                    }
                    default -> false;
                };
            }
    );
    public static NamedValidator stringEquals(String expected) {
        return new NamedValidator(
                "STRING_EQUALS_" + expected,
                (cell, wb) -> {
                    if (cell == null) return false;
                    String value = cell.getStringCellValue();
                    return expected.equals(value);
                }
        );
    }
    private static boolean isFormulaNumeric(Cell c) {
        try {
            c.getNumericCellValue();
            return true;
        } catch (IllegalStateException ex) {
            return false;
        }
    }
}