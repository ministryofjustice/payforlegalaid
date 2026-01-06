package uk.gov.laa.gpfd.controller.ui;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.gov.laa.gpfd.config.AppConfig;
import uk.gov.laa.gpfd.utils.UrlUtils;

@Controller
@AllArgsConstructor
public class PolicyController {

    private final AppConfig appConfig;

    @ModelAttribute("gpfdUrl")
    public String gpfdUrl() {
        return UrlUtils.getServiceUrl();
    }

    @GetMapping("/cookies")
    public String cookies() {
        return "cookies"; // resolves to cookies.html
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "privacy"; // resolves to privacy.html
    }

    @GetMapping("/accessibility")
    public String accessibility() {
        return "accessibility"; // resolves to accessibility.html
    }
}
