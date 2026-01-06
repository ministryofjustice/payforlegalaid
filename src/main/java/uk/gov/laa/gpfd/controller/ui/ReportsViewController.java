package uk.gov.laa.gpfd.controller.ui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.laa.gpfd.api.ReportsApi;
import uk.gov.laa.gpfd.config.AppConfig;
import uk.gov.laa.gpfd.utils.UrlUtils;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ReportsViewController {

    private final ReportsApi api;
    private final AppConfig appConfig;

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
        var reportList = Objects.requireNonNull(api.reportsGet().getBody()).getReportList().stream()
                .map(reportItem ->
                        new Dto(
                                reportItem.getId(),
                                reportItem.getReportName(),
                                reportItem.getDescription(),
                                api.getReportById(reportItem.getId()).getBody().getReportDownloadUrl()
                        )
                )
                .toList();

        model.addAttribute("reportListResponse", reportList);
        //TODO is this doing anythign???
        model.addAttribute("gpfdUrl", UrlUtils.getServiceUrl());
        return "reports/list";
    }

}
