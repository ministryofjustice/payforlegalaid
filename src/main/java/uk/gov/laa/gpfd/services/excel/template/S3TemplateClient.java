package uk.gov.laa.gpfd.services.excel.template;

import lombok.SneakyThrows;
import uk.gov.laa.gpfd.exception.TemplateResourceException.TemplateResourceNotFoundException;
import uk.gov.laa.gpfd.services.s3.S3ClientWrapper;

import java.io.InputStream;
import java.util.UUID;

/**
 * An implementation of {@link TemplateClient} that provides template resources
 * from an S3 bucket.
 */
public record S3TemplateClient(S3ClientWrapper s3Client, TemplateFileNameResolver templateFileNameResolver) implements TemplateClient {

    @Override
    @SneakyThrows
    public InputStream findTemplateById(UUID id) {

        var filename = templateFileNameResolver.getFileNameFromId(id);

        if (filename == null) {
            return null;
        }

        var fileAsStream = s3Client.getTemplate(filename);

        if (fileAsStream == null) {
            throw new TemplateResourceNotFoundException("Template '%s' not found in file store for ID: %s".formatted(filename, id));
        }

        return fileAsStream;
    }

}
