package uk.gov.laa.gpfd.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.dao.ReportsDao;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.mapper.ReportsGet200ResponseReportListInnerMapper;
import uk.gov.laa.gpfd.services.ReportManagementService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportManagementServiceTest {
    @Mock
    private ReportsDao reportsDao;

    @InjectMocks
    private ReportManagementService reportsService;

    @Test
    void should_return_exception_in_fetch_report_list_entries() throws DatabaseReadException {
        when(reportsDao.fetchReportList()).thenThrow(DatabaseReadException.class);

        assertThrows(DatabaseReadException.class, () -> reportsService.fetchReportListEntries());
    }

    @Test
    void shouldReturnReportListEntriesWhenValidReportsExist() {
        // Given
        var mappingTable1 = MappingTableTestDataFactory.aValidBankAccountReport();
        var mappingTable2 = MappingTableTestDataFactory.aValidInvoiceAnalysisReport();
        var list = Arrays.asList(mappingTable1, mappingTable2);
        var expected = list.stream().map(ReportsGet200ResponseReportListInnerMapper::mapData).toList();

        when(reportsDao.fetchReportList()).thenReturn(list);

        // When
        var result = reportsService.fetchReportListEntries();

        // Then
        assertNotNull(result);
        assertEquals(expected.size(), result.size());
        assertEquals(expected, result);
        verify(reportsDao, times(1)).fetchReportList();
    }

    @Test
    void shouldReturnEmptyListWhenNoReportsExist() {
        // Given
        when(reportsDao.fetchReportList()).thenReturn(Collections.emptyList());

        // When
        var result = reportsService.fetchReportListEntries();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reportsDao, times(1)).fetchReportList();
    }

    @Test
    void shouldReturnSingleReportWhenOnlyOneReportExists() throws DatabaseReadException {
        // Given
        var singleReport = MappingTableTestDataFactory.aValidInvoiceAnalysisReport();
        when(reportsDao.fetchReportList()).thenReturn(Collections.singletonList(singleReport));
        var expected = List.of(ReportsGet200ResponseReportListInnerMapper.map(singleReport));

        // When
        var result = reportsService.fetchReportListEntries();

        // Then
        assertNotNull(result);
        assertEquals(expected.size(), result.size());
        assertEquals(expected, result);
        verify(reportsDao, times(1)).fetchReportList();
    }

    @Test
    void shouldHandleSizeLimitExceededAndReturnAllReports() throws DatabaseReadException {
        // Given
        var largeList = new ArrayList<MappingTable>();
        for (int i = 0; i < 1001; i++) {  // Exceed the size limit
            largeList.add(MappingTableTestDataFactory.aValidInvoiceAnalysisReport());
        }
        when(reportsDao.fetchReportList()).thenReturn(largeList);

        // When
        var result = reportsService.fetchReportListEntries();

        // Then
        assertNotNull(result);
        assertEquals(largeList.size(), result.size());
        verify(reportsDao, times(1)).fetchReportList();
    }

    @Test
    void shouldReturnValidReportsAndIgnoreInvalidData() throws DatabaseReadException {
        // Given
        var mappingTable1 = MappingTableTestDataFactory.aValidInvoiceAnalysisReport();
        var mappingTable2 = new MappingTable(DEFAULT_ID, null, null, null, 0, null, null, null, null, null, null); // Invalid data
        var list = Arrays.asList(mappingTable1, mappingTable2);
        when(reportsDao.fetchReportList()).thenReturn(list);

        // When
        var result = reportsService.fetchReportListEntries();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());  // Only one valid mapping should be returned
        verify(reportsDao, times(1)).fetchReportList();
    }
}
