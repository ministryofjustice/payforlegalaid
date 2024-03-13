package uk.gov.laa.pfla.auth.service.controllers;

import com.microsoft.graph.models.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.security.test.context.support.WithMockUser;

import uk.gov.laa.pfla.auth.service.graph.GraphClientHelper;
import uk.gov.laa.pfla.auth.service.services.MappingTableService;
import uk.gov.laa.pfla.auth.service.services.ReportService;
import uk.gov.laa.pfla.auth.service.services.ReportTrackingTableService;
import uk.gov.laa.pfla.auth.service.services.UserService;

import java.io.ByteArrayOutputStream;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

//@ExtendWith(MockitoExtension.class)
//@WebMvcTest(controllers = ReportsController.class,
//        excludeAutoConfiguration = {SecurityAutoConfiguration.class, OAuth2ClientAutoConfiguration.class})
@SpringBootTest
@AutoConfigureMockMvc
class ReportsControllerWITHMVCTest {



    @MockBean
    private MappingTableService mappingTableServiceMock;

    @MockBean
    private ReportService reportServiceMock;

    @MockBean //This is used, despite what sonarlint  might say
    private ReportTrackingTableService reportTrackingTableService;

    @MockBean
    UserService userService;

    @MockBean
    GraphClientHelper mockGraphClientHelper;

    @MockBean
    private OAuth2AuthorizedClientService mockOAuth2ClientService;

    @MockBean
    private OAuth2AuthorizedClient mockOAuth2Client;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

//    @InjectMocks // creating a ReportsController object and then inject the mocked MappingTableService + reportService instances into it.
//    private ReportsController reportsController;

//    @BeforeEach
//    public void setup() {
//        // Setup MockMvc to include Spring Security and the full application context
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .apply(springSecurity()) // Apply Spring Security configuration
//                .build();
//    }

    @NotNull
    private static User createGraphUser() {
        User graphUser = new User();
        graphUser.userPrincipalName = "testPrincipalName";
        graphUser.givenName = "testGivenName";
        graphUser.surname = "testSurname";
        graphUser.preferredName = "testPreferredName";
        graphUser.mail = "testMail";
        return graphUser;
    }



    @Test
    @WithMockUser(roles="ADMIN")
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
        ResponseEntity<StreamingResponseBody> mockResponseEntity = ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=data.csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);

        // Mocking one level ABOVE OAuth2AuthorizedClient, trying to avoid error: IllegalStateException: No primary or single unique constructor found for class org.springframework.security.oauth2.client.OAuth2AuthorizedClient
        when(mockOAuth2ClientService.loadAuthorizedClient(anyString(), anyString())).thenReturn(mockOAuth2Client);

        // Mock method call
        when(reportServiceMock.createCSVResponse(1)).thenReturn(mockResponseEntity);


        // Act & Assert
        mockMvc.perform(get("/csv/1"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.csv"))
                .andExpect((ResultMatcher) content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect((ResultMatcher) content().string(csvDataOutputStream.toString()));

        verify(reportServiceMock).createCSVResponse(1);

    }

}
