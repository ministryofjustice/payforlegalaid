package uk.gov.laa.gpfd.integration;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.laa.gpfd.utils.DatabaseUtils;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIT {

  @Autowired
  private DatabaseUtils databaseUtils;

  @Autowired
  MockMvc mockMvc;

  public static final String REPORT_UUID_1 = "0d4da9ec-b0b3-4371-af10-f375330d85d3";

  @BeforeAll
  void setUpDatabase() {
    databaseUtils.setUpDatabase();
  }

  @AfterAll
  void cleanUpDatabase() {
    databaseUtils.cleanUpDatabase();
  }

  @NotNull
  protected ResultActions performGetRequest(String uriTemplate) throws Exception {
    return mockMvc.perform(
      MockMvcRequestBuilders.get(uriTemplate)
      .contentType(MediaType.APPLICATION_JSON)
    );
  }
}