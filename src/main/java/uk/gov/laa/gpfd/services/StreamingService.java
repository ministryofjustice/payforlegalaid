package uk.gov.laa.gpfd.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.exception.TransferException;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@Slf4j
@Service
public record StreamingService(ExcelService excelService) {

    private static final String APPLICATION_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    /**
     * Streams an Excel file as a response for the given UUID.
     * <p>
     * This method generates an Excel file using the provided UUID, streams it directly to the client,
     * and sets the appropriate headers for file download. The method ensures efficient streaming
     * by avoiding loading the entire file into memory.
     * </p>
     *
     * @param uuid The unique identifier for the report or data to be streamed as an Excel file.
     * @return A {@link ResponseEntity} containing a {@link StreamingResponseBody} for the Excel file.
     * @throws TransferException.StreamException.ExcelStreamWriteException If an error occurs while streaming the Excel data.
     */
    public ResponseEntity<StreamingResponseBody> streamExcel(UUID uuid) {
        log.info("Starting Excel streaming process for UUID: {}", uuid);
        log.debug("Response content type set to: {}", APPLICATION_OCTET_STREAM_VALUE);

        var excel = excelService.createExcel(uuid);
        var filename = "%s.%s".formatted(excel.getLeft().getName(), "xlsx");
        var contentDisposition = "attachment; filename=%s".formatted(filename);
        log.debug("Set Content-Disposition header: {}", contentDisposition);

        StreamingResponseBody responseBody = outputStream -> {
            try(var workbook = excel.getRight()) {
                log.debug("Starting to stream Excel file for UUID: {}", uuid);
                workbook.write(outputStream);
                log.debug("Successfully streamed Excel file for UUID: {}", uuid);
            } catch (IOException e) {
                log.error("Error streaming Excel data to response for UUID: {}", uuid, e);
                throw new TransferException.StreamException.ExcelStreamWriteException("Error streaming Excel data to response", e);
            }
        };

        log.debug("Building response for Excel file streaming for UUID: {}", uuid);
        return ResponseEntity.ok()
                .header("Content-Disposition", contentDisposition)
                .contentType(MediaType.parseMediaType(APPLICATION_EXCEL))
                .body(responseBody);
    }

}