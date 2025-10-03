package uk.gov.laa.gpfd.services.s3;

import org.junit.jupiter.api.Test;
import uk.gov.laa.gpfd.exception.OperationNotSupportedException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FileDownloadLocalServiceTest {

    private final FileDownloadLocalService fileDownloadLocalService = new FileDownloadLocalService();

    @Test
    void shouldThrowUnsupportedException(){
        var testUUID = UUID.randomUUID();
        var exception = assertThrows(OperationNotSupportedException.class, () -> fileDownloadLocalService.getFileStreamResponse(testUUID));
        assertEquals("Operation /reports/" + testUUID + "/file is not supported on this instance", exception.getMessage());

    }

}