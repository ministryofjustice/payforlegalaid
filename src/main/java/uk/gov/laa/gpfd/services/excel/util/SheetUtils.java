package uk.gov.laa.gpfd.services.excel.util;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Optional;

import static java.util.stream.IntStream.range;

/**
 * The interface provides utility methods for working with {@link Sheet} objects in an Apache POI
 * {@link Workbook}. It includes a static method, {@link #findSheetByName(Workbook, String)}, to locate a sheet by its name
 * within a workbook.
 *
 * <p>This interface is designed to simplify common operations related to sheets in a workbook, such as searching for
 * a specific sheet by name. It uses Java 8's {@link Optional} to handle cases where the sheet may not exist, ensuring
 * null-safe and clean code.
 */
public interface SheetUtils {

    /**
     * Searches for a {@link Sheet} with the specified name within the given {@link Workbook}. If a sheet with the
     * matching name is found, it is returned as an {@link Optional}. If no such sheet exists, an empty {@link Optional}
     * is returned.
     *
     * <p>This method iterates through all sheets in the workbook and checks for a match with the provided sheet name.
     * The search is case-sensitive.
     *
     * @param workbook  the workbook in which to search for the sheet
     * @param sheetName the name of the sheet to find
     * @return an {@link Optional} containing the matching sheet if found, otherwise an empty {@link Optional}
     * @throws NullPointerException if the workbook or sheetName is {@code null}
     */
    static Optional<Sheet> findSheetByName(Workbook workbook, String sheetName) {
        return range(0, workbook.getNumberOfSheets())
                .mapToObj(workbook::getSheetAt)
                .filter(sheet -> sheetName.equals(sheet.getSheetName()))
                .findFirst();
    }
}
