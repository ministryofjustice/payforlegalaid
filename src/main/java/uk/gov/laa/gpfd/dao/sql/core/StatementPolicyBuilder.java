package uk.gov.laa.gpfd.dao.sql.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A builder for creating composite {@link StatementPolicy} instances by combining
 * {@link StatementCreationPolicy} and multiple {@link StatementConfigurationPolicy} implementations.
 */
public class StatementPolicyBuilder {
    private StatementCreationPolicy creationPolicy = new ForwardOnlyReadOnlyPolicy();
    private final Collection<StatementConfigurationPolicy> configurationPolicies = new ArrayList<>();

    /**
     * Sets the statement creation policy for the builder.
     *
     * @param policy the creation policy to use (cannot be null)
     * @return this builder instance for method chaining
     * @throws IllegalArgumentException if the policy parameter is null
     */
    public StatementPolicyBuilder withCreationPolicy(StatementCreationPolicy policy) {
        if (policy == null) {
            throw new IllegalArgumentException("Creation policy cannot be null");
        }
        this.creationPolicy = policy;
        return this;
    }

    /**
     * Adds a configuration policy to the builder.
     * <p>
     * Configuration policies are applied in the order they are added to the builder.
     * </p>
     *
     * @param policy the configuration policy to add (cannot be null)
     * @return this builder instance for method chaining
     * @throws IllegalArgumentException if the policy parameter is null
     */
    public StatementPolicyBuilder addConfigurationPolicy(StatementConfigurationPolicy policy) {
        if (policy == null) {
            throw new IllegalArgumentException("Configuration policy cannot be null");
        }
        this.configurationPolicies.add(policy);
        return this;
    }

    /**
     * Constructs a new {@link StatementPolicy} instance based on the current builder configuration.
     * <p>
     * The returned policy will:
     * <ol>
     *   <li>Create statements using the configured {@link StatementCreationPolicy}</li>
     *   <li>Configure statements by applying all added {@link StatementConfigurationPolicy}
     *       instances in the order they were added</li>
     * </ol>
     * </p>
     *
     * @return a new immutable statement policy instance
     */
    public StatementPolicy build() {
        return new StatementPolicy() {
            @Override
            public PreparedStatement createStatement(Connection conn, String sql) throws SQLException {
                return creationPolicy.createStatement(conn, sql);
            }

            @Override
            public void configure(PreparedStatement ps) throws SQLException {
                for (StatementConfigurationPolicy policy : configurationPolicies) {
                    policy.configure(ps);
                }
            }
        };
    }
}
