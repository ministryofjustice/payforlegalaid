package uk.gov.laa.gpfd.config;

import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.web.client.RestTemplate;

import uk.gov.laa.gpfd.dao.ReportTrackingDao;
import uk.gov.laa.gpfd.model.FileExtension;
import uk.gov.laa.gpfd.model.excel.ExcelMappingProjection;
import uk.gov.laa.gpfd.services.StreamingService;
import uk.gov.laa.gpfd.services.TemplateService;
import uk.gov.laa.gpfd.services.excel.editor.CellValueSetter;
import uk.gov.laa.gpfd.services.excel.editor.PivotTableRefresher;
import uk.gov.laa.gpfd.services.excel.formatting.BoldStyleFormatting;
import uk.gov.laa.gpfd.services.excel.formatting.CellFormatter;
import uk.gov.laa.gpfd.services.excel.formatting.CellFormatting;
import uk.gov.laa.gpfd.services.excel.formatting.ColumnFormatting;
import uk.gov.laa.gpfd.services.excel.formatting.Formatting;
import uk.gov.laa.gpfd.services.excel.template.TemplateClient;
import uk.gov.laa.gpfd.services.excel.template.TemplateFileNameResolver;
import uk.gov.laa.gpfd.services.excel.workbook.StyleManager;
import uk.gov.laa.gpfd.services.stream.DataStream;
import uk.gov.laa.gpfd.utils.StrategyFactory;
import uk.gov.laa.gpfd.utils.WorkbookFactory;

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
    @Value("${excel.security.compression-ratio:0.001}")
    private double allowedCompressionRatio;

    @Value("${excel.steam.window.size:1000}")
    private int rowAccessWindowSize;

    /**
     * Creates Datasource for the Postgres RDS which has tracking data in.
     * Can rename if we port more functionality over to RDS rather than MOJFIN.
     *
     * @return Data Source that talks to the associated Postgres DB
     */
    @Bean
    @ConfigurationProperties(prefix = "gpfd.datasource.tracking")
    DataSource trackingDataSource() {
        return DataSourceBuilder.create()
                .build();
    }

    /**
     * Exposes the existing tracking RDS connection for metadata reads.
     *
     * <p>Keeping this as its own bean allows test configurations to replace the
     * metadata connection explicitly, while production continues to use the same RDS.</p>
     *
     * @param trackingDataSource the tracking RDS data source
     * @return the data source used for report metadata
     */
    @Bean
    @ConditionalOnMissingBean(name = "metadataDataSource")
    DataSource metadataDataSource(@Qualifier("trackingDataSource") DataSource trackingDataSource) {
        return trackingDataSource;
    }

    /**
     * Allows JDBC operations on the "trackingDataSource" above.
     *
     * @param dataSource - data source for Postgres RDS DB used for tracking
     * @return JDBC template that lets us perform operations on the tracking DB.
     */
    @Bean
    JdbcTemplate trackingJdbcTemplate(@Qualifier("trackingDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * Provides a JDBC client for reading report metadata from the tracking RDS database.
     *
     * <p>Supports both positional and named-parameter queries through a single API.</p>
     *
     * @param dataSource the tracking RDS data source containing the metadata tables
     * @return a JdbcClient for report metadata access
     */
    @Bean
    @ConditionalOnMissingBean(name = "metadataClient")
    JdbcClient metadataClient(@Qualifier("metadataDataSource") DataSource dataSource) {
        return JdbcClient.create(dataSource);
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
     * <p>
     * Although it appears to have no usages in this repo, this is needed by the acceptance tests currently.
     * </p>
     *
     * @return a configured {@link RestTemplate} instance with custom message converters.
     */
    @Bean
    RestTemplate restTemplate() {
        var restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(
                List.of(
                        new StringHttpMessageConverter(),
                        new ByteArrayHttpMessageConverter()
                )
        );

        return restTemplate;
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

    @Bean
    public RequestContextFilter customRequestContextFilter() {
        return new RequestContextFilter();
    }

    @Bean
    public FilterRegistrationBean<RequestContextFilter> requestContextFilterRegistration() {
        var registration = new FilterRegistrationBean<>(customRequestContextFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns("/*");
        registration.setName("laaRequestContextFilter");
        return registration;
    }

    /**
     * Creates a {@link TemplateFileNameResolver} for use in the app.
     *
     * @return a file name resolver instance.
     */
    @Bean
    public TemplateFileNameResolver createFileNameResolver() {
        return new TemplateFileNameResolver();
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

    @Bean
    StreamingService streamingService(StrategyFactory<FileExtension, DataStream> streamStrategyFactory) {
        return new StreamingService.DefaultStreamingService(streamStrategyFactory.getStrategies());
    }

    @Bean
    public StrategyFactory<FileExtension, DataStream> streamStrategyFactory(Collection<DataStream> strategies) {
        return StrategyFactory.createGenericStrategyFactory(strategies, DataStream::getFormat);
    }

    /**
     * Sets up the report tracking data access to use the Postgres tracking db rather than the MOJFIN ones
     *
     * @param trackingJdbcTemplate tracking table JDBC template
     * @return report tracking data access object
     */
    @Bean
    ReportTrackingDao reportTrackingDao(@Qualifier("trackingJdbcTemplate") JdbcOperations trackingJdbcTemplate) {
        return new ReportTrackingDao(trackingJdbcTemplate);
    }
}
