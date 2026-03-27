package uk.gov.laa.gpfd.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;
import uk.gov.laa.gpfd.exception.DatabaseWriteException;
import uk.gov.laa.gpfd.utils.SecurityUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public record ReportTrackingDao(@Qualifier("trackingJdbcTemplate") JdbcOperations trackingJdbcTemplate, SecurityUtils securityUtils) {

    private static final String insertSql = "INSERT INTO GLAD.REPORT_TRACKING(ID, REPORT_ID, USER_ID, DOWNLOAD_TIME) VALUES (?, ?, ?, ?)";

    public void insertTrackingRow(UUID reportId) {
        log.info("Inserting report tracking row for {}", reportId);
        var userId = UUID.fromString(securityUtils.extractUserId());

        try {
            trackingJdbcTemplate.update(insertSql, UUID.randomUUID(), reportId, userId, Timestamp.from(Instant.now()));
        } catch (DataAccessException e) {
            var message = "Failed to insert report tracking row for report " + reportId + " / user " + userId;
            log.error("{} with {} exception: {}", message, e.getClass().getName(), e.getMessage());
            throw new DatabaseWriteException(message);
        }
    }

}
