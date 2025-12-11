package uk.gov.laa.gpfd.controller.ui;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.gov.laa.gpfd.utils.GpfdUrlClient;

@Controller
@AllArgsConstructor
public class PolicyController {

    private final GpfdUrlClient gpfdUrlClient;

    @ModelAttribute("gpfdUrl")
    public String gpfdUrl() {
        return gpfdUrlClient.getGpfdUrl();
    }

    @GetMapping("/cookies")
    public String cookies() {
        return "cookies";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "privacy";
    }

    @GetMapping("/accessibility")
    public String accessibility() {
        return "accessibility";
    }
}
