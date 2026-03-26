package uk.gov.laa.gpfd.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcOperations;
import uk.gov.laa.gpfd.exception.DatabaseWriteException;
import uk.gov.laa.gpfd.exception.UnableToParseAuthDetailsException;
import uk.gov.laa.gpfd.utils.SecurityUtils;

import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportTrackingDaoTest {

    @Mock
    JdbcOperations trackingJdbcTemplate;

    @Mock
    SecurityUtils securityUtils;

    @InjectMocks
    ReportTrackingDao reportTrackingDao;

    @BeforeEach
    void beforeEach() {
        reset(securityUtils, trackingJdbcTemplate);
    }

    @Test
    void insertTrackingRow_shouldUpdateDbWithCorrectValues() {
        var reportId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        when(securityUtils.extractUserId()).thenReturn(userId.toString());
        reportTrackingDao.insertTrackingRow(reportId);

        verify(trackingJdbcTemplate).update(anyString(),any(UUID.class), eq(reportId), eq(userId), any(Timestamp.class));
    }

    @Test
    void insertTrackingRow_shouldRethrowExceptionsFromJdbc() {
        var reportId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        when(securityUtils.extractUserId()).thenReturn(userId.toString());
        when(trackingJdbcTemplate.update(any(), any(), any(), any(), any())).thenThrow(new DuplicateKeyException("Error :("));
        assertThrows(DatabaseWriteException.class, () -> reportTrackingDao.insertTrackingRow(reportId));
    }

    @Test
    void insertTrackingRow_shouldLetHandlerHandleExceptionsFromSecurityUtils() {
        var reportId = UUID.randomUUID();
        when(securityUtils.extractUserId()).thenThrow(new UnableToParseAuthDetailsException.AuthenticationIsNullException());
        assertThrows(UnableToParseAuthDetailsException.class, () -> reportTrackingDao.insertTrackingRow(reportId));
    }

}