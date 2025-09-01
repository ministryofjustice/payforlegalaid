package uk.gov.laa.gpfd.services.s3;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public class S3ClientWrapper {

    private final S3Client s3Client;
    private final String s3Bucket;

    public S3ClientWrapper(String awsRegion, String s3Bucket) {
        this.s3Client = new S3ClientFactory().createS3Client(awsRegion);
        this.s3Bucket = s3Bucket;
    }

    public S3ClientWrapper(S3Client s3Client, String s3Bucket) {
        this.s3Client = s3Client;
        this.s3Bucket = s3Bucket;
    }

    public ResponseInputStream<GetObjectResponse> getTemplate(String filename){

        var getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Bucket)
                .key("templates/" + filename)
                .build();

        return s3Client.getObject(getObjectRequest);

    }
}
