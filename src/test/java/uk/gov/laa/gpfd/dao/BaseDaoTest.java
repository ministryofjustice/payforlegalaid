package uk.gov.laa.gpfd.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.gov.laa.gpfd.utils.DatabaseUtils;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseDaoTest {

  @Autowired
  private DatabaseUtils databaseUtils;

  @Autowired
  protected JdbcTemplate writeJdbcTemplate;

  @BeforeEach
  void setup() {
    databaseUtils.setUpDatabase();
  }

  @AfterEach
  void resetDatabase() {
    databaseUtils.cleanUpDatabase();
  }

}