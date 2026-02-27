package uk.gov.laa.gpfd.config.builders;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.laa.gpfd.builders.ReportResponseTestBuilder;
import uk.gov.laa.gpfd.services.ReportManagementService;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("testauth")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HttpSecuritySessionManagementConfigurerBuilderTest {

    @MockitoBean
    ReportManagementService reportServiceMock;

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldNotHaveAccessToSecureEndpointAfterSessionExpires() throws Exception {
        var reportId = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");
        var reportResponseMock = new ReportResponseTestBuilder().withId(reportId).createReportResponse();

        when(reportServiceMock.createReportResponse(reportId)).thenReturn(reportResponseMock);

        mockMvc.perform(get("/reports/{id}", reportId)
                        .sessionAttr("SPRING_SECURITY_CONTEXT", "null"))
                .andExpect(status().is3xxRedirection())  // Should redirect after session expires
                .andExpect(header().string("Location", "/oauth2/authorization/azure"));  // Check that redirection goes to /login?expired
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldValidSessionDoesNotRedirect() throws Exception {
        var reportId = UUID.fromString("0d4da9ec-b0b3-4371-af10-f375330d85d1");
        var reportResponseMock = new ReportResponseTestBuilder().withId(reportId).createReportResponse();

        when(reportServiceMock.createReportResponse(reportId)).thenReturn(reportResponseMock);

        mockMvc.perform(get("/reports/{id}", reportId))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(reportId.toString()));
    }
}
