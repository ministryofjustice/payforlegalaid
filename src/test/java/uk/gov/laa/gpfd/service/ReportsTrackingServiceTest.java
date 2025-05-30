package uk.gov.laa.gpfd.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.config.AppConfig;
import uk.gov.laa.gpfd.dao.ReportDao;
import uk.gov.laa.gpfd.dao.ReportsTrackingDao;
import uk.gov.laa.gpfd.data.ReportsTestDataFactory;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.mapper.ReportsTrackingMapper;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.model.ReportsTracking;
import uk.gov.laa.gpfd.services.ReportsTrackingService;
import uk.gov.laa.gpfd.services.UserService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.exception.DatabaseReadException.DatabaseFetchException;

@ExtendWith(MockitoExtension.class)
class ReportsTrackingServiceTest {

    private static final UUID id = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");
    public static final String VALID_TEST_USER = "Valid Test User";

    Report testReportDetails = ReportsTestDataFactory.createTestReport();

    @Mock
    ReportsTrackingDao reportsTrackingDao;
    @Mock
    UserService userService;
    @Mock
    ReportsTrackingMapper reportsTrackingMapper;
    @Mock
    AppConfig config;
    @Mock
    ReportDao reportDetailsDao;

    @InjectMocks
    ReportsTrackingService reportsTrackingService;

    @BeforeEach
    void init() {
        reportsTrackingMapper = new ReportsTrackingMapper(config);
    }

    @Test
    void shouldSuccessfullyUpdateReportsTrackingWhenValidDataIsProvided() {
        // Given
        var requestedId = id;

        when(reportDetailsDao.fetchReportById(requestedId)).thenReturn(Optional.of(testReportDetails));
        when(userService.getCurrentUserName()).thenReturn(VALID_TEST_USER);

        // Then
        reportsTrackingService.saveReportsTracking(requestedId);
        ArgumentCaptor<ReportsTracking> captor = ArgumentCaptor.forClass(ReportsTracking.class);

        // Then
        verify(reportsTrackingDao, times(1)).saveReportsTracking(captor.capture());
    }

    @Test
    void shouldThrowReportIdNotFoundExceptionWhenInvalidReportIdIsProvided() {
        // Given
        var invalidId = id;  // Non-existing ID
        when(reportDetailsDao.fetchReportById(invalidId)).thenThrow(ReportIdNotFoundException.class);

        // When
        // Then
        assertThrows(ReportIdNotFoundException.class,
                () -> reportsTrackingService.saveReportsTracking(invalidId));
    }


    @Test
    void shouldThrowNullPointerExceptionWhenReportListIsEmpty() {
        // Given
        var requestedId = id;
        when(reportDetailsDao.fetchReportById(requestedId)).thenReturn(null);  // Simulate empty report list

        // When
        // Then
        assertThrows(NullPointerException.class,
                () -> reportsTrackingService.saveReportsTracking(requestedId));
    }

    @Test
    void shouldThrowDatabaseReadExceptionWhenDatabaseErrorOccurs() {
        // Given
        var requestedId = id;
        when(reportDetailsDao.fetchReportById(requestedId)).thenReturn(Optional.of(testReportDetails));
        when(userService.getCurrentUserName()).thenReturn(VALID_TEST_USER);

        // Simulate database error
        doThrow(new DatabaseFetchException("Database error")).when(reportsTrackingDao).saveReportsTracking(any());

        // When
        assertThrows(DatabaseFetchException.class,
                () -> reportsTrackingService.saveReportsTracking(requestedId));
    }

    @Test
    void shouldEnsureThreadSafetyWhenMultipleThreadsCallUpdateReportsTracking() throws InterruptedException {
        // Given
        var requestedId = id;

        when(reportDetailsDao.fetchReportById(requestedId)).thenReturn(Optional.of(testReportDetails));
        when(userService.getCurrentUserName()).thenReturn(VALID_TEST_USER);

        // When & Assert
        Thread thread1 = new Thread(() -> reportsTrackingService.saveReportsTracking(requestedId));
        Thread thread2 = new Thread(() -> reportsTrackingService.saveReportsTracking(requestedId));

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        ArgumentCaptor<ReportsTracking> captor = ArgumentCaptor.forClass(ReportsTracking.class);

        // Then
        verify(reportsTrackingDao, times(2)).saveReportsTracking(captor.capture());
    }

}
