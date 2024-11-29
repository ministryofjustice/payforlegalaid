package uk.gov.laa.gpfd.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uk.gov.laa.gpfd.dao.MappingTableDao;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;
import uk.gov.laa.gpfd.model.MappingTable;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class MappingTableService {

    private final ModelMapper mapper;
    private final MappingTableDao mappingTableDao;

    public MappingTable getDetailsForSpecificMapping(int requestedId)  {
        List<MappingTable> reportListResponses;

        if (requestedId < 1000 && requestedId > 0) {
            reportListResponses = mappingTableDao.fetchReportList();
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

    public List<ReportsGet200ResponseReportListInner> fetchReportListEntries() throws DatabaseReadException {
        List<ReportsGet200ResponseReportListInner> reportListEntries = new ArrayList<>();

        List<MappingTable> mappingTableObjectList = mappingTableDao.fetchReportList();

        mappingTableObjectList.forEach(obj -> {
            ReportsGet200ResponseReportListInner reportListResponse = mapper.map(obj, ReportsGet200ResponseReportListInner.class);

            reportListEntries.add(reportListResponse);
        });

        return reportListEntries;
    }

    /**
     * Create a ReportListResponse with report metadata such as reportname, obtained from the CSV to SQL mapping table
     *
     * @param requestedId - the id of the requested report
     * @return a ReportListResponse from the CSV - SQL mapping table
     */
    public ReportsGet200ResponseReportListInner getDetailsForSpecificReport(int requestedId)  {
        MappingTable mappingEntry = getDetailsForSpecificMapping(requestedId);

        return mapper.map(mappingEntry, ReportsGet200ResponseReportListInner.class);
    }
}