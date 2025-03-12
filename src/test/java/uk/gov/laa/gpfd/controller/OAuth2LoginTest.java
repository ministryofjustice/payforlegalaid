package uk.gov.laa.gpfd.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.laa.gpfd.config.TestAuthConfig;
import uk.gov.laa.gpfd.data.ReportListEntryTestDataFactory;
import uk.gov.laa.gpfd.graph.AzureGraphClient;
import uk.gov.laa.gpfd.services.MappingTableService;
import uk.gov.laa.gpfd.services.ReportService;
import uk.gov.laa.gpfd.services.ReportTrackingTableService;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("testauth")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OAuth2LoginTest extends TestAuthConfig {

    @MockitoBean
    AzureGraphClient mockAzureGraphClient;

    @MockitoBean
    MappingTableService mappingTableServiceMock;

    @MockitoBean
    ReportService reportServiceMock;

    @MockitoBean
    ReportTrackingTableService reportTrackingTableService;

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithAnonymousUser
    void shouldRedirectToLoginWhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/reports"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnResponseWhenUserIsAuthenticatedWithAdminRole() throws Exception {
        // Given
        var mock = ReportListEntryTestDataFactory.aValidReportsGet200ResponseReportListInner();
        when(mappingTableServiceMock.fetchReportListEntries()).thenReturn(singletonList(mock));

        // Then
        mockMvc.perform(MockMvcRequestBuilders.get("/reports"))
                .andExpect(status().isOk());
    }

}
