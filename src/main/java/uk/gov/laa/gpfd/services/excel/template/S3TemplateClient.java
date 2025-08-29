package uk.gov.laa.gpfd.services.excel.template;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import uk.gov.laa.gpfd.exception.TemplateResourceException;

import java.io.InputStream;
import java.util.UUID;

public record S3TemplateClient(S3Client s3Client) implements TemplateClient {

    @Override
    public InputStream findTemplateById(UUID id) {

        System.out.println("CCCCC - Using S3 One");

        var filename = getFileNameFromId(id);

        if (filename == null) {
            return null;
        }

        // TODO check various parameters on the aws docs
        // TODO use params instead of hardcoded
        // TODO use shared S3 client
        // TODO null checks etc

        var getObjectRequest = GetObjectRequest.builder()
                .bucket("laa-get-payments-finance-data-dev-file-store")
                .key("templates/" + filename)
                .build();

        var fileAsStream = s3Client.getObject(getObjectRequest);

        //TODO check null etc

        return fileAsStream;
    }

}
