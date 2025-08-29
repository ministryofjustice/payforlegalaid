package uk.gov.laa.gpfd.services.excel.template;

import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.UUID;

import static uk.gov.laa.gpfd.exception.TemplateResourceException.TemplateNotFoundException;
import static uk.gov.laa.gpfd.exception.TemplateResourceException.TemplateResourceNotFoundException;

/**
 * A local implementation of {@link TemplateClient} that provides template resources
 * from the classpath. This client loads templates during initialization and serves
 * them from memory.
 * <p>
 * Templates are identified by UUID strings and mapped to Excel file resources packaged
 * with the application.
 */
public record LocalTemplateClient() implements TemplateClient {

    @Override
    @SneakyThrows
    public InputStream findTemplateById(UUID id) {

        var filename = getFileNameFromId(id);

        if (filename == null) {
            return null;
        }

        var resourceAsStream = getClass().getClassLoader().getResourceAsStream(filename);

        if (resourceAsStream == null) {
            throw new TemplateResourceNotFoundException("Template file '%s' not found in resources for ID: %s".formatted(filename, id));
        }

        return resourceAsStream;
    }

}
