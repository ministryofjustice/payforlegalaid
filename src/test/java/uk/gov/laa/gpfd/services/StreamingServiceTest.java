package uk.gov.laa.gpfd.services;


import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import uk.gov.laa.gpfd.model.Report;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
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
    void testStreamExcel_Success() {
        // Given
        var uuid = UUID.fromString("b36f9bbb-1178-432c-8f99-8090e285f2d3");
        var report = mock(Report.class);
        Workbook workbook = mock(Workbook.class);

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

        // When
        var deferredResult = streamingService.streamExcel(response, uuid);

        // Then
        assertNotNull(deferredResult);
        assertTrue(deferredResult.hasResult());
    }

}