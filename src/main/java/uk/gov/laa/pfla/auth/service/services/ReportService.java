package uk.gov.laa.pfla.auth.service.services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uk.gov.laa.pfla.auth.service.dao.ReportTableDao;
import uk.gov.laa.pfla.auth.service.models.MappingTableModel;
import uk.gov.laa.pfla.auth.service.models.ReportTableModel;
import uk.gov.laa.pfla.auth.service.responses.ReportResponse;

import java.util.ArrayList;
import java.util.List;


@Service
public class ReportService {

    private ModelMapper mapper = new ModelMapper();


    private final ReportTableDao reportTableDao;

    private final List<ReportResponse> reportResponses = new ArrayList<>();

    public ReportService(ReportTableDao reportTableDao) {
        this.reportTableDao = reportTableDao;
    }

    public ReportResponse createReportResponse(int id) {




        //Fetching report items from database
        List<ReportTableModel> reportTableObjectList = reportTableDao.fetchReport(id);

        reportTableObjectList.forEach(reportTableObject -> {


            ReportResponse reportResponse = mapper.map(reportTableObject, ReportResponse.class);

            reportResponses.add(reportResponse);

        });

        return reportResponses.get(0);

    }



}