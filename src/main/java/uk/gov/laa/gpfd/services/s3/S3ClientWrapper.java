package uk.gov.laa.gpfd.services.s3;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;
import uk.gov.laa.gpfd.controller.GlobalExceptionHandler;

import java.util.ArrayList;
import java.util.Comparator;

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

    public record S3CsvDownload(String key, ResponseInputStream<GetObjectResponse> stream) implements AutoCloseable {

        @Override
        public void close() throws Exception {
            stream.close();
        }

        public String getFileName() {
            return key.substring(key.lastIndexOf('/') + 1);
        }
    }

    /**
     * Fetches the current version of a given report file from the S3 bucket.
     * If there is an error, a {@link AwsServiceException} can be thrown. This will be caught by the {@link GlobalExceptionHandler}
     *
     * @param filename - report file name
     * @return Stream of the file
     */
    public S3CsvDownload getResultCsv(String filename, String folder, String prefix) {

        log.info("Prefix is {}", prefix);
        var listReq = ListObjectsV2Request.builder()
                .bucket(s3Bucket)
                .prefix(prefix)
                .build();

        var listRes = s3Client.listObjectsV2(listReq);

        // List in AWS response is not modifiable
        var sortedList = new ArrayList<>(listRes.contents());
        sortedList.sort(Comparator.comparing(S3Object::lastModified).reversed());

        sortedList.forEach(obj -> log.info("Found in s3 {} with lastmodified {}", obj.key(), obj.lastModified()));

        var item = sortedList.stream().findFirst();

        log.info("Have chosen file {}", item.get().key());

        var req = GetObjectRequest.builder()
                .bucket(s3Bucket)
                .key(item.get().key())
                .build();

        return new S3CsvDownload(item.get().key(), s3Client.getObject(req));
    }

    private GetObjectRequest buildRequest(String folder, String filename){
        log.info("Attempting to fetch {}/{} from S3 bucket", folder, filename);
        return GetObjectRequest.builder()
                .bucket(s3Bucket)
                .key(folder + "/" + filename)
                .build();
    }
}
