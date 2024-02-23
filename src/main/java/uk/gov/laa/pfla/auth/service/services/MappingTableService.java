package uk.gov.laa.pfla.auth.service.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uk.gov.laa.pfla.auth.service.dao.MappingTableDao;
import uk.gov.laa.pfla.auth.service.exceptions.DatabaseReadException;
import uk.gov.laa.pfla.auth.service.exceptions.ReportIdNotFoundException;
import uk.gov.laa.pfla.auth.service.models.MappingTableModel;
import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class MappingTableService {

    private final ModelMapper mapper = new ModelMapper();


    private final MappingTableDao mappingTableDao;


    public MappingTableService(MappingTableDao mappingTableDao) {
        this.mappingTableDao = mappingTableDao;
    }

    public List<ReportListResponse> createReportListResponseList() throws DatabaseReadException {
       List<ReportListResponse> reportListResponses = new ArrayList<>();

        //Fetching reportList items from database
        List<MappingTableModel> mappingTableObjectList = mappingTableDao.fetchReportList();

        mappingTableObjectList.forEach(obj -> {
            ReportListResponse reportListResponse = mapper.map(obj, ReportListResponse.class);

            reportListResponses.add(reportListResponse);

        });




        return reportListResponses;
    }


    public ReportListResponse getDetailsForSpecificReport(int requestedId) throws IndexOutOfBoundsException, ReportIdNotFoundException, DatabaseReadException {

        List<ReportListResponse> reportListResponses = createReportListResponseList();

        int indexInt = requestedId - 1;   // The '-1' accounts for the fact that the array index starts at 0, whereas the database index/id starts at 1
        log.debug("Checking the reportListResponses for the desired reportListResponse object, the requested report ID is: {}", indexInt);

        if (indexInt >= reportListResponses.size()){
            throw new ReportIdNotFoundException("Report ID not found with ID: " + requestedId);
        }
        else {
            return reportListResponses.get(indexInt);

        }

    }
}