package uk.gov.laa.gpfd.testsupport;

import java.util.List;
import java.util.Map;

/**
 * SiLAS role names stored in the tracking RDS metadata tables.
 * Short aliases are kept for Cucumber tags, which cannot contain whitespace.
 */
public final class TestRoles {

    public static final String REP000 = "Get legal aid data - REP000";
    public static final String RECONCILIATION = "Get legal aid data - Reconciliation";
    public static final String FINANCIAL = "Get legal aid data - Financial";

    private static final Map<String, String> ALIASES = Map.of(
            "REP000", REP000,
            "Reconciliation", RECONCILIATION,
            "Financial", FINANCIAL
    );

    private TestRoles() {
    }

    public static List<String> all() {
        return List.of(REP000, FINANCIAL, RECONCILIATION);
    }

    public static String resolve(String roleOrAlias) {
        return ALIASES.getOrDefault(roleOrAlias, roleOrAlias);
    }

    public static List<String> resolveAll(List<String> rolesOrAliases) {
        return rolesOrAliases.stream().map(TestRoles::resolve).toList();
    }
}
