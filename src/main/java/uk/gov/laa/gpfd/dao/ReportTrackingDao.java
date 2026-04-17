package uk.gov.laa.gpfd.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;
import uk.gov.laa.gpfd.exception.DatabaseWriteException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class ReportTrackingDao {

    private final JdbcOperations trackingJdbcTemplate;

    protected static final String INSERT_INTO_TRACKING_SQL = "INSERT INTO GLAD.REPORT_TRACKING(ID, REPORT_ID, USER_ID, DOWNLOAD_TIME) VALUES (?, ?, ?, ?)";

    public ReportTrackingDao(JdbcOperations trackingJdbcTemplate) {
        this.trackingJdbcTemplate = trackingJdbcTemplate;
    }

    /**
     * Insert an entry into our tracking table
     *
     * @param reportId report being downloaded
     * @param userId   user doing the download
     */
    public void insertTrackingRow(UUID reportId, UUID userId) {
        log.info("Inserting report tracking row for {}", reportId);

        try {
            trackingJdbcTemplate.update(INSERT_INTO_TRACKING_SQL, UUID.randomUUID(), reportId, userId, Timestamp.from(Instant.now()));
        } catch (DataAccessException e) {
            var message = "Failed to insert report tracking row for report " + reportId + " / user " + userId;
            log.error("{} with {} exception: {}", message, e.getClass().getName(), e.getMessage());
            throw new DatabaseWriteException(message);
        }
    }

}
