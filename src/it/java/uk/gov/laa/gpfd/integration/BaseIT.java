package uk.gov.laa.gpfd.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.laa.gpfd.utils.DatabaseUtils;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIT {

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
}