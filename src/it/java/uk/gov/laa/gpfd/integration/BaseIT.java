package uk.gov.laa.gpfd.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.laa.gpfd.utils.DatabaseUtils;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIT {

  @Autowired
  private DatabaseUtils databaseUtils;

  @BeforeAll
  void setUpDatabase() throws Exception {
    databaseUtils.setUpDatabase();
  }

  @AfterAll
  void cleanUpDatabase() throws Exception {
    databaseUtils.cleanUpDatabase();
  }
}