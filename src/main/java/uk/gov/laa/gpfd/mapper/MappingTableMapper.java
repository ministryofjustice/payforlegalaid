package uk.gov.laa.gpfd.mapper;

import uk.gov.laa.gpfd.model.MappingTable;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * A utility class that provides functionality to map a {@link Map} to a {@link MappingTable} object.
 */
public class MappingTableMapper {
    private static final String ID = "ID";
    private static final String REPORT_NAME = "REPORT_NAME";
    private static final String SQL_QUERY = "SQL_QUERY";
    private static final String BASE_URL = "BASE_URL";
    private static final String REPORT_OWNER = "REPORT_OWNER";
    private static final String REPORT_CREATOR = "REPORT_CREATOR";
    private static final String REPORT_DESCRIPTION = "REPORT_DESCRIPTION";
    private static final String EXCEL_REPORT = "EXCEL_REPORT";
    private static final String EXCEL_SHEET_NUM = "EXCEL_SHEET_NUM";
    private static final String CSV_NAME = "CSV_NAME";
    private static final String OWNER_EMAIL = "OWNER_EMAIL";

    /**
     * Maps a given {@link Map} of String keys to corresponding values to a {@link MappingTable} object.
     * <p>
     * This method retrieves the values from the map and constructs a {@link MappingTable} object using those values.
     * It ensures that the mapping is done correctly even if the map contains unexpected null values.
     * </p>
     *
     * @param obj the {@link Map} containing the keys and values to be mapped
     * @return a {@link MappingTable} object populated with the values from the map
     */
    public static MappingTable mapToMappingTable(Map<String, Object> obj) {
        var id = getInt(obj, ID);
        var reportName = (String) obj.get(REPORT_NAME);
        var sqlQuery = (String) obj.get(SQL_QUERY);
        var baseUrl = (String) obj.get(BASE_URL);
        var reportOwner = (String) obj.get(REPORT_OWNER);
        var reportCreator = (String) obj.get(REPORT_CREATOR);
        var description = (String) obj.get(REPORT_DESCRIPTION);
        var excelReport = (String) obj.get(EXCEL_REPORT);
        var excelSheetNum = getInt(obj, EXCEL_SHEET_NUM);
        var csvName = (String) obj.get(CSV_NAME);
        var ownerEmail = (String) obj.get(OWNER_EMAIL);

        return new MappingTable(
                id,
                reportName,
                excelReport,
                csvName,
                excelSheetNum,
                sqlQuery,
                baseUrl,
                reportOwner,
                reportCreator,
                description,
                ownerEmail
        );
    }

    /**
     * Utility method to safely extract integer values from the map.
     * <p>
     * This method is specifically designed to handle {@link BigDecimal} objects in the map
     * and convert them to an integer. If the value is not a valid {@link BigDecimal} or is null,
     * it returns a default value of 0.
     * </p>
     *
     * @param obj the map containing the data
     * @param key the key whose value needs to be extracted
     * @return the integer value corresponding to the key or 0 if the value is invalid or null
     */
    private static int getInt(Map<String, Object> obj, String key) {
        return Optional.ofNullable(obj.get(key))
                .filter(BigDecimal.class::isInstance)
                .map(BigDecimal.class::cast)
                .map(BigDecimal::intValue)
                .orElse(0);
    }
}
