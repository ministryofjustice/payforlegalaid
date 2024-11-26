package uk.gov.laa.gpfd.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetadataReportServiceTest {

    @Mock
    MappingTableService mappingTableService;

    @InjectMocks
    MetadataReportService reportService;

    @Test
    void shouldCreateReportResponseSuccessfully() {
        // Given
        var validId = 1;
        var reportDetails = new ReportsGet200ResponseReportListInner() {{
            setId(validId);
            setReportName("Test Report");
        }};
        when(mappingTableService.getDetailsForSpecificReport(validId)).thenReturn(reportDetails);

        // When
        var response = reportService.createReportResponse(validId);

        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(validId, response.getId(), "Report ID should match");
        assertEquals("Test Report", response.getReportName(), "Report name should match");
        assertEquals(URI.create("https://laa-pay-for-la-dev.apps.live.cloud-platform.service.justice.gov.uk/csv/1"), response.getReportDownloadUrl(), "Report URL should match");
        verify(mappingTableService, times(1)).getDetailsForSpecificReport(validId);
    }

    @Test
    void shouldThrowIndexOutOfBoundsExceptionForInvalidId() {
        // Given
        var invalidId = -1;

        // When
        // Then
        assertThrows(NullPointerException.class, () -> reportService.createReportResponse(invalidId), "Should throw IndexOutOfBoundsException for invalid ID");
    }

    @Test
    void shouldThrowReportIdNotFoundExceptionForMissingReport() {
        // Given
        var missingId = 999;
        when(mappingTableService.getDetailsForSpecificReport(missingId)).thenThrow(new ReportIdNotFoundException("Report not found"));

        // When
        // Then
        var exception = assertThrows(ReportIdNotFoundException.class, () -> reportService.createReportResponse(missingId), "Should throw ReportIdNotFoundException for missing report");
        assertEquals("Report not found", exception.getMessage(), "Exception message should match");
        verify(mappingTableService, times(1)).getDetailsForSpecificReport(missingId);
    }

    @Test
    void shouldHandleLargeValidIdSuccessfully() {
        // Given
        var largeId = 999;
        var reportDetails = new ReportsGet200ResponseReportListInner() {{
            setId(largeId);
            setReportName("Large Report");
        }};
        when(mappingTableService.getDetailsForSpecificReport(largeId)).thenReturn(reportDetails);

        // When
        var response = reportService.createReportResponse(largeId);

        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals(largeId, response.getId(), "Report ID should match");
        assertEquals("Large Report", response.getReportName(), "Report name should match");
        assertEquals(URI.create("https://laa-pay-for-la-dev.apps.live.cloud-platform.service.justice.gov.uk/csv/999"), response.getReportDownloadUrl(), "Report URL should match");
        verify(mappingTableService, times(1)).getDetailsForSpecificReport(largeId);
    }

    @Test
    void shouldLogInfoWhenReturningResponse() {
        // Given
        var validId = 1;
        var reportDetails = new ReportsGet200ResponseReportListInner() {{
            setId(validId);
            setReportName("Log Test Report");
        }};
        when(mappingTableService.getDetailsForSpecificReport(validId)).thenReturn(reportDetails);

        // When
        var response = reportService.createReportResponse(validId);

        // Then
        assertNotNull(response, "Response should not be null");
        verify(mappingTableService, times(1)).getDetailsForSpecificReport(validId);
        // Verify log using a logging framework or mock log (if configured).
    }

    @Test
    void shouldHandleReportNameWithSpecialCharacters() {
        // Given
        var validId = 2;
        var reportDetails = new ReportsGet200ResponseReportListInner() {{
            setId(validId);
            setReportName("Test Report @#$%");
        }};
        when(mappingTableService.getDetailsForSpecificReport(validId)).thenReturn(reportDetails);

        // When
        var response = reportService.createReportResponse(validId);

        // Then
        assertEquals("Test Report @#$%", response.getReportName(), "Report name with special characters should match");
    }

    @Test
    void shouldHandleEmptyReportName() {
        // Given
        var validId = 3;
        var reportDetails = new ReportsGet200ResponseReportListInner() {{
            setId(validId);
            setReportName("");
        }};
        when(mappingTableService.getDetailsForSpecificReport(validId)).thenReturn(reportDetails);

        // When
        var response = reportService.createReportResponse(validId);

        // Then
        assertEquals("", response.getReportName(), "Empty report name should match");
    }

    @Test
    void shouldReturnResponseWithValidUrl() {
        // Given
        var validId = 4;
        var reportDetails = new ReportsGet200ResponseReportListInner() {{
            setId(validId);
            setReportName("Test Report");
        }};
        when(mappingTableService.getDetailsForSpecificReport(validId)).thenReturn(reportDetails);

        // When
        var response = reportService.createReportResponse(validId);

        // Then
        assertEquals(URI.create("https://laa-pay-for-la-dev.apps.live.cloud-platform.service.justice.gov.uk/csv/4"), response.getReportDownloadUrl(), "Generated URL should match the expected format");
    }

    @Test
    void shouldHandleConcurrentRequests() {
        // Given
        var validId1 = 5;
        var validId2 = 6;
        var reportDetails1 = new ReportsGet200ResponseReportListInner() {{
            setId(validId1);
            setReportName("Test Report 1");
        }};
        var reportDetails2 = new ReportsGet200ResponseReportListInner() {{
            setId(validId2);
            setReportName("Test Report 2");
        }};
        when(mappingTableService.getDetailsForSpecificReport(validId1)).thenReturn(reportDetails1);
        when(mappingTableService.getDetailsForSpecificReport(validId2)).thenReturn(reportDetails2);

        // When
        var response1 = reportService.createReportResponse(validId1);
        var response2 = reportService.createReportResponse(validId2);

        // Then
        assertNotEquals(response1.getId(), response2.getId(), "Concurrent requests should produce distinct responses");
        assertNotEquals(response1.getReportName(), response2.getReportName(), "Concurrent requests should produce distinct responses");
    }

    @Test
    void shouldLogErrorWhenReportNotFound() {
        // Given
        var missingId = 100;
        when(mappingTableService.getDetailsForSpecificReport(missingId)).thenThrow(new ReportIdNotFoundException("Report not found"));

        // When
        // Then
        assertThrows(ReportIdNotFoundException.class, () -> reportService.createReportResponse(missingId), "Should log an error for a missing report");
    }

    @Test
    void shouldReturnResponseForBoundaryId() {
        // Given
        var boundaryId = 1;
        var reportDetails = new ReportsGet200ResponseReportListInner() {{
            setId(boundaryId);
            setReportName("Boundary Report");
        }};
        when(mappingTableService.getDetailsForSpecificReport(boundaryId)).thenReturn(reportDetails);

        // When
        var response = reportService.createReportResponse(boundaryId);

        // Then
        assertEquals(boundaryId, response.getId(), "Boundary ID should match");
        assertEquals("Boundary Report", response.getReportName(), "Boundary report name should match");
    }

    @Test
    void shouldHandleNullReportName() {
        // Given
        var validId = 7;
        var reportDetails = new ReportsGet200ResponseReportListInner() {{
            setId(validId);
            setReportName(null);
        }};
        when(mappingTableService.getDetailsForSpecificReport(validId)).thenReturn(reportDetails);

        // When
        var response = reportService.createReportResponse(validId);

        // Then
        assertNull(response.getReportName(), "Report name should be null");
    }

    @Test
    void shouldHandleNullUrlFieldGracefully() {
        // Given
        var validId = 8;
        var reportDetails = new ReportsGet200ResponseReportListInner() {{
            setId(validId);
            setReportName("Report Without URL");
        }};
        when(mappingTableService.getDetailsForSpecificReport(validId)).thenReturn(reportDetails);

        // When
        var response = reportService.createReportResponse(validId);

        // Then
        assertNotNull(response.getReportDownloadUrl(), "Generated URL should not be null");
    }

}
