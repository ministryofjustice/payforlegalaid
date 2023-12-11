package uk.gov.laa.pfla.auth.service.services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uk.gov.laa.pfla.auth.service.dao.ReportTableDao;
import uk.gov.laa.pfla.auth.service.models.ReportTableModel;
import uk.gov.laa.pfla.auth.service.responses.ReportResponse;
import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ReportService {

    public static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private ModelMapper mapper = new ModelMapper();


    private final ReportTableDao reportTableDao;

    private final MappingTableService mappingTableService;


    private final List<ReportResponse> reportResponses = new ArrayList<>();

    public ReportService(ReportTableDao reportTableDao, MappingTableService mappingTableService) {
        this.reportTableDao = reportTableDao;
        this.mappingTableService = mappingTableService;

    }

    public ReportResponse createReportResponse(int id) {


        log.debug("Made it to  report service");

        //Fetching report items from database
        List<ReportTableModel> reportTableObjectList = reportTableDao.fetchReport(id);

        log.debug("Object table list: " + reportTableObjectList.toString());

        reportTableObjectList.forEach(reportTableObject -> {


            ReportResponse reportResponse = mapper.map(reportTableObject, ReportResponse.class);
            ReportListResponse reportListResponse = mappingTableService.getDetailsForSpecificReport(id);

            reportResponse.setId(id);
            reportResponse.setReportName(reportListResponse.getReportName());
            reportResponse.setReportUrl(reportListResponse.getBaseUrl());
            reportResponse.setCreationTime(LocalDateTime.now());



            reportResponses.add(reportResponse);

        });

        log.debug("Report table list: " +  reportResponses.get(0));

        return reportResponses.get(0);

    }



}