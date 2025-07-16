package uk.gov.laa.gpfd.controller.ui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.laa.gpfd.controller.ReportsController;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ReportsViewController {

    private final ReportsController reportsApiController;

    @GetMapping("/ui")
    public String index() {
        return "redirect:/ui/reports";
    }

    @GetMapping("/ui/reports")
    public String getAllReports(Model model) {
        record Dto(
                UUID id,
                String reportName,
                String description,
                URI reportDownloadUrl
        ) {
        }
        var reportList = Objects.requireNonNull(reportsApiController.reportsGet().getBody()).getReportList().stream()
                .map(reportItem ->
                        new Dto(
                                reportItem.getId(),
                                reportItem.getReportName(),
                                reportItem.getDescription(),
                                reportsApiController.getReportById(reportItem.getId()).getBody().getReportDownloadUrl()
                        )
                )
                .toList();

        model.addAttribute("reportListResponse", reportList);
        return "reports/list";
    }

}
