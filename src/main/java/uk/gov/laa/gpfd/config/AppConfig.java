package uk.gov.laa.gpfd.config;

import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.web.client.RestTemplate;
import uk.gov.laa.gpfd.dao.ReportDao;
import uk.gov.laa.gpfd.dao.stream.AbstractStreamingDao;
import uk.gov.laa.gpfd.dao.stream.StreamingDao;
import uk.gov.laa.gpfd.dao.support.ResultSetToMapMapper;
import uk.gov.laa.gpfd.model.FileExtension;
import uk.gov.laa.gpfd.model.FieldAttributes;
import uk.gov.laa.gpfd.services.DataStreamer;
import uk.gov.laa.gpfd.services.StreamingService;
import uk.gov.laa.gpfd.services.TemplateService;
import uk.gov.laa.gpfd.services.excel.editor.CellValueSetter;
import uk.gov.laa.gpfd.services.excel.editor.FormulaCalculator;
import uk.gov.laa.gpfd.services.excel.editor.PivotTableRefresher;
import uk.gov.laa.gpfd.services.excel.editor.SheetDataWriter;
import uk.gov.laa.gpfd.services.excel.formatting.CellFormatter;
import uk.gov.laa.gpfd.services.excel.formatting.CellFormatting;
import uk.gov.laa.gpfd.services.excel.formatting.ColumnFormatting;
import uk.gov.laa.gpfd.services.excel.formatting.Formatting;
import uk.gov.laa.gpfd.services.excel.template.LocalTemplateClient;
import uk.gov.laa.gpfd.services.excel.template.TemplateClient;
import uk.gov.laa.gpfd.services.stream.AbstractDataStream;
import uk.gov.laa.gpfd.services.stream.DataStream;
import uk.gov.laa.gpfd.utils.StrategyFactory;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

    @Value("${excel.steam.window.size:100}")
    private int rowAccessWindowSize;

    @Getter
    @Value("${spring.cloud.azure.active-directory.credential.client-id}")
    private String entraIdClientId;

    @Getter
    @Value("${spring.cloud.azure.active-directory.profile.tenant-id}")
    private String entraIdTenantId;

    /**
     * Configures a read-only {@link DataSource} using properties prefixed with
     * "gpfd.datasource.read-only" in the application's configuration file.
     * <p>
     * This data source is intended for read-only operations in the database, such as queries.
     * </p>
     *
     * @return a configured {@link DataSource} for read-only operations.
     */
    @Bean
    @ConfigurationProperties(prefix = "gpfd.datasource.read-only")
    DataSource readOnlyDataSource() {
        return new DriverManagerDataSource();
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
        return new JdbcTemplate(dataSource);
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
    public TemplateService templateService(TemplateClient templateClient) {
        if (allowedCompressionRatio <= 0) {
            throw new IllegalStateException("Compression ratio must be positive");
        }

        return new TemplateService.ExcelTemplateService.Builder()
                .repository(templateClient)
                .factory(XSSFWorkbook::new)
                .withSecurity(allowedCompressionRatio)
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
        return new CellValueSetter() {};
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
            public void applyFormatting(Sheet sheet, Cell cell, FieldAttributes fieldAttribute) {
                applyFormatting(strategies,sheet,cell,fieldAttribute);
            }
        };
    }

    /**
     * Creates a {@link SheetDataWriter} bean for writing data to Excel sheets. This bean uses
     * the provided {@link CellValueSetter} and {@link CellFormatter} to set cell values and apply formatting.
     *
     * @param cellValueSetterSupplier the {@link CellValueSetter} used to set cell values
     * @param cellFormatter the {@link CellFormatter} used to apply cell formatting
     * @return a {@link SheetDataWriter} instance
     */
    @Bean
    public SheetDataWriter sheetDataWriter(CellValueSetter cellValueSetterSupplier, CellFormatter cellFormatter) {
        return new SheetDataWriter() {
            @Override
            public void writeDataToSheet(Sheet sheet, Stream<Map<String, Object>> data, Collection<FieldAttributes> fieldAttributes) {
                writeDataToSheet(cellValueSetterSupplier, cellFormatter, sheet, data, fieldAttributes);
            }
        };
    }

    /**
     * Creates a {@link CellFormatting} bean for applying cell-level formatting strategies.
     *
     * @return a {@link CellFormatting} instance
     */
    @Bean
    public CellFormatting cellFormattingStrategy() {
        return new CellFormatting() {};
    }

    /**
     * Creates a {@link ColumnFormatting} bean for applying column-level formatting strategies,
     * such as setting column widths.
     *
     * @return a {@link ColumnFormatting} instance
     */
    @Bean
    public ColumnFormatting columnWidthStrategy() {
        return new ColumnFormatting() {};
    }

    /**
     * Creates and returns a {@link PivotTableRefresher} bean. This bean is responsible for
     * refreshing pivot tables in Excel workbooks.
     *
     * @return a {@link PivotTableRefresher} instance
     */
    @Bean
    public PivotTableRefresher pivotTableRefresher() {
        return new PivotTableRefresher() {};
    }

    /**
     * Creates and returns a {@link FormulaCalculator} bean. This bean is responsible for
     * evaluate formula cells in Excel workbooks.
     *
     * @return a {@link FormulaCalculator} instance
     */
    @Bean
    public FormulaCalculator formulaCalculator() {
        return new FormulaCalculator() {};
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
                                     StreamingDao<Map<String, Object>> dataFetcher,
                                     SheetDataWriter sheetDataWriter,
                                     PivotTableRefresher pivotTableRefresher,
                                     FormulaCalculator formulaCalculator) {
        return DataStreamer.createExcelStreamer(templateLoader, dataFetcher, sheetDataWriter, pivotTableRefresher, formulaCalculator);
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

    @Bean
    public RowMapper<Map<String, Object>> resultSetToMapMapper() {
        return new ResultSetToMapMapper();
    }

    @Bean
    public StreamingDao<Map<String, Object>> mapStreamingDao(
            JdbcOperations readOnlyJdbcTemplate,
            RowMapper<Map<String, Object>> resultSetToMapMapper) {
        return new AbstractStreamingDao<>(readOnlyJdbcTemplate, resultSetToMapMapper) {};
    }

}