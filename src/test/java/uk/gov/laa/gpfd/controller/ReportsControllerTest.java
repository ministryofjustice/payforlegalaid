package uk.gov.laa.gpfd.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.builders.ReportResponseTestBuilder;
import uk.gov.laa.gpfd.data.ReportListEntryTestDataFactory;
import uk.gov.laa.gpfd.model.FileExtension;
import uk.gov.laa.gpfd.model.GetReportById200Response;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;
import uk.gov.laa.gpfd.services.ReportManagementService;
import uk.gov.laa.gpfd.services.ReportsTrackingService;
import uk.gov.laa.gpfd.services.StreamingService;
import uk.gov.laa.gpfd.services.s3.FileDownloadService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Client;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("testauth")
class ReportsControllerTest {

    public static final UUID DEFAULT_ID = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");

    @MockitoBean
    ReportManagementService reportManagementServiceMock;

    @MockitoBean
    ReportsTrackingService reportsTrackingService; // This is required, despite the sonarlint suggestions

    @MockitoBean
    StreamingService streamingService;

    @MockitoBean
    FileDownloadService fileDownloadService;

    @Autowired
    MockMvc mockMvc;

   /* @Test
    @WithMockUser(roles = "ADMIN")
    void downloadCsvReturnsCorrectResponse() throws Exception {
        // Mock CSV data
        ByteArrayOutputStream csvDataOutputStream = new ByteArrayOutputStream();
        csvDataOutputStream.write("1,John,Doe\n".getBytes());
        csvDataOutputStream.write("2,Jane,Smith\n".getBytes());

        // Mock response body
        StreamingResponseBody responseBody = outputStream -> {
            csvDataOutputStream.writeTo(outputStream);
            outputStream.flush();
        };

        // Mock ResponseEntity
        ResponseEntity<StreamingResponseBody> mockResponseEntity = ResponseEntity.ok().header("Content-Disposition", "attachment; filename=data.csv").contentType(MediaType.APPLICATION_OCTET_STREAM).body(responseBody);

        // Mock method call
        when(streamingService.stream(DEFAULT_ID, FileExtension.CSV)).thenReturn(mockResponseEntity);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/csv/0d4da9ec-b0b3-4371-af10-f375330d85d1").with(oidcLogin()).with(oauth2Client("graph"))).andExpect(status().isOk()).andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.csv"));
        verify(streamingService).stream(DEFAULT_ID, FileExtension.CSV);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getReportListReturnsCorrectResponseEntity() throws Exception {
        //Create Mock Response objects
        ReportsGet200ResponseReportListInner reportListEntryMock1 = ReportListEntryTestDataFactory.aValidReportsGet200ResponseReportListInner();
        ReportsGet200ResponseReportListInner reportListEntryMock2 = ReportListEntryTestDataFactory.aValidReportsGet200ResponseReportListInnerWithCustomId(DEFAULT_ID);

        //Add mock response objects to a list
        List<ReportsGet200ResponseReportListInner> reportListResponseMockList = Arrays.asList(reportListEntryMock1, reportListEntryMock2);
        // Mock the Service call
        when(reportManagementServiceMock.fetchReportListEntries()).thenReturn(reportListResponseMockList);

        // Perform request and assert results
        mockMvc.perform(MockMvcRequestBuilders.get("/reports"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.reportList", hasSize(2)))
                .andExpect(jsonPath("$.reportList[0].id").value(reportListEntryMock1.getId().toString()))
                .andExpect(jsonPath("$.reportList[1].id").value(reportListEntryMock2.getId().toString()));

        verify(reportManagementServiceMock, times(1)).fetchReportListEntries();

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getReportReturnsCorrectResponseEntity() throws Exception {
        var reportId = DEFAULT_ID;

        GetReportById200Response reportResponseMock = new ReportResponseTestBuilder().withId(reportId).createReportResponse();

        // Mock the service
        when(reportManagementServiceMock.createReportResponse(reportId)).thenReturn(reportResponseMock);

        // Perform request and assert results
        mockMvc.perform(MockMvcRequestBuilders.get("/reports/{id}", reportId)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(reportId.toString())).andExpect(jsonPath("$.reportName").value(reportResponseMock.getReportName()));

        verify(reportManagementServiceMock, times(1)).createReportResponse(reportId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getReportDownloadByIdReturnsCorrectResponseEntity() throws Exception {
        var reportId = DEFAULT_ID;

        var inputStreamResource = new InputStreamResource(new ByteArrayInputStream("test".getBytes()));
        var mockResponse = ResponseEntity.ok(inputStreamResource);

        when(fileDownloadService.getFileStreamResponse(reportId)).thenReturn(mockResponse);

        var result = mockMvc.perform(MockMvcRequestBuilders.get("/reports/{id}/file", reportId))
                .andExpect(status().isOk()).andReturn();

        assertEquals("test", result.getResponse().getContentAsString());
        verify(fileDownloadService, times(1)).getFileStreamResponse(reportId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getReportDownloadByIdReturnsErrorWhenIdInvalid() throws Exception {
        var reportId = "not a uuid";

        mockMvc.perform(MockMvcRequestBuilders.get("/reports/{id}/file", reportId))
                .andExpect(status().isBadRequest()).andReturn();
    }*/

}