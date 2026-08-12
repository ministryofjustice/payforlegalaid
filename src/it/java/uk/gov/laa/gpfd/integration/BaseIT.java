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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.laa.gpfd.integration.config.SharedTrackingPostgres;
import uk.gov.laa.gpfd.integration.config.TestDatabaseConfig;
import uk.gov.laa.gpfd.utils.DatabaseUtils;

import java.util.List;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;

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
    static void overrideTracking(DynamicPropertyRegistry r) {
        r.add("gpfd.datasource.tracking.jdbcUrl", SharedTrackingPostgres.CONTAINER::getJdbcUrl);
        r.add("gpfd.datasource.tracking.username", SharedTrackingPostgres.CONTAINER::getUsername);
        r.add("gpfd.datasource.tracking.password", SharedTrackingPostgres.CONTAINER::getPassword);
        r.add("gpfd.datasource.tracking.driver-class-name", () -> "org.postgresql.Driver");
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

        if (!result.getRequest().isAsyncStarted()) {
            return new CompletedResultActions(result);
        }

        var asyncResult = result.getAsyncResult();
        if (asyncResult instanceof Throwable throwable) {
            throw new IllegalStateException("Async streaming request failed", throwable);
        }

        return mockMvc.perform(asyncDispatch(result));
    }

    private record CompletedResultActions(MvcResult result) implements ResultActions {

        @Override
        public ResultActions andExpect(ResultMatcher matcher) throws Exception {
            matcher.match(result);
            return this;
        }

        @Override
        public ResultActions andDo(ResultHandler handler) throws Exception {
            handler.handle(result);
            return this;
        }

        @Override
        public MvcResult andReturn() {
            return result;
        }
    }

}