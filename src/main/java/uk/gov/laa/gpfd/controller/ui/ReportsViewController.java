package uk.gov.laa.gpfd.controller.ui;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import uk.gov.laa.gpfd.api.ReportsApi;
import uk.gov.laa.gpfd.utils.UrlBuilder;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ReportsViewController {

    private final ReportsApi api;
    private final UrlBuilder urlBuilder;

    @GetMapping("/ui")
    public String index() {
        return "redirect:/";
    }

    @GetMapping({"/", "/ui/reports"})
    public String getAllReports(Model model) {

        record ReportDto(
                UUID id,
                String reportName,
                String description,
                URI reportDownloadUrl,
                String fileExtension
        ) {}

        var response = Objects.requireNonNull(api.reportsGet().getBody());

        var reportList = response.getReportList().stream()
                .map(reportItem -> {
                    var reportResponse =
                            Objects.requireNonNull(api.getReportById(reportItem.getId()).getBody());

                    var downloadUrl = reportResponse.getReportDownloadUrl();

                    return new ReportDto(
                            reportItem.getId(),
                            reportItem.getReportName(),
                            reportItem.getDescription(),
                            downloadUrl,
                            extractExtension(downloadUrl)
                    );
                })
                .toList();

        model.addAttribute("reportListResponse", reportList);
        model.addAttribute("gpfdUrl", urlBuilder.getServiceUrl());

        return "reports/list";
    }

    /**
     * Extracts the file extension from the download URL path.
     * e.g. /excel/{id}          -> xlsx
     * e.g. /csv/{id}            -> csv
     * e.g. /reports/{id}/file   -> csv
     */
    private String extractExtension(URI uri) {
        if (uri == null || uri.getPath() == null) {
            return "";
        }

        String path = uri.getPath().toLowerCase();

        if (path.contains("/excel/")) {
            return "xlsx";
        }

        return "csv";
    }

}
