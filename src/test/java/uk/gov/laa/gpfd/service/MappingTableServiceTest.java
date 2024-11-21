package uk.gov.laa.gpfd.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import uk.gov.laa.gpfd.dao.MappingTableDao;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.model.MappingTable;
import uk.gov.laa.gpfd.services.MappingTableService;
import uk.gov.laa.gpfd.model.ReportsGet200ResponseReportListInner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MappingTableServiceTest {


    @InjectMocks
    MappingTableService classUnderTest;

    @Mock
    MappingTableDao mappingTableDao;

    @Mock
    ModelMapper mapper;

    @Test
    void should_return_report_list_entry_from_mappingtablemodels() throws DatabaseReadException {

        when(mappingTableDao.fetchReportList()).thenReturn(createMappingTableModel());
        when(mapper.map(Mockito.any(), Mockito.any())).thenReturn(createMappedTableModel());

        List<ReportsGet200ResponseReportListInner> results = classUnderTest.fetchReportListEntries();
        assertEquals(results.size(), 1);
        assertEquals(results.get(0).getId(), 1);
        assertEquals(results.get(0).getDescription(), "description");
        assertEquals(results.get(0).getReportName(), "report name");
        assertEquals(results.get(0).getSqlQuery(), "select * from table1");
        assertEquals(results.get(0).getBaseUrl(), "http://nothing");
        assertEquals(results.get(0).getCsvName(), "csv name");
        assertEquals(results.get(0).getExcelReport(), "excel report");
        assertEquals(results.get(0).getExcelSheetNum(), 1);
        assertEquals(results.get(0).getOwnerEmail(), "owner@a.com");
        assertEquals(results.get(0).getReportCreator(), "report creator");
        assertEquals(results.get(0).getReportOwner(), "report owner");
    }

    @Test
    void should_return_exception_in_fetch_report_list_entries() throws DatabaseReadException {
        when(mappingTableDao.fetchReportList()).thenThrow(DatabaseReadException.class);

        assertThrows(DatabaseReadException.class, () -> classUnderTest.fetchReportListEntries());
    }

    @Test
    void should_return_out_of_range_exception_when_report_id_out_of_range() throws DatabaseReadException {
        assertThrows(IndexOutOfBoundsException.class, () -> classUnderTest.getDetailsForSpecificReport(0));
        assertThrows(IndexOutOfBoundsException.class, () -> classUnderTest.getDetailsForSpecificReport(1000));
    }

    @Test
    void should_return_report_id_not_found_exception_when_report_id_out_of_range() throws DatabaseReadException {
        when(mappingTableDao.fetchReportList()).thenReturn(createMappingTableModel());

        assertThrows(ReportIdNotFoundException.class, () -> classUnderTest.getDetailsForSpecificReport(3));
    }

    @Test
    void should_return_database_read_exception_when_database_read_fails() throws DatabaseReadException {
        when(mappingTableDao.fetchReportList()).thenThrow(DatabaseReadException.class);

        assertThrows(DatabaseReadException.class, () -> classUnderTest.getDetailsForSpecificReport(1));
    }

    @Test
    void should_return_report_read_response() throws DatabaseReadException {
        when(mappingTableDao.fetchReportList()).thenReturn(createMappingTableModel());
        when(mapper.map(Mockito.any(), Mockito.any())).thenReturn(createMappedTableModel());

        ReportsGet200ResponseReportListInner results = classUnderTest.getDetailsForSpecificReport(1);

        assertEquals(results.getId(), 1);
        assertEquals(results.getDescription(), "description");
        assertEquals(results.getReportName(), "report name");
        assertEquals(results.getSqlQuery(), "select * from table1");
        assertEquals(results.getBaseUrl(), "http://nothing");
        assertEquals(results.getCsvName(), "csv name");
        assertEquals(results.getExcelReport(), "excel report");
        assertEquals(results.getExcelSheetNum(), 1);
        assertEquals(results.getOwnerEmail(), "owner@a.com");
        assertEquals(results.getReportCreator(), "report creator");
        assertEquals(results.getReportOwner(), "report owner");

    }

    List<MappingTable> createMappingTableModel() {
        MappingTable firstReport = new MappingTable(
                1,
                "report name",
                "excel report",
                "csv name",
                1,
                "select * from table1",
                "http://nothing",
                "report owner",
                "report creator",
                "description",
                "owner@a.com"
        );

        return List.of(firstReport);
    }

    ReportsGet200ResponseReportListInner createMappedTableModel() {
        ReportsGet200ResponseReportListInner mappedReport = new ReportsGet200ResponseReportListInner();
        mappedReport.setId(1);
        mappedReport.setReportCreator("report creator");
        mappedReport.sqlQuery("select * from table1");
        mappedReport.setExcelReport("excel report");
        mappedReport.setReportName("report name");
        mappedReport.setBaseUrl("http://nothing");
        mappedReport.setDescription("description");
        mappedReport.setCsvName("csv name");
        mappedReport.setOwnerEmail("owner@a.com");
        mappedReport.setReportOwner("report owner");
        mappedReport.setExcelSheetNum(1);

       return mappedReport;
    }
}
