package uk.gov.laa.gpfd.services;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;

public class S3ClientWrapper {

    private final S3Client s3Client;
    private final String s3Bucket;

    public S3ClientWrapper(String awsRegion, String s3Bucket) {
        this.s3Client = S3Client.builder()
                .region(Region.of(awsRegion))
                .build();
        this.s3Bucket = s3Bucket;
    }

    public InputStream getTemplate(String filename){

        var getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Bucket)
                .key("templates/" + filename)
                .build();

        return s3Client.getObject(getObjectRequest);

    }
}
