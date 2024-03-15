package uk.gov.laa.pfla.auth.service.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.pfla.auth.service.graph.GraphClientHelper;
import uk.gov.laa.pfla.auth.service.services.MappingTableService;
import uk.gov.laa.pfla.auth.service.services.ReportService;
import uk.gov.laa.pfla.auth.service.services.ReportTrackingTableService;
import uk.gov.laa.pfla.auth.service.services.UserService;

import java.io.ByteArrayOutputStream;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Client;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ReportsControllerSpringBootTest {

    @MockBean
    UserService userService;
    @MockBean
    GraphClientHelper mockGraphClientHelper;
    @MockBean
    private MappingTableService mappingTableServiceMock;
    @MockBean
    private ReportService reportServiceMock;
    @MockBean
    private ReportTrackingTableService reportTrackingTableService;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
    }


    @AfterEach
    void tearDown() {
    }

    @Test
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
        when(reportServiceMock.createCSVResponse(1)).thenReturn(mockResponseEntity);

        // Perform the GET request to the /user endpoint
        mockMvc.perform(MockMvcRequestBuilders.get("/csv/1")
                        .with(oidcLogin())
                        .with(oauth2Client("graph")))
                .andExpect(status().isOk()).andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.csv"));
        verify(reportServiceMock).createCSVResponse(1);
    }

    @Test
    @WithAnonymousUser
    void downloadCsvReturnsCorrectResponseWhenNotAuthenticated() throws Exception {
        // Perform the GET request to the /user endpoint
        mockMvc.perform(MockMvcRequestBuilders.get("/csv/1")).andExpect(status().is3xxRedirection());
        verify(reportServiceMock, never()).createCSVResponse(anyInt());
    }
}