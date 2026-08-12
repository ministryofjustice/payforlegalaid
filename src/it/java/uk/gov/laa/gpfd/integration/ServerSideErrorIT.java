package uk.gov.laa.gpfd.integration;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServerSideErrorIT extends BaseIT {

    @Test
    void getReportsShouldNotDependOnMojfinGpfdMetadataTables() throws Exception {
        MvcResult result = performGetRequestWithRoles("/reports", List.of("Financial"))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus(),
                "Expected 200 but got " + result.getResponse().getStatus()
                        + ": " + result.getResponse().getContentAsString());
    }

    @Test
    void getReportByIdShouldNotDependOnMojfinGpfdMetadataTables() throws Exception {
        MvcResult result = performGetRequestWithRoles(
                        "/reports/b36f9bbb-1178-432c-8f99-8090e285f2d3",
                        List.of("Financial"))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus(),
                "Expected 200 but got " + result.getResponse().getStatus()
                        + ": " + result.getResponse().getContentAsString());
    }

}
