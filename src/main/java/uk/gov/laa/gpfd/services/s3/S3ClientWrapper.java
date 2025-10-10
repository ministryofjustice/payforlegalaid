package uk.gov.laa.gpfd.services.s3;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import uk.gov.laa.gpfd.controller.GlobalExceptionHandler;

/**
 * Class that wraps around the default {@link S3Client}, allowing us to set default behaviours
 */
@Slf4j
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
     * Fetches the current version of a given template from the S3 bucket.
     * If there is an error, a {@link AwsServiceException} can be thrown. This will be caught by the {@link GlobalExceptionHandler}
     *
     * @param filename - template file name
     * @return Stream of the file
     */
    public ResponseInputStream<GetObjectResponse> getTemplate(String filename){
        return s3Client.getObject(buildRequest("templates", filename));
    }

    /**
     * Fetches the current version of a given report file from the S3 bucket.
     * If there is an error, a {@link AwsServiceException} can be thrown. This will be caught by the {@link GlobalExceptionHandler}
     *
     * @param filename - report file name
     * @return Stream of the file
     */
    public ResponseInputStream<GetObjectResponse> getResultCsv(String filename) {
        return s3Client.getObject(buildRequest("reports", filename));
    }

    private GetObjectRequest buildRequest(String folder, String filename){
        log.info("Attempting to fetch {}/{} from S3 bucket", folder, filename);
        return GetObjectRequest.builder()
                .bucket(s3Bucket)
                .key(folder + "/" + filename)
                .build();
    }
}
