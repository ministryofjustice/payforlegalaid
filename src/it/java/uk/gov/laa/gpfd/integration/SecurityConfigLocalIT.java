//package uk.gov.laa.gpfd.integration;
//
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import uk.gov.laa.gpfd.builders.ReportResponseTestBuilder;
//import uk.gov.laa.gpfd.services.ReportService;
//
//import java.util.UUID;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ActiveProfiles("local")
//@AutoConfigureMockMvc
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class SecurityConfigLocalIT {
//
//    @MockitoBean
//    ReportService reportServiceMock;
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @Autowired
//    private JdbcTemplate writeJdbcTemplate;
//
//    @AfterAll
//    void resetDatabase() {
//        writeJdbcTemplate.execute("DROP SCHEMA GPFD");
//    }
//
//    // Local profile just ignores Azure and requires no login session.
//    @Test
//    void shouldNotRedirectToAzureLoginEvenIfNoActiveSession() throws Exception {
//        var reportId = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");
//        var reportResponseMock = new ReportResponseTestBuilder().withId(reportId).createReportResponse();
//
//        when(reportServiceMock.createReportResponse(reportId)).thenReturn(reportResponseMock);
//
//        mockMvc.perform(get("/reports/{id}", reportId)
//                        .sessionAttr("SPRING_SECURITY_CONTEXT", "null"))
//                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(reportId.toString()));
//    }
//
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void shouldLoadPageIfValidSession() throws Exception {
//        var reportId = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");
//        var reportResponseMock = new ReportResponseTestBuilder().withId(reportId).createReportResponse();
//
//        when(reportServiceMock.createReportResponse(reportId)).thenReturn(reportResponseMock);
//
//        mockMvc.perform(get("/reports/{id}", reportId))
//                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(reportId.toString()));
//    }
//
//    @Test
//    public void shouldOnlyAllowSameOriginExternalFrames() throws Exception {
//        var reportId = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");
//        var reportResponseMock = new ReportResponseTestBuilder().withId(reportId).createReportResponse();
//
//        when(reportServiceMock.createReportResponse(reportId)).thenReturn(reportResponseMock);
//
//        mockMvc.perform(get("/reports/{id}", reportId))
//                .andExpect(status().isOk())
//                // This is the header that tells the browser what to allow.
//                .andExpect(header().string("X-Frame-Options", "SAMEORIGIN"));
//    }
//
//}
