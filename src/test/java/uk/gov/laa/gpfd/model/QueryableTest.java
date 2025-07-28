package uk.gov.laa.gpfd.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.laa.gpfd.data.ReportsTestDataFactory;
import uk.gov.laa.gpfd.model.excel.ExcelSheet;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class QueryableTest {

    @Nested
    class ReportQueryQueryableSelector {

        @Test
        void presentOnly_shouldFilterOutNullQueries() {
            var nullQueryMapping = new TestMapping(null);
            var presentQueryMapping = new TestMapping(ReportQuerySql.of("SELECT * FROM ANY_REPORT.TEST"));
            var source = new TestQueryable(List.of(nullQueryMapping, presentQueryMapping));

            var result = Queryable.processor(source)
                    .presentOnly()
                    .allMappings();

            assertEquals(1, result.size());
            assertEquals(presentQueryMapping, result.get(0));
        }

        @Test
        void presentOnly_shouldKeepPresentQueries() {
            var query1 = new TestMapping(ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE1"));
            var query2 = new TestMapping(ReportQuerySql.of("SELECT * FROM ANY_REPORT.TABLE2"));
            var source = new TestQueryable(List.of(query1, query2));

            var result = Queryable.processor(source)
                    .presentOnly()
                    .allMappings();

            assertEquals(2, result.size());
            assertTrue(result.containsAll(List.of(query1, query2)));
        }

        @Test
        void presentOnly_shouldWorkWithEmptyCollection() {
            var source = new TestQueryable(emptyList());

            var result = Queryable.processor(source)
                    .presentOnly()
                    .allMappings();

            assertTrue(result.isEmpty());
        }

        @Test
        void presentOnly_shouldFilterOutNoneQueries() {
            var noneQueryMapping = new TestMapping(ReportQuerySql.ofNullable(null));
            var presentQueryMapping = new TestMapping(ReportQuerySql.of("SELECT * FROM ANY_REPORT.TEST"));
            var source = new TestQueryable(List.of(noneQueryMapping, presentQueryMapping));

            var result = Queryable.processor(source)
                    .presentOnly()
                    .allMappings();

            assertEquals(1, result.size());
            assertEquals(presentQueryMapping, result.get(0));
        }

        @Test
        void presentOnly_shouldWorkWithAllInvalidQueries() {
            var source = new TestQueryable(List.of(new TestMapping(null), new TestMapping(ReportQuerySql.ofNullable(null))));

            var result = Queryable.processor(source)
                    .presentOnly()
                    .allMappings();

            assertTrue(result.isEmpty());
        }

        record TestMapping(ReportQuerySql query) implements Mapping {
            @Override
            public ReportQuerySql getQuery() {
                return query;
            }

            @Override
            public ExcelSheet getExcelSheet() {
                return null;
            }
        }

        record TestQueryable(Collection<TestMapping> queries) implements Queryable<TestMapping, TestQueryable> {
            @Override
            public Collection<TestMapping> getQueries() {
                return queries;
            }
        }

    }

    @Nested
    class GroupByTest {

        @Test
        void testGroupByWithNonNullValues() {
            var report = ReportsTestDataFactory.createTestReportWithMultipleFieldAttributes();
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