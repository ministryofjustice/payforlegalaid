package uk.gov.laa.gpfd.services;

import org.apache.poi.ss.usermodel.Workbook;
import uk.gov.laa.gpfd.exception.TemplateResourceException;
import uk.gov.laa.gpfd.services.excel.template.TemplateClient;
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
     * @param id the unique identifier of the template to retrieve
     * @return the {@link Workbook} representing the template
     */
    Workbook findTemplateById(String id);

    record ExcelTemplateService(TemplateClient repository, WorkbookFactory factory) implements TemplateService {

        /**
         * Retrieves an Excel template as a {@link Workbook} using the provided {@link TemplateClient} and unique identifier.
         * This default method uses the {@link TemplateClient} to fetch the template as an {@link InputStream}, then loads it
         * into a {@link Workbook} using custom factory {@link WorkbookFactory}. If an error occurs during loading, a
         * {@link ExcelTemplateCreationException} is thrown with a descriptive message.
         *
         * @param id the unique identifier of the template to retrieve
         * @return the {@link Workbook} representing the template
         * @throws ExcelTemplateCreationException if the template cannot be loaded
         */
        @Override
        public Workbook findTemplateById(String id) {
            try (var input = repository.findTemplateById(id)) {
                return factory.create(input);
            } catch (IOException e) {
                throw new TemplateResourceException.ExcelTemplateCreationException("Failed to load template for ID: " + id, e);
            }
        }

        public static final class Builder {
            private TemplateClient repository;
            private WorkbookFactory factory;
            private SecurityPolicy<InputStream> security = SecurityPolicy.zipBombProtection(1.0E-04);

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

            public Builder withAdditionalTransformation(SecurityPolicy<InputStream> transformer) {
                this.security = this.security.compose(transformer);
                return this;
            }

            public ExcelTemplateService build() {
                Objects.requireNonNull(repository, "Repository must be provided");
                Objects.requireNonNull(factory, "Factory must be provided");

                var securedFactory = factory.withTransformation(security);
                return new ExcelTemplateService(repository, securedFactory);
            }
        }
    }
}