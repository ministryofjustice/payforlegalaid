package uk.gov.laa.gpfd.controller.ui;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.services.s3.S3ClientWrapper;

@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(name = "gpfd.s3.use-template-store", havingValue = "true")
public class DownloadController {


    private final S3ClientWrapper s3ClientWrapper;

    @RequestMapping(
            method = {RequestMethod.GET},
            value = {"/downloadurl"}
    )
    public ResponseEntity<String> getDownloadUrl(){

        var url = s3ClientWrapper.getPresignedUrl();

        return ResponseEntity.ok(url);
    }

}
