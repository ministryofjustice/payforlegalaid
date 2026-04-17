package uk.gov.laa.gpfd.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.laa.gpfd.utils.DatabaseUtils;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIT {

    @Autowired
    private DatabaseUtils databaseUtils;

    @Autowired
    MockMvc mockMvc;

    @Container
    static final PostgreSQLContainer trackingDb =
            new PostgreSQLContainer("postgres:16");

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
    void setUpDatabase() {
        databaseUtils.setUpDatabase();
    }

    @AfterAll
    void cleanUpDatabase() {
        databaseUtils.cleanUpDatabase();
    }

    protected ResultActions performGetRequest(String uriTemplate) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.get(uriTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }

    protected ResultActions performGetRequest(String uriTemplate, SecurityContext securityContext) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.get(uriTemplate)
                        .with(securityContext(securityContext))
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