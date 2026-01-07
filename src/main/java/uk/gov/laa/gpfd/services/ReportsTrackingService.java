package uk.gov.laa.gpfd.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.gov.laa.gpfd.dao.ReportDao;
import uk.gov.laa.gpfd.dao.ReportsTrackingDao;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.mapper.ReportsTrackingMapper;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportsTrackingService {
    private final ReportsTrackingDao reportsTrackingDao;
    private final ReportsTrackingMapper reportsTrackingMapper;
    private final ReportDao reportDetailsDao;
    private final UserService userService;

    @Async
    public void saveReportsTracking(UUID requestedId, String requestUrl) {
        var currentUserName = userService.getCurrentUserName();

        log.debug("Tracking report {} being accessed by {}", requestedId, currentUserName);
        var report = reportDetailsDao.fetchReportById(requestedId);
        if (report.isEmpty()) {
            throw new ReportIdNotFoundException("Report with unrecognised ID");
        }

        var reportsTracking = reportsTrackingMapper.map(report.get(), currentUserName, requestUrl);

        log.debug("Before tracking report {} being accessed by {}", requestedId, currentUserName);
        reportsTrackingDao.saveReportsTracking(reportsTracking);
        log.debug("After tracking report {} being accessed by {}", requestedId, currentUserName);
    }
}