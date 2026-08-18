package uk.gov.laa.gpfd.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.laa.gpfd.integration.config.TestDatabaseConfig;
import uk.gov.laa.gpfd.utils.DatabaseUtils;
import uk.gov.laa.pfla.configuration.TrackingDbSetup;

import java.util.List;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("testauth")
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = {TestDatabaseConfig.class})
public abstract class BaseIT {

    @Autowired
    private DatabaseUtils databaseUtils;

    @Autowired
    MockMvc mockMvc;

    @DynamicPropertySource
    static void overrideTracking(DynamicPropertyRegistry registry) {
        registry.add("gpfd.datasource.tracking.jdbcUrl", TrackingDbSetup.POSTGRES::getJdbcUrl);
        registry.add("gpfd.datasource.tracking.username", TrackingDbSetup.POSTGRES::getUsername);
        registry.add("gpfd.datasource.tracking.password", TrackingDbSetup.POSTGRES::getPassword);
    }

    @BeforeAll
    void setUpMojfinDatabase() {
        databaseUtils.setUpMockMojfinDatabase();
    }

    @AfterAll
    void cleanUpMojfinDatabase() {
        databaseUtils.cleanUpMockMojfinDatabase();
    }

    protected ResultActions performGetRequest(String uriTemplate) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.get(uriTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    protected ResultActions performGetRequestWithRoles(String uri, List<String> roles) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.get(uri)
                        .with(oidcLogin()
                                .idToken(token -> token.claim("LAA_APP_ROLES", roles)
                                        .claim("oid", UUID.randomUUID().toString())))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    protected ResultActions performStreamingGetRequestWithRoles(String uri, List<String> roles) throws Exception {
        var result = performGetRequestWithRoles(uri, roles)
                .andReturn();

        var asyncResult = result.getAsyncResult();
        if (asyncResult instanceof Throwable throwable) {
            throw new IllegalStateException("Async streaming request failed", throwable);
        }

        return mockMvc.perform(asyncDispatch(result));
    }

}
