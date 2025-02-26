package uk.gov.laa.gpfd.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.laa.gpfd.dao.ReportsDao;
import uk.gov.laa.gpfd.mapper.ReportsGet200ResponseReportListInnerMapper;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportManagementService {
    private final ReportsDao reportsDao;

    public List<ReportsGet200ResponseReportListInner> fetchReportListEntries() {
        return reportsDao.fetchReportList().stream()
                .map(ReportsGet200ResponseReportListInnerMapper::map)
                .toList();
    }


}
