package uk.gov.laa.gpfd.mapper;

import uk.gov.laa.gpfd.model.MappingTable;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;

/**
 * Utility class responsible for mapping a {@link MappingTable} object to a {@link ReportsGet200ResponseReportListInner} object.
 * <p>
 * This class provides a method to transform a {@link MappingTable} entity (which holds CSV to SQL mapping details)
 * into a response object that can be returned in an API response.
 * </p>
 */
public class ReportsGet200ResponseReportListInnerMapper {

    /**
     * Maps a {@link MappingTable} object to a {@link ReportsGet200ResponseReportListInner} object.
     * <p>
     * This method transforms a given {@link MappingTable} entity into a response object used by the service layer
     * to expose report details such as report name, SQL query, and other metadata to the client.
     * </p>
     *
     * @param mappingTable the {@link MappingTable} object containing the data to be mapped
     * @return a {@link ReportsGet200ResponseReportListInner} object populated with data from the provided {@link MappingTable}
     */
    public static ReportsGet200ResponseReportListInner map(MappingTable mappingTable) {
        return new ReportsGet200ResponseReportListInner() {{
            setId(mappingTable.getId());
            setReportName(mappingTable.getReportName());
            description(mappingTable.getDescription());
        }};
    }
}
