package uk.gov.laa.gpfd.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.dao.ReportViewsDao;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    @Mock
    ReportViewsDao reportViewsDAO;

    @Mock
    MappingTableService mappingTableService;

    @Mock
    MetadataReportService metadataReportService;

    @InjectMocks
    ReportService reportService;

    @Test
    void createCSVStreamReturnsCorrectContent() throws Exception {
        // Arrange
        String sqlQuery = "SELECT * FROM exampleTable";
        List<Map<String, Object>> mockResultList = Arrays.asList(
                new LinkedHashMap<String, Object>() {{
                    put("id", 1);
                    put("DATE_AUTHORISED_CIS", Timestamp.valueOf(LocalDateTime.of(2023, 8, 7, 0, 0)));
                    put("name", "Example Report 1");
                }},
                new LinkedHashMap<String, Object>() {{
                    put("id", 2);
                    put("DATE_AUTHORISED_CIS", Timestamp.valueOf(LocalDateTime.of(2023, 12, 31, 1, 50)));
                    put("name", "Example Report 2");
                }}
        );

        when(reportViewsDAO.callDataBase(sqlQuery)).thenReturn(mockResultList);

        // Act
        ByteArrayOutputStream outputStream = reportService.createCsvStream(sqlQuery);

        // Assert
        assertNotNull(outputStream);
        String resultContent = outputStream.toString();
        assertTrue(resultContent.contains("id,DATE_AUTHORISED_CIS,name"));
        assertTrue(resultContent.contains("1,2023-08-07 00:00:00.0,Example Report 1"));
        assertTrue(resultContent.contains("2,2023-12-31 01:50:00.0,Example Report 2"));


        // Verify the interaction with the mock
        verify(reportViewsDAO).callDataBase(sqlQuery);
    }


}