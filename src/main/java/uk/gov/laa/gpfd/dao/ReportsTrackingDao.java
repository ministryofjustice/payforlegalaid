package uk.gov.laa.gpfd.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.model.ReportsTracking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReportsTrackingDao {
    private static final String TRACKING_REPORT_CREATED_SQL = "INSERT INTO GPFDS.REPORT_TRACKING (" +
            "ID, NAME, REPORT_ID, CREATION_DATE, REPORT_CREATOR, REPORT_OWNER, REPORT_OUTPUT_TYPE, TEMPLATE_URL, REPORT_URL) " +
            "VALUES (?,?,?,?,?,?,?,?,?)";

    private static final String TRACKING_REPORT_GENERATED_SQL = "INSERT INTO GPFDS.REPORT_TRACKING (" +
            "ID, NAME, REPORT_ID, CREATION_DATE, REPORT_CREATOR, REPORT_OWNER, REPORT_OUTPUT_TYPE, TEMPLATE_URL, REPORT_URL, REPORT_GENERATED_BY) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?)";

    private static final String TRACKING_REPORT_DOWNLOADED_SQL = "INSERT INTO GPFDS.REPORT_TRACKING (" +
            "ID, NAME, REPORT_ID, CREATION_DATE, REPORT_CREATOR, REPORT_OWNER, REPORT_OUTPUT_TYPE, TEMPLATE_URL, REPORT_URL, REPORT_DOWNLOADED_BY) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?)";

    private final JdbcTemplate writeJdbcTemplate;

    public void insertReportedCreatedEvent(ReportsTracking trackingModel) {
        int numberOfRowsAffected = writeJdbcTemplate.update(
                TRACKING_REPORT_CREATED_SQL,
                UUID.randomUUID().toString(),
                trackingModel.getName(),
                trackingModel.getReportId(),
                trackingModel.getCreationDate(),
                trackingModel.getReportCreator(),
                trackingModel.getReportOwner(),
                trackingModel.getReportOutputType(),
                trackingModel.getTemplateUrl(),
                trackingModel.getReportUrl());
    }

    public void insertReportedGeneratedEvent(ReportsTracking trackingModel) {
        int numberOfRowsAffected = writeJdbcTemplate.update(
                TRACKING_REPORT_GENERATED_SQL,
                UUID.randomUUID().toString(),
                trackingModel.getName(),
                trackingModel.getReportId(),
                trackingModel.getCreationDate(),
                trackingModel.getReportCreator(),
                trackingModel.getReportOwner(),
                trackingModel.getReportOutputType(),
                trackingModel.getTemplateUrl(),
                trackingModel.getReportUrl(),
                trackingModel.getReportGeneratedBy());
    }

    public void insertReportedDownloadedEvent(ReportsTracking trackingModel) {
        int numberOfRowsAffected = writeJdbcTemplate.update(
                TRACKING_REPORT_DOWNLOADED_SQL,
                UUID.randomUUID().toString(),
                trackingModel.getName(),
                trackingModel.getReportId(),
                trackingModel.getCreationDate(),
                trackingModel.getReportCreator(),
                trackingModel.getReportOwner(),
                trackingModel.getReportOutputType(),
                trackingModel.getTemplateUrl(),
                trackingModel.getReportUrl(),
                trackingModel.getReportDownloadedBy());
    }

}
