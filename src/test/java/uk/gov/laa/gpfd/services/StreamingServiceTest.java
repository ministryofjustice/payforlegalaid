package uk.gov.laa.gpfd.services;


import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import uk.gov.laa.gpfd.data.ReportsTestDataFactory;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.model.Report;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StreamingServiceTest {
    private static final String APPLICATION_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Mock
    private Workbook workbook;

    @Mock
    private Report report;

    @Mock
    private ExcelService excelService;

    @InjectMocks
    private StreamingService streamingService;

    @Test
    void shouldSuccessfullyCreateExcel() {
        // Given
        var uuid = UUID.fromString(ReportsTestDataFactory.REPORT_UUID_1);
        when(excelService.createExcel(uuid)).thenReturn(Pair.of(report, workbook));

        // When
        var response = streamingService.streamExcel(uuid);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(APPLICATION_EXCEL, response.getHeaders().getFirst("Content-Type"));
        assertEquals("attachment; filename=null.xlsx", response.getHeaders().getFirst("Content-Disposition"));
    }

    @Test
    @SneakyThrows
    void shouldFailWhenCouldNotFindReport() {
        // Given
        var uuid = UUID.fromString(ReportsTestDataFactory.REPORT_UUID_1);
        when(excelService.createExcel(uuid)).thenThrow(new ReportIdNotFoundException("Report not found for ID: %s".formatted(uuid.toString())));

        // When & Then
        assertThrows(ReportIdNotFoundException.class, () -> streamingService.streamExcel(uuid));
    }

}