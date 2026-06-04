package uk.gov.laa.gpfd.services.excel.copier.types.xssf;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PivotTableDirectorTest {

    @Test
    void constructor_shouldRejectNullBuilder() {
        assertThrows(IllegalArgumentException.class, () -> PivotTableDirector.standard(null));
    }

    @Test
    void standard_shouldConfigureBuilderWithDefaultConfiguratorAndBuild() {
        var builder = Mockito.mock(PivotTableBuilder.class);
        when(builder.withConfigurator(any(PivotTableConfigurator.class))).thenReturn(builder);

        var director = PivotTableDirector.standard(builder);
        director.construct();

        verify(builder, times(1)).withConfigurator(any(PivotTableConfigurator.class));
        verify(builder, times(1)).build();
    }

    @Test
    void minimal_shouldConfigureBuilderWithMinimalConfiguratorAndBuild() {
        var builder = Mockito.mock(PivotTableBuilder.class);
        when(builder.withConfigurator(any(PivotTableConfigurator.class))).thenReturn(builder);

        var director = PivotTableDirector.minimal(builder);
        director.construct();

        verify(builder, times(1)).withConfigurator(any(PivotTableConfigurator.class));
        verify(builder, times(1)).build();
    }

    @Test
    void custom_shouldConfigureBuilderWithProvidedConfiguratorAndBuild() {
        var builder = Mockito.mock(PivotTableBuilder.class);
        var configurator = new PivotTableConfigurator.CompositeConfigurator();
        when(builder.withConfigurator(configurator)).thenReturn(builder);

        var director = PivotTableDirector.custom(builder, configurator);
        director.construct();

        verify(builder, times(1)).withConfigurator(configurator);
        verify(builder, times(1)).build();
    }
}