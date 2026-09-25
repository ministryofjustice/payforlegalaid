package uk.gov.laa.gpfd.integration;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static uk.gov.laa.gpfd.security.SilasRoles.FINANCIAL;

class ServerSideErrorIT extends BaseIT {

    @Test
    void getReportsShouldNotDependOnMojfinGpfdMetadataTables() throws Exception {
        MvcResult result = performGetRequestWithRoles("/reports", List.of(FINANCIAL))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus(),
                "Expected 200 but got " + result.getResponse().getStatus()
                        + ": " + result.getResponse().getContentAsString());
    }

    @Test
    void getNonExistentReportByIdShouldBeForbidden() throws Exception {
        MvcResult result = performGetRequestWithRoles(
                        "/reports/00000000-0000-0000-0000-000000000999",
                        List.of(FINANCIAL))
                .andReturn();

        assertEquals(403, result.getResponse().getStatus(),
                "Expected 403 but got " + result.getResponse().getStatus()
                        + ": " + result.getResponse().getContentAsString());
    }

}
