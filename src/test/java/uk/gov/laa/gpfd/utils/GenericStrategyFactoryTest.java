package uk.gov.laa.gpfd.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static uk.gov.laa.gpfd.utils.StrategyFactory.createGenericStrategyFactory;

@ExtendWith(MockitoExtension.class)
class GenericStrategyFactoryTest {

    private enum TestKey { KEY1, KEY2, KEY3 }
    private interface TestStrategy {}

    @Test
    void shouldInitializeStrategiesMap() {
        var strategy1 = mock(TestStrategy.class);
        var strategy2 = mock(TestStrategy.class);
        var strategies = List.of(strategy1, strategy2);
        Function<TestStrategy, TestKey> keyExtractor = s -> s == strategy1 ? TestKey.KEY1 : TestKey.KEY2;

        StrategyFactory<TestKey, TestStrategy> factory =
                createGenericStrategyFactory(strategies, keyExtractor);

        var strategyMap = factory.getStrategies();
        assertEquals(2, strategyMap.size());
        assertEquals(strategy1, strategyMap.get(TestKey.KEY1));
        assertEquals(strategy2, strategyMap.get(TestKey.KEY2));
    }

    @Test
    void shouldHandleDuplicateKeysByKeepingFirst() {
        var strategy1 = mock(TestStrategy.class);
        var strategy2 = mock(TestStrategy.class);
        var strategies = List.of(strategy1, strategy2);
        Function<TestStrategy, TestKey> keyExtractor = s -> TestKey.KEY1; // Both map to same key

        StrategyFactory<TestKey, TestStrategy> factory =
                createGenericStrategyFactory(strategies, keyExtractor);

        assertEquals(1, factory.getStrategies().size());
        assertEquals(strategy1, factory.getStrategy(TestKey.KEY1).get());
    }

    @Test
    void getStrategy_shouldReturnEmptyOptionalForUnknownKey() {
        StrategyFactory<TestKey, TestStrategy> factory = createGenericStrategyFactory(List.of(), s -> null);

        var result = factory.getStrategy(TestKey.KEY3);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnStrategyForKnownKey() {
        var strategy = mock(TestStrategy.class);
        StrategyFactory<TestKey, TestStrategy> factory = createGenericStrategyFactory(List.of(strategy), s -> TestKey.KEY1);

        var result = factory.getStrategy(TestKey.KEY1);

        assertTrue(result.isPresent());
        assertEquals(strategy, result.get());
    }

    @Test
    void shouldReturnRequiredStrategyForKnownKey() {
        var strategy = mock(TestStrategy.class);
        StrategyFactory<TestKey, TestStrategy> factory = createGenericStrategyFactory(List.of(strategy), s -> TestKey.KEY1);

        var result = factory.getRequiredStrategy(TestKey.KEY1);

        assertEquals(strategy, result);
    }

    @Test
    void shouldThrowForUnknownKey() {
        StrategyFactory<TestKey, TestStrategy> factory = createGenericStrategyFactory(List.of(), s -> null);

        var exception = assertThrows(
                NoSuchElementException.class,
                () -> factory.getRequiredStrategy(TestKey.KEY1)
        );
        assertEquals("No strategy found for key: KEY1", exception.getMessage());
    }

    @Test
    void shouldReturnUnmodifiableMap() {
        StrategyFactory<TestKey, TestStrategy> factory = createGenericStrategyFactory(List.of(), s -> null);

        var result = factory.getStrategies();

        assertThrows(UnsupportedOperationException.class, () -> result.put(TestKey.KEY1, mock(TestStrategy.class)));
    }

    @Test
    void shouldWorkWithEmptyStrategyList() {
        StrategyFactory<TestKey, TestStrategy> factory = createGenericStrategyFactory(List.of(), s -> null);

        var result = factory.getStrategies();

        assertTrue(result.isEmpty());
    }
}