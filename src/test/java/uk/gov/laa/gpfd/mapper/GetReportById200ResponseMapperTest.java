package uk.gov.laa.gpfd.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.config.AppConfig;
import uk.gov.laa.gpfd.data.ReportsTestDataFactory;
import uk.gov.laa.gpfd.model.Report;

import java.net.URI;

import static java.util.UUID.fromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetReportById200ResponseMapperTest {
    Report report = ReportsTestDataFactory.createTestReport(fromString("8dd30c01-700a-4790-96d6-bd5440a31692"));

    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private GetReportById200ResponseMapper mapper;

    @Test
    void shouldReturnCorrectResponse() {
        var reportId = fromString("8dd30c01-700a-4790-96d6-bd5440a31692");

        when(appConfig.getServiceUrl()).thenReturn("https://api.example.com");

        var response = mapper.map(report);

        assertNotNull(response);
        assertEquals(reportId, response.getId());
        assertEquals("Test Report", response.getReportName());
        assertEquals(URI.create("https://api.example.com/excel/" + reportId), response.getReportDownloadUrl());
    }

    @Test
    void shouldHandleTrailingSlashesInBaseUrl() {
        when(appConfig.getServiceUrl()).thenReturn("https://api.example.com///");

        var response = mapper.map(report);

        assertFalse(response.getReportDownloadUrl().toString().contains("///"));
    }

    @Test
    void shouldThrowWhenReportIsNull() {
        assertThrows(NullPointerException.class, () -> mapper.map(null));
    }

    @Test
    void shouldThrowForInvalidUrl() {
        when(appConfig.getServiceUrl()).thenReturn("invalid url");

        assertThrows(IllegalStateException.class, () -> mapper.map(report));
    }

}
