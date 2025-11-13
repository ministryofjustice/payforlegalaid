package uk.gov.laa.gpfd.controller.ui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.laa.gpfd.api.ReportsApi;

import java.net.URI;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Controller
@Profile({"local", "dev", "uat"})
@RequiredArgsConstructor
public class ReportsViewController {

    private final ReportsApi api;

    @GetMapping("/ui")
    public String index() {
        return "redirect:/";
    }

    @GetMapping({"/", "/ui/reports"})
    public String getAllReports(Model model) {
        record Dto(
                UUID id,
                String reportName,
                String description,
                URI reportDownloadUrl
        ) {
        }
        String errorMessage = null;
        List<Dto> reportList = Collections.emptyList();
        try {

            reportList = Objects.requireNonNull(api.reportsGet().getBody()).getReportList().stream()
                    .map(reportItem ->
                            new Dto(
                                    reportItem.getId(),
                                    reportItem.getReportName(),
                                    reportItem.getDescription(),
                                    api.getReportById(reportItem.getId()).getBody().getReportDownloadUrl()
                            )
                    )
                    .toList();
        } catch (Exception e) {
        // If API returned an error JSON instead of throwing, you need to inspect the body
            if (e instanceof HttpClientErrorException httpEx) {
                errorMessage = httpEx.getResponseBodyAsString(); // will contain {error: "..."}
            } else {
                errorMessage = e.getMessage();
            }
    }
      log.debug("Error message: {}", errorMessage);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("reportListResponse", reportList);
        return "reports/list";
    }

}
