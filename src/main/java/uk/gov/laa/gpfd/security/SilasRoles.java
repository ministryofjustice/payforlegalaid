package uk.gov.laa.gpfd.security;

import java.util.List;

/**
 * Application roles as defined in SILAS (Entra) and stored in {@code glad.roles.role_name}.
 */
public final class SilasRoles {

    public static final String REP000 = "Get legal aid data - REP000";
    public static final String RECONCILIATION = "Get legal aid data - Reconciliation";
    public static final String FINANCIAL = "Get legal aid data - Financial";

    private SilasRoles() {
    }

    public static List<String> all() {
        return List.of(REP000, RECONCILIATION, FINANCIAL);
    }
}
