package uk.gov.laa.gpfd.utils;

import liquibase.CatalogAndSchema;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
@RequiredArgsConstructor
public class DatabaseUtils {

  private final DataSource writeDataSource;

  private static Liquibase liquibase;
  private static Connection connection;

  public void setUpMockMojfinDatabase() {
    try {
      connection = writeDataSource.getConnection();
      Database database = new liquibase.database.core.H2Database(); // or OracleDatabase, PostgresDatabase, etc.
      database.setConnection(new JdbcConnection(connection));

      applyLiquibaseXml("db.changelog-gpfd-reports-schema.xml", database);
      applyLiquibaseXml("db.changelog-gpfd-reports-data.xml", database);
      applyLiquibaseXml("db.changelog-any-report-schema.xml", database);
      applyLiquibaseXml("db.changelog-any-report-data.xml", database);
      applyLiquibaseXml("db.changelog-gpfd-schema-rbac.xml", database);
    } catch (Exception e) {
      throw new RuntimeException("Exception when setting up test database:" + e.getMessage());
    }

  }

  /**
   * Seeds only the ANY_REPORT finance fixtures used by acceptance tests.
   *
   * <p>Metadata now lives in the tracking Postgres database, so the GPFD metadata changelogs
   * must not be applied to the H2 MoJFin test database when prod Liquibase has already run.</p>
   */
  public void setUpMockAnyReportDatabase() {
    try {
      connection = writeDataSource.getConnection();
      Database database = new liquibase.database.core.H2Database();
      database.setConnection(new JdbcConnection(connection));

      applyLiquibaseXml("db.changelog-any-report-schema.xml", database);
      applyLiquibaseXml("db.changelog-any-report-data.xml", database);
    } catch (Exception e) {
      throw new RuntimeException("Exception when setting up ANY_REPORT test database:" + e.getMessage());
    }
  }

  private static void applyLiquibaseXml(String changeLogFile, Database database)
      throws LiquibaseException {
    liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database);
    liquibase.update("test");
  }

  public void cleanUpMockMojfinDatabase() {
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
      throw new RuntimeException("Exception when cleaning up test database:" + e.getMessage());
    }
  }

}
