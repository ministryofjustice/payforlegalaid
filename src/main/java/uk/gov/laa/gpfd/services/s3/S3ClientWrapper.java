package uk.gov.laa.gpfd.services.s3;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import uk.gov.laa.gpfd.controller.GlobalExceptionHandler;

import java.time.Duration;

/**
 * Class that wraps around the default {@link S3Client}, allowing us to set default behaviours
 */
public class S3ClientWrapper {

    private final S3Client s3Client;
    private final String s3Bucket;
    private final S3Presigner s3Presigner;

    public S3ClientWrapper(String awsRegion, String s3Bucket) {
        this.s3Client = new S3ClientFactory().createS3Client(awsRegion);
        this.s3Bucket = s3Bucket;
        this.s3Presigner = S3Presigner.create();
    }

    public S3ClientWrapper(S3Client s3Client, String s3Bucket, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Bucket = s3Bucket;
        this.s3Presigner = s3Presigner;
    }

    /**
     * Fetches the current version of a given template from the S3 bucket.
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

    public String getPresignedUrl(){
        var getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Bucket)
                .key("templates/C12_LATE_PROCESSED_BILLS_CIS_TEMPLATE.xlsx")
                .build();

        var getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        var response = s3Presigner.presignGetObject(getObjectPresignRequest);
        System.out.println("Presigned URL: " + response.url().toString());
        System.out.println("HTTP method: " + response.httpRequest().method());

        return response.url().toExternalForm();

    }
}
