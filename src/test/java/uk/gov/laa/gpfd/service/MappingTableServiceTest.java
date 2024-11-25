package uk.gov.laa.gpfd.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.laa.gpfd.dao.MappingTableDao;
import uk.gov.laa.gpfd.data.MappingTableTestDataFactory;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.mapper.ReportsGet200ResponseReportListInnerMapper;
import uk.gov.laa.gpfd.model.MappingTable;
import uk.gov.laa.gpfd.services.MappingTableService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MappingTableServiceTest {

    @Mock
    private MappingTableDao mappingTableDao;

    @InjectMocks
    private MappingTableService mappingTableService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnReportListEntriesWhenValidReportsExist() {
        // Given
        var mappingTable1 = MappingTableTestDataFactory.aValidBankAccountReport();
        var mappingTable2 = MappingTableTestDataFactory.aValidInvoiceAnalysisReport();
        var list = Arrays.asList(mappingTable1, mappingTable2);
        var expected = list.stream().map(ReportsGet200ResponseReportListInnerMapper::map).toList();

        when(mappingTableDao.fetchReportList()).thenReturn(list);

        // When
        var result = mappingTableService.fetchReportListEntries();

        // Then
        assertNotNull(result);
        assertEquals(expected.size(), result.size());
        assertEquals(expected, result);
        verify(mappingTableDao, times(1)).fetchReportList();
    }

    @Test
    void shouldReturnEmptyListWhenNoReportsExist() {
        // Given
        when(mappingTableDao.fetchReportList()).thenReturn(Collections.emptyList());

        // When
        var result = mappingTableService.fetchReportListEntries();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mappingTableDao, times(1)).fetchReportList();
    }

    @Test
    void shouldReturnSingleReportWhenOnlyOneReportExists() throws DatabaseReadException {
        // Given
        var singleReport = MappingTableTestDataFactory.aValidInvoiceAnalysisReport();
        when(mappingTableDao.fetchReportList()).thenReturn(Collections.singletonList(singleReport));
        var expected = List.of(ReportsGet200ResponseReportListInnerMapper.map(singleReport));

        // When
        var result = mappingTableService.fetchReportListEntries();

        // Then
        assertNotNull(result);
        assertEquals(expected.size(), result.size());
        assertEquals(expected, result);
        verify(mappingTableDao, times(1)).fetchReportList();
    }

    @Test
    void shouldHandleSizeLimitExceededAndReturnAllReports() throws DatabaseReadException {
        // Given
        var largeList = new ArrayList<MappingTable>();
        for (int i = 0; i < 1001; i++) {  // Exceed the size limit
            largeList.add(MappingTableTestDataFactory.aValidInvoiceAnalysisReport());
        }
        when(mappingTableDao.fetchReportList()).thenReturn(largeList);

        // When
        var result = mappingTableService.fetchReportListEntries();

        // Then
        assertNotNull(result);
        assertEquals(largeList.size(), result.size());
        verify(mappingTableDao, times(1)).fetchReportList();
    }

    @Test
    void shouldReturnValidReportsAndIgnoreInvalidData() throws DatabaseReadException {
        // Given
        var mappingTable1 = MappingTableTestDataFactory.aValidInvoiceAnalysisReport();
        var mappingTable2 = new MappingTable(999, null, null, null, 0, null, null, null, null, null, null); // Invalid data
        var list = Arrays.asList(mappingTable1, mappingTable2);
        when(mappingTableDao.fetchReportList()).thenReturn(list);

        // When
        var result = mappingTableService.fetchReportListEntries();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());  // Only one valid mapping should be returned
        verify(mappingTableDao, times(1)).fetchReportList();
    }

    @Test
    void shouldThrowIndexOutOfBoundsExceptionWhenRequestedIdIsLessThanZero() {
        // Given
        var invalidId = 0;

        // When
        // Then
        assertThrows(IndexOutOfBoundsException.class,
                () -> mappingTableService.getDetailsForSpecificReport(invalidId));
    }

    @Test
    void shouldThrowIndexOutOfBoundsExceptionWhenRequestedIdIsGreaterThan1000() {
        // Given
        var invalidId = 1001;

        // When
        // Then
        assertThrows(IndexOutOfBoundsException.class,
                () -> mappingTableService.getDetailsForSpecificReport(invalidId));
    }

    @Test
    void shouldThrowReportIdNotFoundExceptionWhenRequestedReportDoesNotExist() {
        // Given
        var requestedId = 2;
        when(mappingTableDao.fetchReportList()).thenReturn(List.of(MappingTableTestDataFactory.aValidBankAccountReport()));

        // When
        // Then
        assertThrows(ReportIdNotFoundException.class,
                () -> mappingTableService.getDetailsForSpecificReport(requestedId));
    }

    @Test
    void shouldThrowReportIdNotFoundExceptionWhenReportListIsEmpty() {
        // Given
        var requestedId = 1;
        when(mappingTableDao.fetchReportList()).thenReturn(Collections.emptyList());

        // When
        // Then
        assertThrows(ReportIdNotFoundException.class,
                () -> mappingTableService.getDetailsForSpecificReport(requestedId));
    }

    @Test
    void shouldThrowIndexOutOfBoundsExceptionWhenRequestedIdIsNegative() {
        // Given
        var requestedId = -1;

        // When
        // Then
        assertThrows(IndexOutOfBoundsException.class,
                () -> mappingTableService.getDetailsForSpecificReport(requestedId));
    }

    @Test
    void shouldThrowReportIdNotFoundExceptionWhenRequestedIdExceedsAvailableReports() {
        // Given
        var requestedId = 100;
        when(mappingTableDao.fetchReportList()).thenReturn(List.of(MappingTableTestDataFactory.aValidBankAccountReport()));

        // When
        // Then
        assertThrows(ReportIdNotFoundException.class,
                () -> mappingTableService.getDetailsForSpecificReport(requestedId));
    }

}