package uk.gov.laa.gpfd.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PolicyController {

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
