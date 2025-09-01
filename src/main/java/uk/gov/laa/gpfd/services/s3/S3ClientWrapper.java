package uk.gov.laa.gpfd.services.s3;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import uk.gov.laa.gpfd.controller.GlobalExceptionHandler;

/**
 * Class that wraps around the default {@link S3Client}, allowing us to set default behaviours
 */
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

    /**
     * Fetches a given template from the S3 bucket.
     * If there is an error, a {@link AwsServiceException} can be thrown. This will be caught by the {@link GlobalExceptionHandler}
     *
     * @param filename - template file name
     * @return Stream of the file
     */
    public ResponseInputStream<GetObjectResponse> getTemplate(String filename){

        var getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Bucket)
                .key("templates/" + filename)
                .build();

        return s3Client.getObject(getObjectRequest);

    }
}
