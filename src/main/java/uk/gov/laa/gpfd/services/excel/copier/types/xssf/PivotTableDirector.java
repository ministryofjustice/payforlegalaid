package uk.gov.laa.gpfd.services.excel.copier.types.xssf;

import static uk.gov.laa.gpfd.services.excel.copier.types.xssf.PivotTableConfigurator.defaultConfigurator;
import static uk.gov.laa.gpfd.services.excel.copier.types.xssf.PivotTableConfigurator.minimalConfigurator;

/**
 * Directs the construction of pivot tables using a configured {@link PivotTableBuilder}.
 */
public abstract class PivotTableDirector {

    protected final PivotTableBuilder builder;

    /**
     * Creates a director that will use the specified builder.
     *
     * @param builder the pivot table builder to direct (non-null)
     * @throws IllegalArgumentException if builder is null
     */
    protected PivotTableDirector(PivotTableBuilder builder) {
        if (builder == null) {
            throw new IllegalArgumentException("PivotTableBuilder cannot be null");
        }
        this.builder = builder;
    }

    /**
     * Constructs a pivot table according to the director's configuration.
     * The exact behavior is determined by the concrete implementation.
     */
    public final void construct() {
        configureBuilder();
        builder.build();
    }

    /**
     * Configures the builder with specific settings.
     */
    protected abstract void configureBuilder();

    /**
     * Creates a standard director that builds pivot tables with default configuration.
     *
     * @param builder the builder to use
     * @return a director configured for standard pivot tables
     */
    public static PivotTableDirector standard(PivotTableBuilder builder) {
        return new PivotTableDirector(builder) {
            @Override
            protected void configureBuilder() {
                builder.withConfigurator(defaultConfigurator());
            }
        };
    }

    /**
     * Creates a minimal director that builds pivot tables with basic configuration.
     *
     * @param builder the builder to use
     * @return a director configured for minimal pivot tables
     */
    public static PivotTableDirector minimal(PivotTableBuilder builder) {
        return new PivotTableDirector(builder) {
            @Override
            protected void configureBuilder() {
                builder.withConfigurator(minimalConfigurator());
            }
        };
    }

    /**
     * Creates a custom director that allows full configuration control.
     *
     * @param builder the builder to use
     * @param configurator the configurator to apply
     * @return a director with custom configuration
     */
    public static PivotTableDirector custom(PivotTableBuilder builder,
                                            PivotTableConfigurator configurator
                                            ) {
        return new PivotTableDirector(builder) {
            @Override
            protected void configureBuilder() {
                builder.withConfigurator(configurator);
            }
        };
    }
}