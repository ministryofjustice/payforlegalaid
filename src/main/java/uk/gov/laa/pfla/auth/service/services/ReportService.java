package uk.gov.laa.pfla.auth.service.services;

import org.springframework.stereotype.Service;
import uk.gov.laa.pfla.auth.service.dao.ReportViewsDao;
import uk.gov.laa.pfla.auth.service.models.report_view_models.ReportModel;
import uk.gov.laa.pfla.auth.service.models.report_view_models.VBankMonth;
import uk.gov.laa.pfla.auth.service.models.report_view_models.VCisToCcmsInvoiceSummaryModel;
import uk.gov.laa.pfla.auth.service.responses.ReportResponse;
import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ReportService {

    public static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final ReportViewsDao reportViewsDao;

    private final MappingTableService mappingTableService;

    private final Map<Integer, Class<? extends ReportModel>> reportModelMapping;


    public ReportService(ReportViewsDao reportViewsDao, MappingTableService mappingTableService) {
        this.reportViewsDao = reportViewsDao;
        this.mappingTableService = mappingTableService;

        this.reportModelMapping = new HashMap<>();
        reportModelMapping.put(1, VCisToCcmsInvoiceSummaryModel.class);
        reportModelMapping.put(2, VBankMonth.class);
    }

    public ReportResponse createReportResponse(int id) throws IndexOutOfBoundsException {

        //Querying the mapping table, to obtain metadata about the report
        ReportListResponse reportListResponse;

        if(id < 1000 && id > 0){
            reportListResponse = mappingTableService.getDetailsForSpecificReport(id);
        }else{ throw new IndexOutOfBoundsException("Report ID needs to be a number between 0 and 1000");}

        Class<? extends ReportModel> classOne = reportModelMapping.get(id);
        //Fetch report data from MOJFIN database
        List<ReportModel> reportViewObjectList = fetchReportViewObjectList(classOne, reportListResponse.getSqlQuery());


        // Create csv here
//        List<String> collect = reportViewObjectList.stream().map(new Function<ReportModel, String>() {
//            @Override
//            public String apply(ReportModel reportModel) {
//                return reportModel.asCSV();
//            }
//        }).collect(Collectors.toList());

        ReportResponse reportResponse = new ReportResponse();
        reportResponse.setId(reportListResponse.getId());
        reportResponse.setReportName(reportListResponse.getReportName());
        reportResponse.setReportUrl(reportListResponse.getBaseUrl());
        reportResponse.setCreationTime(LocalDateTime.now());


        log.debug("Report response object: {}", reportResponse);

        return reportResponse;

    }

    public List<ReportModel>  fetchReportViewObjectList(Class<? extends ReportModel> clazz, String sqlQuery) {
        //Use the id from the customer's request to define the report model we need to use (when we later query the database)

        //Fetching report items from database report views (using the SQL query string from the mapping table)
        List<ReportModel> reportViewObjectList = reportViewsDao.fetchReport(sqlQuery, clazz);

        log.debug("Object table list size: {}", reportViewObjectList.size()); // Checking if the list is unexpectedly empty

        return reportViewObjectList;
    }


}