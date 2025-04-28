package uk.gov.laa.gpfd.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Client;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.laa.gpfd.config.TestAuthConfig;
import uk.gov.laa.gpfd.utils.DatabaseUtils;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIT extends TestAuthConfig {

  @Autowired
  private DatabaseUtils databaseUtils;

  @Autowired
  MockMvc mockMvc;

  @BeforeAll
  void setUpDatabase() {
    databaseUtils.setUpDatabase();
  }

  @AfterAll
  void cleanUpDatabase() {
    databaseUtils.cleanUpDatabase();
  }

  @NotNull
  protected MockHttpServletResponse getResponseForAuthenticatedRequest(String uriTemplate) throws Exception {
      return mockMvc.perform(
          MockMvcRequestBuilders.get(uriTemplate)
              .with(SecurityMockMvcRequestPostProcessors.oauth2Login())
              .with(oidcLogin()
                  .idToken(token -> token.subject("mockUser")
                      .claim("preferred_username", "mockUsername")))
              .with(oauth2Client("graph"))).andReturn().getResponse();
  }
}