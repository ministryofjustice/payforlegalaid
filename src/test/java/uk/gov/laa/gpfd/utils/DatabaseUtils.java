package uk.gov.laa.gpfd.utils;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import liquibase.CatalogAndSchema;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

@Component
public class DatabaseUtils {

  private final DataSource dataSource;

  public DatabaseUtils(@Qualifier("readOnlyDataSource") DataSource dataSource) {
    this.dataSource = dataSource;
  }

  private static Liquibase liquibase;
  private static Connection connection;

  public void setUpMockMojfinDatabase() {
    try {
      connection = dataSource.getConnection();
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
