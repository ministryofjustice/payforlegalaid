package uk.gov.laa.gpfd.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import uk.gov.laa.gpfd.integration.config.TestDatabaseConfig;
import uk.gov.laa.gpfd.config.TestSecurityConfig;
import uk.gov.laa.gpfd.utils.DatabaseUtils;

import java.util.List;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = {TestDatabaseConfig.class, TestSecurityConfig.class})
public abstract class BaseIT {

    @Autowired
    private DatabaseUtils databaseUtils;

    @Autowired
    MockMvc mockMvc;

    /*
        Can't fully take advantage of things like ServiceConnection here because we are connecting to multiple data sources currently
     */
    @Container
    static final PostgreSQLContainer trackingDb =
            new PostgreSQLContainer("postgres:18");

    @DynamicPropertySource
    static void overrideTracking(DynamicPropertyRegistry r) {
        trackingDb.start();
        r.add("gpfd.datasource.tracking.jdbcUrl", trackingDb::getJdbcUrl);
        r.add("gpfd.datasource.tracking.username", trackingDb::getUsername);
        r.add("gpfd.datasource.tracking.password", trackingDb::getPassword);

        r.add("spring.flyway.enabled", () -> true);
        r.add("spring.flyway.url", trackingDb::getJdbcUrl);
        r.add("spring.flyway.user", trackingDb::getUsername);
        r.add("spring.flyway.password", trackingDb::getPassword);

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

}