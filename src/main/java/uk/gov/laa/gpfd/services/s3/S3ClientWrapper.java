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

import java.util.Comparator;
import java.util.Optional;

/**
 * Class that wraps around the default {@link S3Client}, allowing us to set default behaviours
 */
@Slf4j
public class S3ClientWrapper {

    public static final String TEMPLATE_FOLDER = "templates";
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
    public ResponseInputStream<GetObjectResponse> getTemplate(String filename) {
        log.info("Attempting to fetch {}/{} from S3 bucket", TEMPLATE_FOLDER, filename);
        var req = GetObjectRequest.builder()
                .bucket(s3Bucket)
                .key(TEMPLATE_FOLDER + "/" + filename)
                .build();
        return s3Client.getObject(req);
    }

    /**
     * Used to return the stream and key back to the caller
     * The key is needed to name the file correctly when the user downloads it
     *
     * @param key - file name in S3 with the path
     * @param stream - file stream
     */
    public record S3CsvDownload(String key, ResponseInputStream<GetObjectResponse> stream) implements AutoCloseable {

        @Override
        public void close() throws Exception {
            stream.close();
        }

        /**
         * Create the file name for the download. It strips out the path from the key and just leaves the file name.
         * e.g. reports/daily/report_000_2025-12-12.csv -> report_000_2025-12-12.csv
         *
         * @return file name to use for the downloaded file
         */
        public String getFileName() {
            return key.substring(key.lastIndexOf('/') + 1);
        }
    }

    /**
     * Fetches the latest version of a given report file from the S3 bucket.
     * It will look for anything matching the prefix (which is the path + start of the filename, e.g. for REP000 it might be
     * "reports/monthly/report_000") and pick the one with the most recent Last Modified date
     * If there is an error, a {@link AwsServiceException} can be thrown. This will be caught by the {@link GlobalExceptionHandler}
     *
     * @param filePrefix - path + start of the filename
     * @return Stream of the file
     */
    public Optional<S3CsvDownload> getResultCsv(String filePrefix) {

        log.info("Getting list of all files matching {}", filePrefix);
        var listReq = ListObjectsV2Request.builder()
                .bucket(s3Bucket)
                .prefix(filePrefix)
                .build();

        var listRes = s3Client.listObjectsV2(listReq);
        if (listRes.contents().isEmpty()) {
            log.error("No file matching prefix {} found", filePrefix);
            return Optional.empty();
        }

        var sortedList = listRes.contents().stream().filter(obj -> obj.key().endsWith(".csv"))
                .sorted(Comparator.comparing(S3Object::lastModified).reversed());
        var latestFile = sortedList.findFirst();

        return latestFile.flatMap(first -> {
            log.info("Attempting to download file with key {}, last modified {}", latestFile.get().key(), latestFile.get().lastModified());

            var req = GetObjectRequest.builder()
                    .bucket(s3Bucket)
                    .key(latestFile.get().key())
                    .build();

            return Optional.of(new S3CsvDownload(latestFile.get().key(), s3Client.getObject(req)));
        });

    }

}
