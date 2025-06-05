package uk.gov.laa.gpfd.dao.sql.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * A sealed interface defining policies for configuring existing {@link PreparedStatement} objects.
 * <p>
 * Implementations of this interface encapsulate post-creation configuration of JDBC statements,
 * such as setting fetch size, query timeout, or other modifiable statement properties. This
 * interface forms part of the policy pattern for JDBC statement configuration.
 * </p>
 *
 * @see PreparedStatement
 * @see StatementCreationPolicy
 * @see StatementPolicy
 */
public sealed interface StatementConfigurationPolicy
        permits
        FetchSizePolicy,
        QueryTimeoutPolicy,
        StatementPolicy {

    /**
     * Applies the policy's configuration to a prepared statement.
     * <p>
     * Implementations should modify the statement's configuration according to their
     * specific purpose, such as setting performance parameters or execution controls.
     * </p>
     *
     * @param ps the prepared statement to configure (must not be null)
     * @throws SQLException             if a database access error occurs
     * @throws IllegalArgumentException if the statement parameter is null
     */
    void configure(PreparedStatement ps) throws SQLException;
}
