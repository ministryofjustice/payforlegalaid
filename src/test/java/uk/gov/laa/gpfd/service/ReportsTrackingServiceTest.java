package uk.gov.laa.gpfd.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.dao.ReportsTrackingDao;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.model.ReportDetails;
import uk.gov.laa.gpfd.model.ReportsTracking;
import uk.gov.laa.gpfd.services.ReportManagementService;
import uk.gov.laa.gpfd.services.ReportsTrackingService;
import uk.gov.laa.gpfd.services.UserService;

@ExtendWith(MockitoExtension.class)
class ReportsTrackingServiceTest {

    private static final UUID id = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");
    public static final String VALID_TEST_USER = "Valid Test User";
    private final ReportDetails testReportDetails = ReportDetails
        .builder()
        .id(id)
        .name("Report 1")
        .reportOutputType(UUID.fromString("00000000-0000-0000-0006-000000000001"))
        .reportDownloadUrl("testUrl/"+id.toString())
        .build();

    @Mock
    ReportsTrackingDao reportsTrackingDao;

    @Mock
    UserService userService;

    @Mock
    ReportManagementService reportManagementService;

    @InjectMocks
    ReportsTrackingService reportsTrackingService;

    @Test
    void shouldSuccessfullyUpdateReportsTrackingWhenValidDataIsProvided() {
        // Given
        var requestedId = id;

        when(reportManagementService.getDetailsForSpecificReport(requestedId)).thenReturn(testReportDetails);
        when(userService.getCurrentUserName()).thenReturn(VALID_TEST_USER);

        // Then
        reportsTrackingService.saveReportsTracking(requestedId);
        ArgumentCaptor<ReportsTracking> captor = ArgumentCaptor.forClass(ReportsTracking.class);

        // Then
        verify(reportsTrackingDao, times(1)).saveReportsTracking(captor.capture());
        ReportsTracking capturedTrackingTable = captor.getValue();
        assertEquals(testReportDetails.getName(), capturedTrackingTable.getName());
        assertEquals(testReportDetails.getReportDownloadUrl(), capturedTrackingTable.getReportUrl());
        assertEquals(testReportDetails.getId(), capturedTrackingTable.getReportId());
        assertEquals(VALID_TEST_USER, capturedTrackingTable.getReportCreator());
        assertNotNull(capturedTrackingTable.getCreationDate());
    }

    @Test
    void shouldThrowReportIdNotFoundExceptionWhenInvalidReportIdIsProvided() {
        // Given
        var invalidId = id;  // Non-existing ID
        when(reportManagementService.getDetailsForSpecificReport(invalidId)).thenThrow(ReportIdNotFoundException.class);

        // When
        // Then
        assertThrows(ReportIdNotFoundException.class,
                () -> reportsTrackingService.saveReportsTracking(invalidId));
    }


    @Test
    void shouldThrowNullPointerExceptionWhenReportListIsEmpty() {
        // Given
        var requestedId = id;
        when(reportManagementService.getDetailsForSpecificReport(requestedId)).thenReturn(null);  // Simulate empty report list

        // When
        // Then
        assertThrows(NullPointerException.class,
                () -> reportsTrackingService.saveReportsTracking(requestedId));
    }

    @Test
    void shouldThrowDatabaseReadExceptionWhenDatabaseErrorOccurs() {
        // Given
        var requestedId = id;
        when(reportManagementService.getDetailsForSpecificReport(requestedId)).thenReturn(testReportDetails);
        when(userService.getCurrentUserName()).thenReturn(VALID_TEST_USER);

        // Simulate database error
        doThrow(new DatabaseReadException("Database error")).when(reportsTrackingDao).saveReportsTracking(any());

        // When
        assertThrows(DatabaseReadException.class,
                () -> reportsTrackingService.saveReportsTracking(requestedId));
    }

    @Test
    void shouldEnsureThreadSafetyWhenMultipleThreadsCallUpdateReportsTracking() throws InterruptedException {
        // Given
        var requestedId = id;

        when(reportManagementService.getDetailsForSpecificReport(requestedId)).thenReturn(testReportDetails);
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
        ReportsTracking capturedTrackingTable = captor.getValue();
        assertEquals(testReportDetails.getName(), capturedTrackingTable.getName());
        assertEquals(testReportDetails.getReportDownloadUrl(), capturedTrackingTable.getReportUrl());
        assertEquals(testReportDetails.getId(), capturedTrackingTable.getReportId());
        assertEquals(VALID_TEST_USER, capturedTrackingTable.getReportCreator());
        assertNotNull(capturedTrackingTable.getCreationDate());
    }

}