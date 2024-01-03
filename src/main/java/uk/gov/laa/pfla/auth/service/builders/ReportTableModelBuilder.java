//package uk.gov.laa.pfla.auth.service.builders;
//
//import uk.gov.laa.pfla.auth.service.models.report_view_models.VCisToCcmsInvoiceSummaryModel;
//
//import java.time.LocalDateTime;
//
//public class ReportTableModelBuilder {
//    private int id;
//    private String reportName;
//    private String reportUrl;
//    private LocalDateTime creationTime;
//
//    public ReportTableModelBuilder withId(int id) {
//        this.id = id;
//        return this;
//    }
//
//    public ReportTableModelBuilder withReportName(String reportName) {
//        this.reportName = reportName;
//        return this;
//    }
//
//    public ReportTableModelBuilder withReportUrl(String reportUrl) {
//        this.reportUrl = reportUrl;
//        return this;
//    }
//
//    public ReportTableModelBuilder withCreationTime(LocalDateTime creationTime) {
//        this.creationTime = creationTime;
//        return this;
//    }
//
//    public VCisToCcmsInvoiceSummaryModel createReportModel() {
//        return new VCisToCcmsInvoiceSummaryModel(id, reportName, reportUrl, creationTime);
//    }
//}