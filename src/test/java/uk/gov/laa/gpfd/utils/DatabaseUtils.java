package uk.gov.laa.gpfd.utils;

import java.sql.Connection;
import javax.sql.DataSource;
import liquibase.CatalogAndSchema;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import uk.gov.laa.gpfd.exception.DatabaseReadException;

@Component
@RequiredArgsConstructor
public class DatabaseUtils {
  protected final JdbcTemplate writeJdbcTemplate;

  private final DataSource writeDataSource;

  private static Liquibase liquibase;
  private static Connection connection;

  public void setUpDatabase() {
    try {
      connection = writeDataSource.getConnection();
      Database database = new liquibase.database.core.H2Database(); // or OracleDatabase, PostgresDatabase, etc.
      database.setConnection(new JdbcConnection(connection));

      applyLiquibaseXml("db.changelog-gpfd-schema.xml", database);
      applyLiquibaseXml("db.changelog-gpfd-reports-schema.xml", database);
      applyLiquibaseXml("db.changelog-gpfd-data.xml", database);
      applyLiquibaseXml("db.changelog-gpfd-reports-data.xml", database);
      applyLiquibaseXml("db.changelog-any-report-schema.xml", database);
      applyLiquibaseXml("db.changelog-any-report-data.xml", database);
    } catch (Exception e) {
      throw new DatabaseReadException("Exception when setting up test database:" + e.getMessage());
    }

  }

  private static void applyLiquibaseXml(String changeLogFile, Database database)
      throws LiquibaseException {
    liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);
    liquibase.update("test");
  }

  public void cleanUpDatabase() {
    try {
      if (liquibase != null) {
        CatalogAndSchema[] schemas = new CatalogAndSchema[] {
            new CatalogAndSchema(null, "GPFD"),
            new CatalogAndSchema(null, "GPFD_REPORTS"),
            new CatalogAndSchema(null, "ANY_REPORT")
        };

        liquibase.dropAll(schemas);
      }
    } catch (Exception e) {
      throw new DatabaseReadException("Exception when cleaning up test database:" + e.getMessage());
    }
  }

}
