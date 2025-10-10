package uk.gov.laa.gpfd.services.s3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.gov.laa.gpfd.exception.ReportAccessException;
import uk.gov.laa.gpfd.utils.TokenUtils;

import java.util.List;
import java.util.UUID;

import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP000;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP012;
import static uk.gov.laa.gpfd.utils.TokenUtils.ID_REP013;

/**
 * This is to cover us not having full RBAC implementation right now.
 * For the claim reports we want to check the users are in the security groups that they are locked down to
 * We store these in the GitHub secrets as there are only two for now. Long-run with full RBAC they will be in database etc.
 */
@Slf4j
public class ReportAccessCheckerService {

    private final String rep000GroupId;
    private final String submissionReconciliationGroupId;

    public ReportAccessCheckerService(String rep000GroupId, String submissionReconciliationGroupId) {
        this.rep000GroupId = rep000GroupId;
        this.submissionReconciliationGroupId = submissionReconciliationGroupId;
    }

    public boolean checkUserCanAccessReport(UUID reportId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var groups = TokenUtils.getGroupsFromToken(authentication);

        log.info("Checking user can access report {}", reportId);
        log.info("testing: permission needed is {}", rep000GroupId);

        if (doesUserHaveAccess(reportId, groups)) {
            return true;
        } else {
            throw new ReportAccessException(reportId);
        }
    }

    private boolean doesUserHaveAccess(UUID reportId, List<String> groups) {
        //TODO if group permission empty??
        if (reportId.equals(ID_REP000) && !groups.contains(rep000GroupId)) {
            return false;
        }
        if (reportId.equals(ID_REP012) && !groups.contains(submissionReconciliationGroupId)) {
            return false;
        }
        if (reportId.equals(ID_REP013) && !groups.contains(submissionReconciliationGroupId)) {
            return false;
        }
        return true;
    }

}
