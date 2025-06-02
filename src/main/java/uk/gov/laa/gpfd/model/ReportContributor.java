package uk.gov.laa.gpfd.model;

import jakarta.annotation.Nullable;
import org.immutables.value.Value;

import static uk.gov.laa.gpfd.model.ReportContributor.ContributorRole.CREATOR;
import static uk.gov.laa.gpfd.model.ReportContributor.ContributorRole.OWNER;

@Value.Immutable
@Value.Style(
        builder = "newBuilder",
        init = "with*",
        build = "create"
)
public abstract class ReportContributor implements Identifiable {

    @Nullable
    public abstract String getName();

    @Nullable
    @Value.Redacted
    public abstract String getEmail();

    public abstract ContributorRole getRole();

    public enum ContributorRole {
        OWNER("Owns the report and its distribution"),
        CREATOR("Originally authored the report");

        private final String description;

        ContributorRole(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    @Value.Immutable
    public abstract static class ReportCreator extends ReportContributor {
        @Override
        public ContributorRole getRole() {
            return CREATOR;
        }
    }

    @Value.Immutable
    public abstract static class ReportOwner extends ReportContributor {
        @Override
        public ContributorRole getRole() {
            return OWNER;
        }
    }
}
