package uk.gov.laa.gpfd.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
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

    @RequestMapping(
            method = {RequestMethod.GET},
            value = {"/download/{id}"}
    )
    public ResponseEntity<InputStreamResource> getDownload(@PathVariable("id") String id) throws Exception {

        var fileName = switch(id){
            case "1": yield "oneMbCsv.csv";
            case "11": yield "elevenMbCsv.csv";
            case "70": yield "seventyMbCsv.csv";
            case "100": yield "hundredMbCsv.csv";
            default: throw new Exception("not right thingy");
        };

        var fileStream = s3ClientWrapper.getDownload(fileName);

        return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(fileStream.response().contentLength())
                .body(new InputStreamResource(fileStream));
    }

}
