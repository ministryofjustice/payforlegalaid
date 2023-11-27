package uk.gov.laa.pfla.auth.service.helpers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import uk.gov.laa.pfla.auth.service.builders.MappingTableModelBuilder;
import uk.gov.laa.pfla.auth.service.dao.TableName;
import uk.gov.laa.pfla.auth.service.models.MappingTableModel;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class DBRowMapper implements RowMapper<Object> {

    public MappingTableModel mapRow(ResultSet resultSet, int rowNum) throws SQLException{

        return mapMappingTableModel(resultSet);


//        switch (tableName){
//            case "MappingTableModel":
//                return mapMappingTableModel(resultSet);
//            case "ReportTrackingTableModel":
//                return mapReportTrackingTableModel(resultSet);
//            case "ReportTableModel":
//                return mapReportTableModel(resultSet);
//            default:
//        }
    }




    private MappingTableModel mapMappingTableModel(ResultSet resultSet) throws SQLException {
        return new MappingTableModelBuilder()
                .withId(resultSet.getInt(1))
                .withReportName(resultSet.getString(2))
                .withSqlString(resultSet.getString(3))
                .withBaseUrl(resultSet.getString(4))
                .withReportPeriod(resultSet.getString(5))
                .withReportOwner(resultSet.getString(6))
                .withReportCreator(resultSet.getString(7))
                .withReportDescription(resultSet.getString(8))
                //
                //
                //
                .withExcelSheetNumber(resultSet.getInt(12))
                .withCsvName(resultSet.getString(13))
                .build();

    }

    private MappingTableModel mapReportTrackingTableModel(ResultSet resultSet) {
        return null;
    }

    private MappingTableModel mapReportTableModel(ResultSet resultSet) {
        return null;
    }

}
