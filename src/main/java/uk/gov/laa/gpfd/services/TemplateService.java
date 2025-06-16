package uk.gov.laa.gpfd.services;

import org.apache.poi.ss.usermodel.Workbook;
import uk.gov.laa.gpfd.exception.TemplateResourceException;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.model.excel.ExcelTemplate;
import uk.gov.laa.gpfd.services.excel.template.TemplateClient;
import uk.gov.laa.gpfd.services.excel.workbook.ReportWorkbook;
import uk.gov.laa.gpfd.services.excel.workbook.StyleManager;
import uk.gov.laa.gpfd.utils.SecurityPolicy;
import uk.gov.laa.gpfd.utils.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static uk.gov.laa.gpfd.exception.TemplateResourceException.ExcelTemplateCreationException;

/**
 * The interface is a functional interface designed to retrieve and load Excel templates
 * as Apache POI {@link Workbook} objects. It provides methods to locate a template by its unique identifier
 * and load it into a {@link Workbook} for further processing.
 */
public sealed interface TemplateService permits TemplateService.ExcelTemplateService {

    /**
     * Retrieves an Excel template as a {@link Workbook} based on the provided unique identifier.
     * This method is responsible for locating and loading the template into a format suitable for
     * further processing.
     *
     * @param template the template to retrieve
     * @return the {@link Workbook} representing the template
     */
    Workbook findTemplateById(ExcelTemplate template);

    /**
     * Creates a new empty {@link Workbook} for the specified report. The created workbook
     * will serve as a foundation for report generation
     *
     * @param report the report for which to create an empty workbook
     * @return a new empty {@link Workbook} instance
     */
    Workbook createEmpty(Report report);

    record ExcelTemplateService(TemplateClient repository, WorkbookFactory streamingFactory, WorkbookFactory factory, StyleManager styleManager) implements TemplateService {

        /**
         * Retrieves an Excel template as a {@link Workbook} using the provided {@link TemplateClient} and unique identifier.
         * This default method uses the {@link TemplateClient} to fetch the template as an {@link InputStream}, then loads it
         * into a {@link Workbook} using custom factory {@link WorkbookFactory}. If an error occurs during loading, a
         * {@link ExcelTemplateCreationException} is thrown with a descriptive message.
         *
         * @param template the template to retrieve
         * @return the {@link Workbook} representing the template
         * @throws ExcelTemplateCreationException if the template cannot be loaded
         */
        @Override
        public Workbook findTemplateById(ExcelTemplate template) {
            try (var input = repository.findTemplateById(template.getId())) {
                return factory.create(input);
            } catch (IOException e) {
                throw new TemplateResourceException.ExcelTemplateCreationException("Failed to load template for ID: " + template, e);
            }
        }

        @Override
        public Workbook createEmpty(Report report) {
            return new ReportWorkbook(report, styleManager);
        }

        public static final class Builder  {
            private interface Defaults {
                SecurityPolicy<InputStream> SECURITY = SecurityPolicy.zipBombProtection(1.0E-04);
                boolean STREAMING_ENABLED = false;
                int STREAMING_WINDOW_SIZE = 100;
            }
            private TemplateClient repository;
            private WorkbookFactory factory;
            private StyleManager styleManager;
            private SecurityPolicy<InputStream> security = Defaults.SECURITY;
            private boolean streamingEnabled = Defaults.STREAMING_ENABLED;
            private int streamingWindowSize = Defaults.STREAMING_WINDOW_SIZE;

            public Builder repository(TemplateClient repository) {
                this.repository = repository;
                return this;
            }

            public Builder factory(WorkbookFactory factory) {
                this.factory = factory;
                return this;
            }

            public Builder withSecurity(double ratio) {
                this.security = SecurityPolicy.zipBombProtection(ratio);
                return this;
            }

            public Builder withStyleManager(StyleManager styleManager) {
                this.styleManager = styleManager;
                return this;
            }

            public Builder withAdditionalTransformation(SecurityPolicy<InputStream> transformer) {
                this.security = this.security.compose(transformer);
                return this;
            }

            public Builder withStream(int windowSize) {
                this.streamingEnabled = true;
                this.streamingWindowSize = windowSize;
                return this;
            }

            public ExcelTemplateService build() {
                Objects.requireNonNull(repository, "Repository must be provided");
                Objects.requireNonNull(factory, "Factory must be provided");

                var configuredFactory = streamingEnabled ?
                    factory.asStreamed(streamingWindowSize).withTransformation(security) : factory.withTransformation(security);

                return new ExcelTemplateService(repository, configuredFactory, factory.withTransformation(security), styleManager);
            }
        }
    }
}