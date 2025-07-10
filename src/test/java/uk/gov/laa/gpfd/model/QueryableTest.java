package uk.gov.laa.gpfd.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.laa.gpfd.data.ReportsTestDataFactory;

import java.util.LinkedHashMap;


class QueryableTest {
    @Nested
    class GroupByTest {

        @Test
        void testGroupByWithNonNullValues() {
            var report = ReportsTestDataFactory.createTestReportWithMultipleQueries();
            var expected = new LinkedHashMap<>();
            expected.put("Sheet1", 0);

           var result = report.getSheetOrder();

            assertEquals(expected, result);
        }

        @Test
        void shouldReturnAnEmptyListWhenThereIsNoSheetOrder() {
            var report = ReportsTestDataFactory.createTestReportWithQuery();

            var result = report.getSheetOrder();

            assertTrue(result.isEmpty());
        }

    }
}