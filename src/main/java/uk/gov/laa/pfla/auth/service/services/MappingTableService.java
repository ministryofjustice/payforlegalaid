package uk.gov.laa.pfla.auth.service.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uk.gov.laa.pfla.auth.service.dao.MappingTableDao;
import uk.gov.laa.pfla.auth.service.exceptions.DatabaseReadException;
import uk.gov.laa.pfla.auth.service.exceptions.ReportIdNotFoundException;
import uk.gov.laa.pfla.auth.service.models.MappingTableModel;
import uk.gov.laa.pfla.auth.service.responses.ReportListEntry;

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

    public List<ReportListEntry> fetchReportListEntries() throws DatabaseReadException {
        List<ReportListEntry> reportListEntries = new ArrayList<>();

        List<MappingTableModel> mappingTableObjectList = mappingTableDao.fetchReportList();

        mappingTableObjectList.forEach(obj -> {
            ReportListEntry reportListResponse = mapper.map(obj, ReportListEntry.class);

            reportListEntries.add(reportListResponse);

        });


        return reportListEntries;
    }

    // test
    // fsdfsd
    /**
     * Create a ReportListResponse with report metadata such as reportname, obtained from the CSV to SQL mapping table
     *
     * @param requestedId - the id of the requested report
     * @return a ReportListResponse from the CSV - SQL mapping table
     */
    public ReportListEntry getDetailsForSpecificReport(int requestedId) throws IndexOutOfBoundsException, ReportIdNotFoundException, DatabaseReadException {

        List<ReportListEntry> reportListResponses;
        if (requestedId < 1000 && requestedId > 0) {
            reportListResponses = fetchReportListEntries();
        } else {
            throw new IndexOutOfBoundsException("Report ID needs to be a number between 0 and 1000");
        }


        int indexInt = requestedId - 1;   // The '-1' accounts for the fact that the array index starts at 0, whereas the database index/id starts at 1
        log.debug("Checking the reportListResponses for the desired reportListResponse object, the requested report index is: {}", indexInt);

        if (indexInt >= reportListResponses.size()) {
            throw new ReportIdNotFoundException("Report ID not found with ID: " + requestedId);
        } else {
            return reportListResponses.get(indexInt);

        }

    }
}