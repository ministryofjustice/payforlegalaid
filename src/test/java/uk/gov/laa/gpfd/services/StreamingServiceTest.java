package uk.gov.laa.gpfd.services;

import static org.junit.jupiter.api.Assertions.*;


import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import uk.gov.laa.gpfd.model.Report;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StreamingServiceTest {

    @Mock
    private ExcelService excelService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private StreamingService streamingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testStreamExcel_Success() throws IOException {
        // Given
        var uuid = UUID.fromString("b36f9bbb-1178-432c-8f99-8090e285f2d3");
        var report = mock(Report.class);
        Workbook workbook = mock(Workbook.class);

        when(excelService.createExcel(uuid)).thenReturn(Pair.of(report, workbook));

        // When
        var deferredResult = streamingService.streamExcel(response, uuid);

        // Then
        assertNotNull(deferredResult);
        verify(response).setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        assertTrue(deferredResult.hasResult());
    }

    @Test
    void testStreamExcel_Error() {
        // Given
        var uuid = UUID.fromString("b36f9bbb-1178-432c-8f99-8090e285f2d3");
        when(excelService.createExcel(uuid)).thenThrow(new RuntimeException("Excel creation failed"));

        // When
        var deferredResult = streamingService.streamExcel(response, uuid);

        // Then
        assertNotNull(deferredResult);
        assertTrue(deferredResult.hasResult());
    }

}