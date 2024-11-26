package uk.gov.laa.gpfd.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.dao.ReportTrackingTableDao;
import uk.gov.laa.gpfd.data.UserDetailsTestDataFactory;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;
import uk.gov.laa.gpfd.model.ReportTrackingTable;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportTrackingTableServiceTest {

    @Mock
    ReportTrackingTableDao reportTrackingTableDao;

    @Mock
    OAuth2AuthorizedClient graphClient;

    @Mock
    UserService userService;

    @Mock
    MappingTableService mappingTableService;

    @InjectMocks
    ReportTrackingTableService reportTrackingTableService;

    @Test
    void shouldSuccessfullyUpdateReportTrackingTableWhenValidDataIsProvided() {
        // Given
        var requestedId = 1;
        var reportDetails = new ReportsGet200ResponseReportListInner() {{
            setReportName("Report 1");
            setId(1);
        }};

        when(mappingTableService.getDetailsForSpecificReport(requestedId)).thenReturn(reportDetails);
        when(userService.getUserDetails(graphClient)).thenReturn(UserDetailsTestDataFactory.aValidUserDetails());

        // Then
        reportTrackingTableService.updateReportTrackingTable(requestedId, graphClient);
        ArgumentCaptor<ReportTrackingTable> captor = ArgumentCaptor.forClass(ReportTrackingTable.class);

        // Then
        verify(reportTrackingTableDao, times(1)).updateTrackingTable(captor.capture());
        ReportTrackingTable capturedTrackingTable = captor.getValue();
        assertEquals(reportDetails.getReportName(), capturedTrackingTable.reportName());
        assertEquals("www.sharepoint.com/place-where-we-will-create-report", capturedTrackingTable.reportUrl());
        assertEquals(reportDetails.getId(), capturedTrackingTable.mappingId());
        assertEquals("Foo Bar", capturedTrackingTable.reportGeneratedBy());
        assertNotNull(capturedTrackingTable.creationTime());
    }

    @Test
    void shouldThrowReportIdNotFoundExceptionWhenInvalidReportIdIsProvided() {
        // Given
        int invalidId = 9999;  // Non-existing ID
        when(mappingTableService.getDetailsForSpecificReport(invalidId)).thenThrow(ReportIdNotFoundException.class);

        // When
        // Then
        assertThrows(ReportIdNotFoundException.class,
                () -> reportTrackingTableService.updateReportTrackingTable(invalidId, graphClient));
    }

    @Test
    void shouldThrowNullPointerExceptionWhenUserDetailsAreNull() {
        // Given
        int requestedId = 1;
        var reportDetails = new ReportsGet200ResponseReportListInner() {{
            setReportName("Report 1");
            setId(1);
        }};
        when(mappingTableService.getDetailsForSpecificReport(requestedId)).thenReturn(reportDetails);
        when(userService.getUserDetails(graphClient)).thenReturn(null);  // Simulate null user details

        // When
        // Then
        assertThrows(NullPointerException.class,
                () -> reportTrackingTableService.updateReportTrackingTable(requestedId, graphClient));
    }

    @Test
    void shouldThrowNullPointerExceptionWhenReportListIsEmpty() {
        // Given
        int requestedId = 1;
        when(mappingTableService.getDetailsForSpecificReport(requestedId)).thenReturn(null);  // Simulate empty report list

        // When
        // Then
        assertThrows(NullPointerException.class,
                () -> reportTrackingTableService.updateReportTrackingTable(requestedId, graphClient));
    }

    @Test
    void shouldThrowDatabaseReadExceptionWhenDatabaseErrorOccurs() {
        // Given
        int requestedId = 1;
        var reportDetails = new ReportsGet200ResponseReportListInner() {{
            setReportName("Report 1");
            setId(1);
        }};
        when(mappingTableService.getDetailsForSpecificReport(requestedId)).thenReturn(reportDetails);
        when(userService.getUserDetails(graphClient)).thenReturn(UserDetailsTestDataFactory.aValidUserDetails());

        // Simulate database error
        doThrow(new DatabaseReadException("Database error")).when(reportTrackingTableDao).updateTrackingTable(any());

        // When
        assertThrows(DatabaseReadException.class,
                () -> reportTrackingTableService.updateReportTrackingTable(requestedId, graphClient));
    }

    @Test
    void shouldEnsureThreadSafetyWhenMultipleThreadsCallUpdateReportTrackingTable() throws InterruptedException {
        // Given
        var requestedId = 1;
        var reportDetails = new ReportsGet200ResponseReportListInner() {{
            setReportName("Report 1");
            setId(1);
        }};

        when(mappingTableService.getDetailsForSpecificReport(requestedId)).thenReturn(reportDetails);
        when(userService.getUserDetails(graphClient)).thenReturn(UserDetailsTestDataFactory.aValidUserDetails());

        // When & Assert
        Thread thread1 = new Thread(() -> reportTrackingTableService.updateReportTrackingTable(requestedId, graphClient));
        Thread thread2 = new Thread(() -> reportTrackingTableService.updateReportTrackingTable(requestedId, graphClient));

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        ArgumentCaptor<ReportTrackingTable> captor = ArgumentCaptor.forClass(ReportTrackingTable.class);

        // Then
        verify(reportTrackingTableDao, times(2)).updateTrackingTable(captor.capture());
        ReportTrackingTable capturedTrackingTable = captor.getValue();
        assertEquals(reportDetails.getReportName(), capturedTrackingTable.reportName());
        assertEquals("www.sharepoint.com/place-where-we-will-create-report", capturedTrackingTable.reportUrl());
        assertEquals(reportDetails.getId(), capturedTrackingTable.mappingId());
        assertEquals("Foo Bar", capturedTrackingTable.reportGeneratedBy());
        assertNotNull(capturedTrackingTable.creationTime());
    }

    @Test
    void shouldCorrectlyFormatReportUrlWhenUpdatingReportTrackingTable() {
        // Given
        int requestedId = 1;
        var reportDetails = new ReportsGet200ResponseReportListInner() {{
            setReportName("Report 1");
            setId(1);
        }};
        when(mappingTableService.getDetailsForSpecificReport(requestedId)).thenReturn(reportDetails);
        when(userService.getUserDetails(graphClient)).thenReturn(UserDetailsTestDataFactory.aValidUserDetails());

        // When
        reportTrackingTableService.updateReportTrackingTable(requestedId, graphClient);

        // Then
        ArgumentCaptor<ReportTrackingTable> captor = ArgumentCaptor.forClass(ReportTrackingTable.class);
        verify(reportTrackingTableDao).updateTrackingTable(captor.capture());
    }

    @Test
    void shouldCorrectlySetReportNameInTrackingTable() {
        // Given
        int requestedId = 1;
        var reportDetails = new ReportsGet200ResponseReportListInner() {{
            setReportName("Report 1");
            setId(1);
        }};
        when(mappingTableService.getDetailsForSpecificReport(requestedId)).thenReturn(reportDetails);
        when(userService.getUserDetails(graphClient)).thenReturn(UserDetailsTestDataFactory.aValidUserDetails());

        // When
        reportTrackingTableService.updateReportTrackingTable(requestedId, graphClient);

        // Then
        ArgumentCaptor<ReportTrackingTable> captor = ArgumentCaptor.forClass(ReportTrackingTable.class);
        verify(reportTrackingTableDao).updateTrackingTable(captor.capture());
    }

    @Test
    void shouldSetCorrectReportGeneratedByInTrackingTable() {
        // Given
        int requestedId = 1;
        var reportDetails = new ReportsGet200ResponseReportListInner() {{
            setReportName("Report 1");
            setId(1);
        }};
        when(mappingTableService.getDetailsForSpecificReport(requestedId)).thenReturn(reportDetails);
        when(userService.getUserDetails(graphClient)).thenReturn(UserDetailsTestDataFactory.aValidUserDetails());

        // When
        reportTrackingTableService.updateReportTrackingTable(requestedId, graphClient);

        // Then
        ArgumentCaptor<ReportTrackingTable> captor = ArgumentCaptor.forClass(ReportTrackingTable.class);
        verify(reportTrackingTableDao).updateTrackingTable(captor.capture());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenUserGeneratedByIsNull() {
        // Given
        int requestedId = 1;
        var reportDetails = new ReportsGet200ResponseReportListInner() {{
            setReportName("Report 1");
            setId(1);
        }};
        when(mappingTableService.getDetailsForSpecificReport(requestedId)).thenReturn(reportDetails);
        when(userService.getUserDetails(graphClient)).thenReturn(null);  // Simulate null user details

        // When
        // Then
        assertThrows(NullPointerException.class,
                () -> reportTrackingTableService.updateReportTrackingTable(requestedId, graphClient));
    }

}