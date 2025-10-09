package uk.gov.laa.gpfd.services.s3;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.gov.laa.gpfd.config.S3Config;
import uk.gov.laa.gpfd.exception.OperationNotSupportedException;
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
public class ReportAccessCheckerService {

    private final S3Config s3Config;

    public ReportAccessCheckerService(S3Config s3Config) {
        this.s3Config = s3Config;
    }

    public boolean checkUserCanAccessReport(UUID reportId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var groups = TokenUtils.getGroupsFromToken(authentication);

        if (doesUserHaveAccess(reportId, groups)){
            return true;
        } else {
            throw new ReportAccessException(reportId);
        }
    }

    private boolean doesUserHaveAccess(UUID reportId, List<String> groups){
        if (reportId == ID_REP000 && !groups.contains(s3Config.getRep000GroupId())){
            return false;
        }
        if (reportId == ID_REP012 && !groups.contains(s3Config.getSubmissionReconciliationGroupId())) {
            return false;
        }
        if (reportId == ID_REP013 && !groups.contains(s3Config.getSubmissionReconciliationGroupId())) {
            return false;
        }
            return true;
    }

}
