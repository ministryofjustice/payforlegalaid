package uk.gov.laa.gpfd.config.builders;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.laa.gpfd.builders.ReportResponseTestBuilder;
import uk.gov.laa.gpfd.services.ReportService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HttpSecuritySessionManagementConfigurerBuilderLocalTest {

    @MockBean
    ReportService reportServiceMock;

    @Autowired
    MockMvc mockMvc;

    // Local profile just ignores Azure and requires no login session.
    @Test
    void shouldNotRedirectToAzureLoginEvenIfNoActiveSession() throws Exception {
        var reportId = 2;
        var reportResponseMock = new ReportResponseTestBuilder().withId(reportId).createReportResponse();

        when(reportServiceMock.createReportResponse(reportId)).thenReturn(reportResponseMock);

        mockMvc.perform(get("/report/{id}", reportId)
                        .sessionAttr("SPRING_SECURITY_CONTEXT", "null"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(reportId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldLoadPageIfValidSession() throws Exception {
        var reportId = 2;
        var reportResponseMock = new ReportResponseTestBuilder().withId(reportId).createReportResponse();

        when(reportServiceMock.createReportResponse(reportId)).thenReturn(reportResponseMock);

        mockMvc.perform(get("/report/{id}", reportId))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(reportId));
    }

    @Test
    public void shouldOnlyAllowSameOriginExternalFrames() throws Exception {
        var reportId = 2;
        var reportResponseMock = new ReportResponseTestBuilder().withId(reportId).createReportResponse();

        when(reportServiceMock.createReportResponse(reportId)).thenReturn(reportResponseMock);

        mockMvc.perform(get("/report/{id}", reportId))
                .andExpect(status().isOk())
                // This is the header that tells the browser what to allow.
                .andExpect(header().string("X-Frame-Options", "SAMEORIGIN"));
    }

}
