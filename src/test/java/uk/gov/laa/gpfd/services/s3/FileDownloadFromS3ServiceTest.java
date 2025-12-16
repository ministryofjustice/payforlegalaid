package uk.gov.laa.gpfd.services.s3;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import uk.gov.laa.gpfd.exception.FileDownloadException.InvalidDownloadFormatException;
import uk.gov.laa.gpfd.exception.ReportAccessException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileDownloadFromS3ServiceTest {

    @Mock
    private S3ClientWrapper s3ClientWrapper;

    @Mock
    private ReportFileNameResolver fileNameResolver;

    @Mock
    private ReportAccessCheckerService reportAccessCheckerService;

    @InjectMocks
    private FileDownloadFromS3Service fileDownloadFromS3Service;

    private final UUID testUUID = UUID.randomUUID();
    private final String testFilename = "report_numero_uno.csv";
    private final List<String> groupList = List.of("34fdsfh324-fdsfsdaf324-ds", "asjd324jnfdsf", "hdscv2343rvf");

    @BeforeEach
    void beforeEach() {
        reset(fileNameResolver, s3ClientWrapper, reportAccessCheckerService);
    }

    @SneakyThrows
    @Test
    void shouldReturnFileStreamWrappedInResponseWithAllHeaders() {

        var responseMetadata = GetObjectResponse.builder().contentLength(25L).build();
        var inputStream = new ByteArrayInputStream("csv,data,here,123,4.3,cat".getBytes());
        var mockS3Response = new ResponseInputStream<>(responseMetadata, inputStream);

        when(fileNameResolver.getFileNameFromId(testUUID)).thenReturn(testFilename);
        when(fileNameResolver.getFolderFromId(testUUID)).thenReturn("daily");
        when(fileNameResolver.getPrefixFromId(testUUID)).thenReturn("");
        when(s3ClientWrapper.getResultCsv("")).thenReturn(Optional.of(new S3ClientWrapper.S3CsvDownload("reports/daily/report_numero_uno.csv", mockS3Response)));
        when(reportAccessCheckerService.checkUserCanAccessReport(testUUID)).thenReturn(true);

        var result = fileDownloadFromS3Service.getFileStreamResponse(testUUID);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        var headers = result.getHeaders();

        assertEquals(MediaType.APPLICATION_OCTET_STREAM, headers.getContentType());
        assertEquals(25L, headers.getContentLength());

        var contentDisposition = headers.getContentDisposition();
        assertTrue(contentDisposition.isAttachment());
        assertEquals(testFilename, contentDisposition.getFilename());

        var content = new BufferedReader(new InputStreamReader(result.getBody().getInputStream()))
                .lines()
                .collect(Collectors.joining());
        assertEquals("csv,data,here,123,4.3,cat", content);

        verify(reportAccessCheckerService).checkUserCanAccessReport(testUUID);
        verify(fileNameResolver).getFileNameFromId(testUUID);
        verify(s3ClientWrapper).getResultCsv("");

    }

    @Test
    void shouldThrowExceptionIfUserLacksPermissionToAccessReport() {
        when(reportAccessCheckerService.checkUserCanAccessReport(testUUID)).thenThrow(new ReportAccessException(testUUID));

        assertThrows(ReportAccessException.class, () -> fileDownloadFromS3Service.getFileStreamResponse(testUUID));

        verify(reportAccessCheckerService).checkUserCanAccessReport(testUUID);
        verifyNoInteractions(fileNameResolver);
        verifyNoInteractions(s3ClientWrapper);
    }

    @Test
    void shouldErrorIfFileFormatIsIncorrect() {
        when(reportAccessCheckerService.checkUserCanAccessReport(testUUID)).thenReturn(true);
        when(fileNameResolver.getFileNameFromId(testUUID)).thenReturn("report_numero_uno.xlsx");

        assertThrows(InvalidDownloadFormatException.class, () -> fileDownloadFromS3Service.getFileStreamResponse(testUUID));

        verify(fileNameResolver).getFileNameFromId(testUUID);
        verifyNoInteractions(s3ClientWrapper);
    }

    @Test
    void shouldLetExceptionHandlerHandleExceptionThrownByFileNameResolver() {
        when(reportAccessCheckerService.checkUserCanAccessReport(testUUID)).thenReturn(true);
        when(fileNameResolver.getFileNameFromId(testUUID)).thenThrow(new IllegalArgumentException("Report ID cannot be null or blank"));

        var exception = assertThrows(IllegalArgumentException.class, () -> fileDownloadFromS3Service.getFileStreamResponse(testUUID));
        assertEquals("Report ID cannot be null or blank", exception.getMessage());

        verifyNoInteractions(s3ClientWrapper);
    }

    @Test
    void shouldLetExceptionHandlerHandleExceptionThrownByS3Client() {
        when(reportAccessCheckerService.checkUserCanAccessReport(testUUID)).thenReturn(true);
        when(fileNameResolver.getFileNameFromId(testUUID)).thenReturn(testFilename);
        when(fileNameResolver.getFolderFromId(testUUID)).thenReturn("folder");
        when(fileNameResolver.getPrefixFromId(testUUID)).thenReturn("prefix");
        when(s3ClientWrapper.getResultCsv("prefix")).thenThrow(NoSuchKeyException.builder().build());

        assertThrows(NoSuchKeyException.class, () -> fileDownloadFromS3Service.getFileStreamResponse(testUUID));
    }

}