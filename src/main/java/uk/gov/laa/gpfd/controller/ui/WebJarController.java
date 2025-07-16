package uk.gov.laa.gpfd.controller.ui;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.ClassPathResource;

import static org.springframework.web.servlet.HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE;

@Profile({"local", "dev", "uat"})
@RestController
public class WebJarController {

    @GetMapping("/webjars/**")
    public ResponseEntity<Resource> serveWebJar(HttpServletRequest request) {
        var path = (String) request.getAttribute(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        var webJarPath = path.replaceFirst("^/webjars/", "META-INF/resources/webjars/");

        return ResponseEntity.ok()
                .header("Content-Type", getContentType(path))
                .body(new ClassPathResource(webJarPath));
    }

    private String getContentType(String path) {
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".png")) return "image/png";
        return "application/octet-stream";
    }
}