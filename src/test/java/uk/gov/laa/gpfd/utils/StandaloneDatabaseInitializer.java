package uk.gov.laa.gpfd.utils;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Standalone database initializer for Docker builds.
 * This class initializes the H2 database with the required schema and test data.
 */
public class StandaloneDatabaseInitializer {

    public static void main(String[] args) {
        System.out.println("🔧 Starting database initialization...");
        
        String dbPath = args.length > 0 ? args[0] : "target/docker-db-init/gpfd";
        String dbUrl = "jdbc:h2:file:" + dbPath + ";DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE;MODE=Oracle";
        
        try {
            StandaloneDatabaseInitializer initializer = new StandaloneDatabaseInitializer();
            initializer.initializeDatabase(dbUrl);
            System.out.println("✅ Database initialization completed successfully!");
            System.out.println("Database URL: " + dbUrl);
        } catch (Exception e) {
            System.err.println("❌ Database initialization failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public void initializeDatabase(String dbUrl) throws Exception {
        DataSource dataSource = createDataSource(dbUrl);
        
        // Apply all changelog files in the correct order, each with its own connection
        applyLiquibaseXml("db.changelog-gpfd-schema.xml", dataSource);
        applyLiquibaseXml("db.changelog-gpfd-reports-schema.xml", dataSource);
        applyLiquibaseXml("db.changelog-gpfd-data.xml", dataSource);
        applyLiquibaseXml("db.changelog-gpfd-reports-data.xml", dataSource);
        applyLiquibaseXml("db.changelog-any-report-schema.xml", dataSource);
        applyLiquibaseXml("db.changelog-any-report-data.xml", dataSource);
    }
    
    private DataSource createDataSource(String dbUrl) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setDriverClassName("org.h2.Driver");
        return dataSource;
    }
    
    private void applyLiquibaseXml(String changeLogFile, DataSource dataSource) throws Exception {
        System.out.println("Applying changelog: " + changeLogFile);
        try (Connection connection = dataSource.getConnection()) {
            Database database = new liquibase.database.core.H2Database();
            database.setConnection(new JdbcConnection(connection));
            
            try (Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database)) {
                liquibase.update("docker-init");
            }
        }
    }
}