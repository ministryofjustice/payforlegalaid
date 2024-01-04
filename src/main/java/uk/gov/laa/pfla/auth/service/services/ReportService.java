package uk.gov.laa.pfla.auth.service.services;

import org.springframework.stereotype.Service;
import uk.gov.laa.pfla.auth.service.dao.ReportViewsDao;
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

    private final ReportViewsDao reportTableDao;

    private final MappingTableService mappingTableService;

    private final Map<Integer, Class<?>> reportModelMapping;


    public ReportService(ReportViewsDao reportTableDao, MappingTableService mappingTableService) {
        this.reportTableDao = reportTableDao;
        this.mappingTableService = mappingTableService;

        this.reportModelMapping = new HashMap<>();
        reportModelMapping.put(1, VCisToCcmsInvoiceSummaryModel.class);
        reportModelMapping.put(2, VBankMonth.class);

    }

    public <T> ReportResponse createReportResponse(int id) throws IndexOutOfBoundsException {

        ReportListResponse reportListResponse;

        if(id < 1000 && id > 0){
            //Querying the mapping table, to obtain metadata about the report
             reportListResponse = mappingTableService.getDetailsForSpecificReport(id);
        }else{ throw new IndexOutOfBoundsException("Report ID needs to be a number between 0 and 1000");}

        //Use the id from the customer's request to define the report model we need to use (when we later query the database)
        Class<?> requestedModelClass = reportModelMapping.get(id);

        //Fetching report items from database report views (using the SQL query string from the mapping table)
        List<T> reportTableObjectList = (List<T>) reportTableDao.fetchReport(reportListResponse.getSqlQuery(), requestedModelClass);

        log.debug("Object table list size: {}", reportTableObjectList.size()); // Checking if the list is unexpectedly empty



        // Create csv here


        ReportResponse reportResponse = new ReportResponse();
        reportResponse.setId(reportListResponse.getId());
        reportResponse.setReportName(reportListResponse.getReportName());
        reportResponse.setReportUrl(reportListResponse.getBaseUrl());
        reportResponse.setCreationTime(LocalDateTime.now());


        log.debug("Report response object: {}", reportResponse);

        return reportResponse;

    }



}