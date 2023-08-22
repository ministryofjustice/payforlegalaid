package uk.gov.laa.pfla.auth.service.services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uk.gov.laa.pfla.auth.service.dao.ReportTableDao;
import uk.gov.laa.pfla.auth.service.models.ReportTableModel;
import uk.gov.laa.pfla.auth.service.responses.ReportResponse;



@Service
public class ReportService {

    private ModelMapper mapper = new ModelMapper();


    private final ReportTableDao reportTableDao;

    public ReportService(ReportTableDao reportTableDao) {
        this.reportTableDao = reportTableDao;
    }

    public ReportResponse createReportResponse(int id) {

        //Fetching reportList items from database
        ReportTableModel reportTableObject = reportTableDao.fetchReport(id);

        ReportResponse reportResponse = mapper.map(reportTableObject, ReportResponse.class);

        return reportResponse;
    }



}