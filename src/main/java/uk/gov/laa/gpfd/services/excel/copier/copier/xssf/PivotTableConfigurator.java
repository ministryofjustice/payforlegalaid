package uk.gov.laa.gpfd.services.excel.copier.copier.xssf;

import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotFields;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotTableStyle;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRowFields;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.laa.gpfd.services.excel.copier.copier.xssf.PivotTableConfigurator.CompositeConfigurator;
import static uk.gov.laa.gpfd.services.excel.copier.copier.xssf.PivotTableConfigurator.CustomStyleConfigurator;
import static uk.gov.laa.gpfd.services.excel.copier.copier.xssf.PivotTableConfigurator.DefinitionConfigurator;
import static uk.gov.laa.gpfd.services.excel.copier.copier.xssf.PivotTableConfigurator.LayoutConfigurator;

/**
 * Configures pivot tables during copying operations. Provides factory methods for common
 * configuration combinations and nested implementations for specific configuration aspects.
 * <p>
 * Implementations can be composed using the {@link CompositeConfigurator} to combine multiple
 * configuration behaviors.
 */
public sealed interface PivotTableConfigurator permits
        CompositeConfigurator,
        CustomStyleConfigurator,
        DefinitionConfigurator,
        LayoutConfigurator {

    /**
     * Applies configuration from source pivot table to target pivot table.
     *
     * @param source the source pivot table to copy configuration from
     * @param target the target pivot table to apply configuration to
     */
    void configure(XSSFPivotTable source, XSSFPivotTable target);

    /**
     * Creates a default configurator that includes layout and definition copying.
     *
     * @return a composite configurator with layout and definition configuration
     */
    static PivotTableConfigurator defaultConfigurator() {
        return compositeOf(new LayoutConfigurator(), new DefinitionConfigurator());
    }

    /**
     * Creates a minimal configurator that only copies the basic pivot table definition.
     *
     * @return a configurator that only copies the definition
     */
    static PivotTableConfigurator minimalConfigurator() {
        return new DefinitionConfigurator();
    }

    /**
     * Creates a styled configurator that includes layout, definition, and style configuration.
     *
     * @param styleName the name of the pivot table style to apply
     * @return a composite configurator with layout, definition and style configuration
     */
    static PivotTableConfigurator styledConfigurator(String styleName) {
        return compositeOf(new LayoutConfigurator(), new DefinitionConfigurator(), new CustomStyleConfigurator(styleName));
    }

    /**
     * Creates a composite configurator combining multiple individual configurators.
     *
     * @param configurators the configurators to combine
     * @return a composite configurator that applies all given configurators in order
     */
    static PivotTableConfigurator compositeOf(PivotTableConfigurator... configurators) {
        var composite = new CompositeConfigurator();
        for (var configurator : configurators) {
            composite.addConfigurator(configurator);
        }
        return composite;
    }

    /**
     * Composite configurator that applies multiple configurators in sequence.
     * <p>
     * Allows building complex configurations by combining simple configurators.
     */
    final class CompositeConfigurator implements PivotTableConfigurator {
        private final List<PivotTableConfigurator> configurators = new ArrayList<>();

        /**
         * Adds a configurator to this composite.
         *
         * @param configurator the configurator to add
         */
        public void addConfigurator(PivotTableConfigurator configurator) {
            configurators.add(configurator);
        }

        /**
         * Applies all contained configurators in the order they were added.
         *
         * @param source the source pivot table
         * @param target the target pivot table
         */
        @Override
        public void configure(XSSFPivotTable source, XSSFPivotTable target) {
            for (var c : configurators) c.configure(source, target);
        }
    }

    /**
     * Configures the layout aspects of a pivot table including fields, location and formatting.
     */
    final class LayoutConfigurator implements PivotTableConfigurator {
        /**
         * Copies layout configuration including pivot fields, row fields, column fields,
         * data fields, style info, location, page fields and formatting options.
         *
         * @param source the source pivot table
         * @param target the target pivot table
         */
        @Override
        public void configure(XSSFPivotTable source, XSSFPivotTable target) {
            var srcDef = source.getCTPivotTableDefinition();
            var destDef = target.getCTPivotTableDefinition();

            if (srcDef.isSetPivotFields()) {
                destDef.setPivotFields((CTPivotFields) srcDef.getPivotFields().copy());
            }
            if (srcDef.isSetRowFields()) {
                destDef.setRowFields((CTRowFields) srcDef.getRowFields().copy());
            }
            if (srcDef.isSetColFields()) {
                destDef.setColFields(srcDef.getColFields());
            }
            if (srcDef.isSetDataFields()) {
                destDef.setDataFields(srcDef.getDataFields());
            }
            if (srcDef.isSetPivotTableStyleInfo()) {
                destDef.setPivotTableStyleInfo((CTPivotTableStyle) srcDef.getPivotTableStyleInfo().copy());
            }

            destDef.setLocation(srcDef.getLocation());
            if (srcDef.isSetPageFields()) {
                destDef.setPageFields(srcDef.getPageFields());
            }

            destDef.setDataOnRows(srcDef.getDataOnRows());
            destDef.setApplyNumberFormats(srcDef.getApplyNumberFormats());
        }
    }

    /**
     * Configures the basic definition of a pivot table by copying the entire XML definition.
     */
    final class DefinitionConfigurator implements PivotTableConfigurator {
        /**
         * Copies the complete pivot table definition from source to target.
         *
         * @param source the source pivot table
         * @param target the target pivot table
         */
        @Override
        public void configure(XSSFPivotTable source, XSSFPivotTable target) {
            var srcDef = source.getCTPivotTableDefinition();
            var destDef = target.getCTPivotTableDefinition();
            destDef.set(srcDef);
        }
    }

    /**
     * Configures the visual style of a pivot table.
     */
    final class CustomStyleConfigurator implements PivotTableConfigurator {
        private final String styleName;

        /**
         * Creates a style configurator with the specified style name.
         *
         * @param styleName the name of the style to apply
         */
        public CustomStyleConfigurator(String styleName) {
            this.styleName = styleName;
        }

        /**
         * Applies the configured style to the target pivot table.
         * <p>
         * Sets the style name and enables display of row and column headers.
         *
         * @param source the source pivot table (not used)
         * @param target the target pivot table to style
         */
        @Override
        public void configure(XSSFPivotTable source, XSSFPivotTable target) {
            var destDef = target.getCTPivotTableDefinition();
            var style = destDef.isSetPivotTableStyleInfo()
                    ? destDef.getPivotTableStyleInfo()
                    : destDef.addNewPivotTableStyleInfo();

            style.setName(styleName);
            style.setShowRowHeaders(true);
            style.setShowColHeaders(true);
        }
    }
}