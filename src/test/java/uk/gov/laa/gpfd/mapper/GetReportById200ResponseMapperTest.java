package uk.gov.laa.gpfd.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.utils.UrlBuilder;

import java.net.URI;

import static java.util.UUID.fromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.createTestReport;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.createTestReportForTacticalSol;

@ExtendWith(MockitoExtension.class)
class GetReportById200ResponseMapperTest {
    private final Report report = createTestReport(fromString("8dd30c01-700a-4790-96d6-bd5440a31692"));

    @Mock
    private UrlBuilder urlBuilder;

    @InjectMocks
    private GetReportById200ResponseMapper mapper;

    @Test
    void shouldReturnCorrectResponse() {
        var reportId = fromString("8dd30c01-700a-4790-96d6-bd5440a31692");
        when(urlBuilder.getServiceUrl()).thenReturn("https://api.example.com");

        var response = mapper.map(report);

        assertNotNull(response);
        assertEquals(reportId, response.getId());
        assertEquals("Test Report", response.getReportName());
        assertEquals(URI.create("https://api.example.com/excel/" + reportId), response.getReportDownloadUrl());
    }

    @Test
    void shouldHandleTrailingSlashesInBaseUrl() {
        when(urlBuilder.getServiceUrl()).thenReturn("https://api.example.com");

        var response = mapper.map(report);

        assertFalse(response.getReportDownloadUrl().toString().contains("///"));
    }

    @Test
    void shouldThrowWhenReportIsNull() {
        assertThrows(NullPointerException.class, () -> mapper.map(null));
    }

    @Test
    void shouldReturnCorrectResponseWhenDownloadedFromS3Storage() {
        when(urlBuilder.getServiceUrl()).thenReturn("https://api.example.com");

        var reportId = fromString("8dd30c01-700a-4790-96d6-bd5440a31692");

        var response = mapper.map(createTestReportForTacticalSol(reportId));

        assertNotNull(response);
        assertEquals(reportId, response.getId());
        assertEquals("Test Report", response.getReportName());
        assertEquals(URI.create("https://api.example.com/reports/" + reportId + "/file"), response.getReportDownloadUrl());
    }

}
