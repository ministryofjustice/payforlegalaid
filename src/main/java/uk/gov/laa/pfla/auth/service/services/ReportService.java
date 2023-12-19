package uk.gov.laa.pfla.auth.service.services;

import org.springframework.stereotype.Service;
import uk.gov.laa.pfla.auth.service.dao.ReportTableDao;
import uk.gov.laa.pfla.auth.service.models.ReportTableModel;
import uk.gov.laa.pfla.auth.service.responses.ReportResponse;
import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;
import java.util.List;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ReportService {

    public static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final ReportTableDao reportTableDao;

    private final MappingTableService mappingTableService;


    public ReportService(ReportTableDao reportTableDao, MappingTableService mappingTableService) {
        this.reportTableDao = reportTableDao;
        this.mappingTableService = mappingTableService;

    }

    public ReportResponse createReportResponse(int id) throws IndexOutOfBoundsException {

        ReportListResponse reportListResponse;

        if(id < 1000 && id > 0){
            //Querying the mapping table, to obtain metadata about the report
             reportListResponse = mappingTableService.getDetailsForSpecificReport(id);
        }else{ throw new IndexOutOfBoundsException("Report ID needs to be a number between 0 and 1000");}


        //Fetching report items from database report views (using the SQL query string from the mapping table)
        List<ReportTableModel> reportTableObjectList = reportTableDao.fetchReport(reportListResponse.getSqlQuery());

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