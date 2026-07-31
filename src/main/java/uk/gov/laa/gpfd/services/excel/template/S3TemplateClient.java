package uk.gov.laa.gpfd.services.excel.template;

import lombok.SneakyThrows;
import software.amazon.awssdk.core.sync.RequestBody;
import uk.gov.laa.gpfd.exception.TemplateResourceException.TemplateResourceNotFoundException;
import uk.gov.laa.gpfd.services.s3.S3ClientWrapper;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

        s3Client.s3Client.putObject(
                software.amazon.awssdk.services.s3.model.PutObjectRequest.builder()
                        .bucket(s3Client.s3Bucket)
                        .key("chris-s3-test.txt")
                        .build(),
                RequestBody.fromBytes("S3 test object".getBytes(StandardCharsets.UTF_8))
        );

        System.out.println("File uploaded to S3 bucket: " + s3Client.s3Bucket + "/chris-s3-test.txt");

        return fileAsStream;
    }

}
