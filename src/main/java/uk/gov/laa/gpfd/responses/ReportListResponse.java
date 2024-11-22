package uk.gov.laa.gpfd.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * A class defining a /reports endpoint response object. This response itself consists of a list of ReportListEntry objects
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportListResponse {

    private List<ReportListEntry> reportList;


    public void addReportListEntry(ReportListEntry reportListEntry) {
        this.reportList.add(reportListEntry);
    }
}
