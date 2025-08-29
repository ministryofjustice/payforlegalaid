package uk.gov.laa.gpfd.services.excel.template;

import lombok.SneakyThrows;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import uk.gov.laa.gpfd.exception.TemplateResourceException.TemplateResourceNotFoundException;
import uk.gov.laa.gpfd.services.S3ClientWrapper;

import java.io.InputStream;
import java.util.UUID;

public record S3TemplateClient(S3ClientWrapper s3Client) implements TemplateClient {

    @Override
    @SneakyThrows
    public InputStream findTemplateById(UUID id) {

        var filename = getFileNameFromId(id);

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
