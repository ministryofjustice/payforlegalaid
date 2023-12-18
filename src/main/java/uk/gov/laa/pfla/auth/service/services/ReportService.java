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
        //Querying the mapping table, to obtain metadata about the report
        ReportListResponse reportListResponse = mappingTableService.getDetailsForSpecificReport(id);

        //Fetching report items from database
        List<ReportTableModel> reportTableObjectList = reportTableDao.fetchReport("V_CIS_TO_CCMS_INVOICE_SUMMARY");

        if (log.isDebugEnabled()) { //checking debug is enabled so that method calls aren't performed unnecessarily - which affects performance
            log.debug("Object table list size: " + reportTableObjectList.size()
                    + " Object table report name: " + reportTableObjectList.get(0).getReportName());

            log.debug("Whole Object table list : " + reportTableObjectList);
            log.debug("Object table list, index 0,  whole object : " + reportTableObjectList.get(0));


        }


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