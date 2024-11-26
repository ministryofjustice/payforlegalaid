package uk.gov.laa.gpfd.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.laa.gpfd.data.ReportListEntryTestDataFactory;
import uk.gov.laa.gpfd.graph.AzureGraphClient;
import uk.gov.laa.gpfd.service.MappingTableService;
import uk.gov.laa.gpfd.service.ReportService;
import uk.gov.laa.gpfd.service.ReportTrackingTableService;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OAuth2LoginTest {

    @MockBean
    AzureGraphClient mockAzureGraphClient;

    @MockBean
    MappingTableService mappingTableServiceMock;

    @MockBean
    ReportService reportServiceMock;

    @MockBean
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

        // When
        // Then
        mockMvc.perform(MockMvcRequestBuilders.get("/reports"))
                .andExpect(status().isOk());
    }

}
