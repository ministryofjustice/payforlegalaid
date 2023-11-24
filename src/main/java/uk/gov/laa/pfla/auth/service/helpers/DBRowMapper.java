package uk.gov.laa.pfla.auth.service.helpers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import uk.gov.laa.pfla.auth.service.builders.MappingTableModelBuilder;
import uk.gov.laa.pfla.auth.service.models.MappingTableModel;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class DBRowMapper implements RowMapper<Object> {

    public MappingTableModel mapRow(ResultSet resultSet, int rowNum) throws SQLException{
        String tableName = resultSet.getMetaData().getTableName(1);
        log.info("Table name: "+ tableName);

        switch (tableName){
            case "MappingTableModel":
                return mapMappingTableModel(resultSet);
            case "ReportTrackingTableModel":
                return mapReportTrackingTableModel(resultSet);
            case "ReportTableModel":
                return mapReportTableModel(resultSet);
            default:
                throw new IllegalArgumentException("Unsupported table: " + tableName);
        }
    }




    private MappingTableModel mapMappingTableModel(ResultSet resultSet) {
        MappingTableModel mappingTableObject = new MappingTableModelBuilder()
                .withId(1)
                .build();

        return mappingTableObject;

    }

    private MappingTableModel mapReportTrackingTableModel(ResultSet resultSet) {
        return null;
    }

    private MappingTableModel mapReportTableModel(ResultSet resultSet) {
        return null;
    }

}
