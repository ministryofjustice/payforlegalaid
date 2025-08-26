package uk.gov.laa.gpfd.config;

import lombok.Getter;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.web.client.RestTemplate;
import uk.gov.laa.gpfd.dao.JdbcWorkbookDataStreamer;
import uk.gov.laa.gpfd.dao.ReportDao;
import uk.gov.laa.gpfd.dao.sql.core.FetchSizePolicy;
import uk.gov.laa.gpfd.dao.sql.core.ForwardOnlyReadOnlyPolicy;
import uk.gov.laa.gpfd.dao.sql.core.QueryTimeoutPolicy;
import uk.gov.laa.gpfd.dao.sql.core.StatementConfigurationPolicy;
import uk.gov.laa.gpfd.dao.sql.core.StatementCreationPolicy;
import uk.gov.laa.gpfd.dao.sql.core.StatementPolicy;
import uk.gov.laa.gpfd.dao.sql.core.StatementPolicyBuilder;
import uk.gov.laa.gpfd.model.FieldProjection;
import uk.gov.laa.gpfd.model.FileExtension;
import uk.gov.laa.gpfd.model.Mapping;
import uk.gov.laa.gpfd.model.excel.ExcelMappingProjection;
import uk.gov.laa.gpfd.services.DataStreamer;
import uk.gov.laa.gpfd.services.StreamingService;
import uk.gov.laa.gpfd.services.TemplateService;
import uk.gov.laa.gpfd.services.excel.editor.CellValueSetter;
import uk.gov.laa.gpfd.services.excel.editor.PivotTableRefresher;
import uk.gov.laa.gpfd.services.excel.formatting.BoldStyleFormatting;
import uk.gov.laa.gpfd.services.excel.formatting.CellFormatter;
import uk.gov.laa.gpfd.services.excel.formatting.CellFormatting;
import uk.gov.laa.gpfd.services.excel.formatting.ColumnFormatting;
import uk.gov.laa.gpfd.services.excel.formatting.Formatting;
import uk.gov.laa.gpfd.services.excel.template.LocalTemplateClient;
import uk.gov.laa.gpfd.services.excel.template.TemplateClient;
import uk.gov.laa.gpfd.services.excel.workbook.StyleManager;
import uk.gov.laa.gpfd.services.stream.AbstractDataStream;
import uk.gov.laa.gpfd.services.stream.DataStream;
import uk.gov.laa.gpfd.utils.StrategyFactory;
import uk.gov.laa.gpfd.utils.WorkbookFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static uk.gov.laa.gpfd.dao.sql.ChannelRowHandler.forSheet;
import static uk.gov.laa.gpfd.services.DataStreamer.createJdbcStreamer;

/**
 * Configuration class for application-level beans and settings.
 * <p>
 * This class defines various beans such as data sources, JDBC templates,
 * model mapper, and a RestTemplate with custom message converters. These configurations
 * are essential for database connectivity, object mapping, and external API integration.
 * </p>
 */
@Configuration
public class AppConfig {

    @Getter
    @Value("${gpfd.url}")
    private String serviceUrl;

    @Value("${excel.security.compression-ratio:0.001}")
    private double allowedCompressionRatio;

    @Value("${excel.steam.window.size:1000}")
    private int rowAccessWindowSize;

    @Value("${excel.jdbc.streamer.default-fetch-size:1000}")
    private int defaultFetchSize;

    @Getter
    @Value("${spring.cloud.azure.active-directory.credential.client-id}")
    private String entraIdClientId;

    @Getter
    @Value("${spring.cloud.azure.active-directory.profile.tenant-id}")
    private String entraIdTenantId;

    /**
     * Configures a read-only {@link DataSource}.
     * <p>
     * This data source is intended for read-only operations in the database, such as queries.
     * </p>
     *
     * @return a configured {@link DataSource} for read-only operations.
     */
    @Bean
    public DataSource readOnlyDataSource(
            @Value("${gpfd.datasource.read-only.url}") String url,
            @Value("${gpfd.datasource.read-only.username}") String username,
            @Value("${gpfd.datasource.read-only.password}") String password,
            @Value("${gpfd.datasource.read-only.driver-class-name}") String driverClass
    ) throws SQLException {
        PoolDataSource pds = PoolDataSourceFactory.getPoolDataSource();

        pds.setConnectionFactoryClassName(driverClass);
        pds.setURL(url);
        pds.setUser(username);
        pds.setPassword(password);

        pds.setInitialPoolSize(5);
        pds.setMinPoolSize(5);
        pds.setMaxPoolSize(10);
        pds.setCommitOnConnectionReturn(false);
        pds.setConnectionWaitDuration(Duration.of(30, ChronoUnit.SECONDS));
        pds.setTimeoutCheckInterval(5);
        pds.setInactiveConnectionTimeout(60);
        pds.setAbandonedConnectionTimeout(120);
        pds.setConnectionHarvestTriggerCount(3);
        pds.setConnectionHarvestMaxCount(5);

        pds.setValidateConnectionOnBorrow(false);
        pds.setConnectionProperty("oracle.jdbc.defaultRowPrefetch", "1000");
        pds.setConnectionProperty("oracle.jdbc.useFetchSizeWithLongColumn", "true");
        pds.setConnectionProperty("oracle.jdbc.JdbcConnectionFlags", "0x8000");
        pds.setConnectionProperty("oracle.net.CONNECT_TIMEOUT", "10000");
        pds.setConnectionProperty("oracle.jdbc.ReadTimeout", "30000");

        return pds;
    }

    @Bean
    public StatementCreationPolicy defaultCreationPolicy() {
        return new ForwardOnlyReadOnlyPolicy();
    }

    @Bean
    public StatementConfigurationPolicy fetchSizePolicy(
            @Value("${jdbc.fetch-size:1000}") int fetchSize) {
        return new FetchSizePolicy(fetchSize);
    }

    @Bean
    public StatementConfigurationPolicy queryTimeoutPolicy(
            @Value("${jdbc.query-timeout:30}") int timeout) {
        return new QueryTimeoutPolicy(timeout);
    }

    @Bean
    public StatementPolicy statementPolicy(
            StatementCreationPolicy creationPolicy,
            List<StatementConfigurationPolicy> configurationPolicies
    ) {
        var builder = new StatementPolicyBuilder()
                .withCreationPolicy(creationPolicy);

        configurationPolicies.forEach(builder::addConfigurationPolicy);

        return builder.build();
    }

    @Bean
    public JdbcWorkbookDataStreamer workbookDataStreamer(
            JdbcTemplate readOnlyJdbcTemplate,
            StatementPolicy statementPolicy
    ) {
        return new JdbcWorkbookDataStreamer(readOnlyJdbcTemplate) {

            @Override
            protected String getSql(Mapping mapping) {
                return mapping.getQuery().value();
            }

            @Override
            protected PreparedStatementCreator createStatementCreator(String sql) {
                return statementPolicy.createStatementCreator(sql);
            }

            @Override
            protected RowCallbackHandler createRowCallbackHandler(Sheet sheet, Mapping mapping) {
                var list = mapping.getExcelSheet().getFieldAttributes().stream()
                        .filter(Objects::nonNull)
                        .map(FieldProjection.class::cast)
                        .toList();

                return forSheet(sheet, list);
            }
        };
    }

    /**
     * Configures a write-enabled {@link DataSource} using properties prefixed with
     * "gpfd.datasource.write" in the application's configuration file.
     * <p>
     * This data source is intended for write operations in the database, such as inserts and updates.
     * </p>
     *
     * @return a configured {@link DataSource} for write operations.
     */
    @Bean
    @ConfigurationProperties(prefix = "gpfd.datasource.write")
    DataSource writeDataSource() {
        return new DriverManagerDataSource();
    }

    /**
     * Configures a {@link JdbcTemplate} for read-only database operations.
     * <p>
     * The {@code JdbcTemplate} is built on the {@code readOnlyDataSource} and simplifies
     * querying and interacting with the database in a read-only capacity.
     * </p>
     *
     * @param dataSource the read-only {@link DataSource} to be used by the {@link JdbcTemplate}.
     * @return a configured {@link JdbcTemplate} for read-only operations.
     */
    @Bean
    JdbcTemplate readOnlyJdbcTemplate(@Qualifier("readOnlyDataSource") DataSource dataSource) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.setFetchSize(defaultFetchSize);
        template.setMaxRows(0);
        template.setQueryTimeout(0);
        return template;
    }

    /**
     * Configures a {@link JdbcTemplate} for write-enabled database operations.
     * <p>
     * The {@code JdbcTemplate} is built on the {@code writeDataSource} and simplifies
     * executing updates and inserts into the database.
     * </p>
     *
     * @param dataSource the write-enabled {@link DataSource} to be used by the {@link JdbcTemplate}.
     * @return a configured {@link JdbcTemplate} for write-enabled operations.
     */
    @Bean
    JdbcTemplate writeJdbcTemplate(@Qualifier("writeDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * Configures a {@link RestTemplate} bean for making REST API calls.
     * <p>
     * The {@code RestTemplate} is configured with a custom list of message converters:
     * <ul>
     *     <li>{@link StringHttpMessageConverter}: Converts HTTP messages to and from strings.</li>
     *     <li>{@link ByteArrayHttpMessageConverter}: Converts HTTP messages to and from byte arrays.</li>
     * </ul>
     * These converters enable the application to handle various content types when interacting
     * with external APIs.
     * </p>
     *
     * @return a configured {@link RestTemplate} instance with custom message converters.
     */
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate() {{
            setMessageConverters(List.of(
                    new StringHttpMessageConverter(),
                    new ByteArrayHttpMessageConverter()
            ));
        }};
    }

    /**
     * Creates an {@link AuthorizationManager} bean to allow customization of the Authorization flow.
     * This allows the Authorization flow to be tailed to specific run profile, as needed
     *
     * @return An instance of the AuthorizationManager configured for the payforlegalaid service
     */
    @Bean
    public AuthorizationManager<RequestAuthorizationContext> authManager() {
        return new ContextBasedAuthorizationManager();
    }

    /**
     * Creates a {@link TemplateClient} which returns local template.
     *
     * @return a {@link LocalTemplateClient} instance
     */
    @Bean
    public TemplateClient localTemplateClient() {
        return new LocalTemplateClient();
    }

    /**
     * Creates a {@link TemplateService} bean with security policies that delegates to the provided {@link TemplateClient}
     * for loading Excel templates.
     *
     * @param templateClient the {@link TemplateClient} used to retrieve templates
     * @return a {@link TemplateService} instance
     */
    @Bean
    public TemplateService streamingTemplateService(TemplateClient templateClient, StyleManager styleManager) {
        if (allowedCompressionRatio <= 0) {
            throw new IllegalStateException("Compression ratio must be positive");
        }

        return new TemplateService.ExcelTemplateService.Builder()
                .repository(templateClient)
                .factory(WorkbookFactory::newWorkbook)
                .withSecurity(allowedCompressionRatio)
                .withStyleManager(styleManager)
                .withStream(rowAccessWindowSize)
                .build();
    }

    /**
     * Creates a {@link CellValueSetter} bean for setting cell values in Excel sheets.
     *
     * @return a {@link CellValueSetter} instance
     */
    @Bean
    public CellValueSetter cellValueSetterSupplier() {
        return new CellValueSetter() {
        };
    }

    @Bean
    public Formatting boldStyleFormatting() {
        return new BoldStyleFormatting() {
        };
    }

    /**
     * Creates a {@link CellFormatter} bean that applies formatting strategies to cells in Excel sheets.
     *
     * @param strategies a collection of {@link Formatting} strategies to apply
     * @return a {@link CellFormatter} instance
     */
    @Bean
    public CellFormatter cellFormatter(Collection<Formatting> strategies) {
        return new CellFormatter() {
            @Override
            public void applyFormatting(Sheet sheet, Cell cell, ExcelMappingProjection fieldAttribute) {
                applyFormatting(strategies, sheet, cell, fieldAttribute);
            }
        };
    }

    /**
     * Creates and configures a {@link StyleManager} bean for managing Excel cell styles.
     *
     * <p>This bean provides a centralized way to create, cache, and reuse cell styles
     * throughout the application to ensure consistent formatting in generated Excel files
     * while optimizing memory usage.</p>
     */
    @Bean
    public StyleManager styleManager() {
        return StyleManager.create();
    }

    /**
     * Creates a {@link CellFormatting} bean for applying cell-level formatting strategies.
     *
     * @return a {@link CellFormatting} instance
     */
    @Bean
    public CellFormatting cellFormattingStrategy(StyleManager styleManager) {
        return new CellFormatting(styleManager) {
        };
    }

    /**
     * Creates a {@link ColumnFormatting} bean for applying column-level formatting strategies,
     * such as setting column widths.
     *
     * @return a {@link ColumnFormatting} instance
     */
    @Bean
    public ColumnFormatting columnWidthStrategy() {
        return new ColumnFormatting() {
        };
    }

    /**
     * Creates and returns a {@link PivotTableRefresher} bean. This bean is responsible for
     * refreshing pivot tables in Excel workbooks.
     *
     * @return a {@link PivotTableRefresher} instance
     */
    @Bean
    public PivotTableRefresher pivotTableRefresher() {
        return new PivotTableRefresher() {
        };
    }

    /**
     * Creates and returns a {@link DataStreamer} bean. This bean is responsible for
     * streaming csv files.
     *
     * @return a {@link DataStreamer} instance
     */
    @Bean
    DataStreamer dataStreamer(JdbcTemplate readOnlyJdbcTemplate) {
        return createJdbcStreamer(readOnlyJdbcTemplate);
    }

    @Bean
    DataStreamer createExcelStreamer(TemplateService templateLoader,
                                     JdbcWorkbookDataStreamer dataFetcher,
                                     CellFormatter formatter) {
        return DataStreamer.createExcelStreamer(templateLoader, dataFetcher, formatter);
    }

    @Bean
    DataStream createCsvStreamStrategy(ReportDao reportDao, DataStreamer dataStreamer) {
        return AbstractDataStream.createCsvStreamStrategy(reportDao, dataStreamer);
    }

    @Bean
    DataStream createExcelStreamStrategy(ReportDao reportDao, DataStreamer createExcelStreamer) {
        return AbstractDataStream.createExcelStreamStrategy(reportDao, createExcelStreamer);
    }

    @Bean
    StreamingService streamingService(StrategyFactory<FileExtension, DataStream> streamStrategyFactory) {
        return new StreamingService.DefaultStreamingService(streamStrategyFactory.getStrategies());
    }

    @Bean
    public StrategyFactory<FileExtension, DataStream> streamStrategyFactory(Collection<DataStream> strategies) {
        return StrategyFactory.createGenericStrategyFactory(strategies, DataStream::getFormat);
    }


}
