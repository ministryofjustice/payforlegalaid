package uk.gov.laa.gpfd.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.laa.gpfd.config.AppConfig;
import uk.gov.laa.gpfd.model.Report;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import static java.util.UUID.fromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.createTestReport;
import static uk.gov.laa.gpfd.data.ReportsTestDataFactory.createTestReportForTacticalSol;

class GetReportById200ResponseMapperTest {
    Report report = createTestReport(fromString("8dd30c01-700a-4790-96d6-bd5440a31692"));

    private GetReportById200ResponseMapper mapper;

    @BeforeEach
    void init() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var constructor = GetReportById200ResponseMapper.class.getDeclaredConstructor(AppConfig.class);
        constructor.setAccessible(true);
        var appConfig = mock(AppConfig.class);
        when(appConfig.getServiceUrl()).thenReturn("https://api.example.com///");

        mapper = constructor.newInstance(appConfig);
    }

    @Test
    void shouldReturnCorrectResponse() {
        var reportId = fromString("8dd30c01-700a-4790-96d6-bd5440a31692");

        var response = mapper.map(report);

        assertNotNull(response);
        assertEquals(reportId, response.getId());
        assertEquals("Test Report", response.getReportName());
        assertEquals(URI.create("https://api.example.com/excel/" + reportId), response.getReportDownloadUrl());
    }

    @Test
    void shouldHandleTrailingSlashesInBaseUrl() {
        var response = mapper.map(report);

        assertFalse(response.getReportDownloadUrl().toString().contains("///"));
    }

    @Test
    void shouldThrowWhenReportIsNull() {
        assertThrows(NullPointerException.class, () -> mapper.map(null));
    }

    @Test
    void shouldReturnCorrectResponseWhenDownloadedFromS3Storage() {
        var reportId = fromString("8dd30c01-700a-4790-96d6-bd5440a31692");

        var response = mapper.map(createTestReportForTacticalSol(reportId));

        assertNotNull(response);
        assertEquals(reportId, response.getId());
        assertEquals("Test Report", response.getReportName());
        assertEquals(URI.create("https://api.example.com/reports/" + reportId + "/file"), response.getReportDownloadUrl());
    }

}
