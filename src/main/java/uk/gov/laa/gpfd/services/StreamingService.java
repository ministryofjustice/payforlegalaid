package uk.gov.laa.gpfd.services;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import java.io.OutputStream;
import java.util.UUID;

import static jakarta.servlet.http.HttpServletResponse.SC_REQUEST_TIMEOUT;
import static java.util.concurrent.CompletableFuture.runAsync;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@Slf4j
@Service
public record StreamingService(ExcelService excelService) {

    /**
     * Initiates the streaming of an Excel file to the client. This method sets up a {@link DeferredResult}
     * to handle the asynchronous generation and streaming of the file. It also configures the response
     * headers and handles timeouts and errors.
     *
     * @param response the {@link HttpServletResponse} to which the Excel file will be streamed
     * @param uuid     the unique identifier for the report to be generated
     * @return a {@link DeferredResult} representing the asynchronous result of the streaming operation
     */
    public DeferredResult<StreamingResponseBody> streamExcel(HttpServletResponse response, UUID uuid) {
        log.info("Starting Excel streaming process for UUID: {}", uuid);
        final DeferredResult<StreamingResponseBody> deferredResult = new DeferredResult<>(60000L);
        response.setContentType(APPLICATION_OCTET_STREAM_VALUE);
        log.debug("Response content type set to: {}", APPLICATION_OCTET_STREAM_VALUE);

        runAsync(() -> processExcel(response, uuid, deferredResult))
                .exceptionally((Throwable ex) -> {
                    log.error("An error occurred while processing Excel for UUID: {}", uuid, ex);
                    deferredResult.setErrorResult(ex);
                    return null;
                });

        deferredResult.onTimeout(() -> {
            log.warn("Request timed out for UUID: {}", uuid);
            handleTimeout(response, deferredResult);
        });
        log.info("DeferredResult created successfully for UUID: {}", uuid);
        return deferredResult;
    }

    /**
     * Processes the Excel file generation and streaming. This method is executed asynchronously and
     * sets the result or error on the {@link DeferredResult}.
     *
     * @param response      the {@link HttpServletResponse} to which the Excel file will be streamed
     * @param uuid          the unique identifier for the report to be generated
     * @param deferredResult the {@link DeferredResult} to which the result or error will be set
     */
    private void processExcel(HttpServletResponse response, UUID uuid, DeferredResult<StreamingResponseBody> deferredResult) {
        try {
            deferredResult.setResult(writeExcelToStream(response, uuid));
        } catch (Exception e) {
            deferredResult.setErrorResult(e);
        }
    }

    private StreamingResponseBody writeExcelToStream(HttpServletResponse response, UUID uuid) {
        return (OutputStream outputStream) -> {
            try {
                log.debug("Creating Excel file for UUID: {}", uuid);
                var excel = excelService.createExcel(uuid);

                var report = excel.getLeft();
                log.debug("Retrieved report: {}", report.getName());
                String contentDisposition = "attachment; filename=%s.%s".formatted(report.getName(), "xlsx");
                response.setHeader("Content-Disposition", contentDisposition);
                log.debug("Set Content-Disposition header: {}", contentDisposition);

                log.debug("Writing Excel file to output stream for UUID: {}", uuid);
                excel.getRight().write(outputStream);

                log.debug("Flushing output stream for UUID: {}", uuid);
                outputStream.flush();

                log.info("Successfully wrote Excel file to stream for UUID: {}", uuid);
            } catch (Exception e) {
                log.error("An error occurred while writing Excel file to stream for UUID: {}", uuid, e);
                throw new RuntimeException("Failed to write Excel file to stream", e);
            }
        };
    }

    private void handleTimeout(HttpServletResponse response, DeferredResult<StreamingResponseBody> deferredResult) {
        response.setStatus(SC_REQUEST_TIMEOUT);
        deferredResult.setErrorResult("Request timed out");
    }
}