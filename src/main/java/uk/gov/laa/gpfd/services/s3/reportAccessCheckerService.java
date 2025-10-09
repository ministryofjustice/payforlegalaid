package uk.gov.laa.gpfd.services.s3;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.gov.laa.gpfd.config.S3Config;
import uk.gov.laa.gpfd.exception.OperationNotSupportedException;
import uk.gov.laa.gpfd.utils.TokenUtils;

import java.util.UUID;

/**
 * This is to cover us not having full RBAC implementation right now.
 * For the claim reports we want to check the users are in the security groups that they are locked down to
 * We store these in the github secrets as there are only two for now. Long-run with full RBAC they will be in database etc.
 */
public class reportAccessCheckerService {

    private final S3Config s3Config;

    public reportAccessCheckerService(S3Config s3Config) {
        this.s3Config = s3Config;
    }

    public boolean checkUserCanAccessReport(UUID reportId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var groups = TokenUtils.getGroupsFromToken(authentication);

        if (reportId == UUID.fromString("523f38f0-2179-4824-b885-3a38c5e149e8") && !groups.contains(s3Config.getRep000GroupId())){
            //TODO not placeholder
            throw new OperationNotSupportedException("rep000");
        }
        if (reportId == UUID.fromString("cc55e276-97b0-4dd8-a919-26d4aa373266") && !groups.contains(s3Config.getSubmissionReconciliationGroupId())){
            throw new OperationNotSupportedException("rep012");
        }
        if (reportId == UUID.fromString("aca2120c-8f82-45a8-a682-8dedfb7997a7") && !groups.contains(s3Config.getSubmissionReconciliationGroupId())){
            throw new OperationNotSupportedException("rep013");
        }
        return true;
    }

}
