package uk.gov.laa.gpfd.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.laa.gpfd.data.ReportListEntryTestDataFactory;
import uk.gov.laa.gpfd.services.ReportManagementService;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("testauth")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = uk.gov.laa.gpfd.config.TestDatabaseConfig .class)
class OAuth2LoginTest {

    @MockitoBean
    ReportManagementService reportManagementServiceMock;

    @MockitoBean
    ReportManagementService reportServiceMock;

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldRedirectToLoginWhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/reports"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void shouldReturnResponseWhenUserIsAuthenticatedWithAdminRole() throws Exception {
        var mock = ReportListEntryTestDataFactory.aValidReportsGet200ResponseReportListInner();
        when(reportManagementServiceMock.fetchReportListEntries()).thenReturn(singletonList(mock));

        mockMvc.perform(
                        get("/reports")
                                .with(oidcLogin()
                                        .idToken(token -> token.claim("LAA_APP_ROLES", List.of("REP000")))
                                )
                )
                .andExpect(status().isOk());
    }


}
