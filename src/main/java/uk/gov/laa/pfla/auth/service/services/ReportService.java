package uk.gov.laa.pfla.auth.service.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uk.gov.laa.pfla.auth.service.dao.ReportTableDao;
import uk.gov.laa.pfla.auth.service.models.MappingTableModel;
import uk.gov.laa.pfla.auth.service.models.ReportTableModel;
import uk.gov.laa.pfla.auth.service.responses.ReportResponse;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class ReportService {

    private ModelMapper mapper = new ModelMapper();


    private final ReportTableDao reportTableDao;

    private final List<ReportResponse> reportResponses = new ArrayList<>();

    public ReportService(ReportTableDao reportTableDao) {
        this.reportTableDao = reportTableDao;
    }

    public ReportResponse createReportResponse(int id) {


        log.debug("Made it to  report service");

        //Fetching report items from database
        List<ReportTableModel> reportTableObjectList = reportTableDao.fetchReport(id);

        log.debug("Object table list: " + reportTableObjectList.toString());

        reportTableObjectList.forEach(reportTableObject -> {


          ReportResponse reportResponse = mapper.map(reportTableObject, ReportResponse.class);

          reportResponses.add(reportResponse);

        });

        log.debug("Report table list: " +  reportResponses.get(0));

        return reportResponses.get(0);

    }



}