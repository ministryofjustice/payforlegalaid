package uk.gov.laa.gpfd.services.excel.template;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import uk.gov.laa.gpfd.exception.TemplateResourceException;

import java.io.InputStream;
import java.util.UUID;

public record S3TemplateClient() implements TemplateClient {

    @Override
    public InputStream findTemplateById(UUID id) {

        System.out.println("CCCCC - Using S3 One");

        var filename = getFileNameFromId(id);

        if (filename == null) {
            return null;
        }

        S3Client s3 = S3Client.builder()
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .region(Region.of("eu-west-2"))
                .build();

        var getObjectRequest = GetObjectRequest.builder()
                .bucket("laa-get-payments-finance-data-dev-file-store")
                .key(filename)
                .build();

        var file = s3.getObject(getObjectRequest);

        System.out.println(file);

        // todo - use s3 client

        return null;
    }

}
