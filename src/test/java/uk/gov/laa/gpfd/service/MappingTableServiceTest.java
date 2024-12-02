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
    void should_return_report_id_not_found_exception_when_report_not_found() {
        when(mappingTableDao.fetchReportList()).thenReturn(createMappingTableModel());

        assertThrows(ReportIdNotFoundException.class, () -> classUnderTest.getDetailsForSpecificMapping(3));
    }

    @Test
    void should_return_out_of_range_exception_when_report_not_in_range() {
        assertThrows(IndexOutOfBoundsException.class, () -> classUnderTest.getDetailsForSpecificMapping(0));
        assertThrows(IndexOutOfBoundsException.class, () -> classUnderTest.getDetailsForSpecificMapping(1000));
    }

    @Test
    void should_return_mapping_table_model_for_specific_mapping() {

        when(mappingTableDao.fetchReportList()).thenReturn(createMappingTableModel());

        MappingTable results = classUnderTest.getDetailsForSpecificMapping(1);

        assertEquals(results.id(), 1);
        assertEquals(results.description(), "description");
        assertEquals(results.reportName(), "report name");
        assertEquals(results.baseUrl(), "http://nothing");
        assertEquals(results.csvName(), "csv name");
        assertEquals(results.excelReport(), "excel report");
        assertEquals(results.excelSheetNum(), 1);
        assertEquals(results.ownerEmail(), "owner@a.com");
        assertEquals(results.sqlQuery(), "select * from table1");
        assertEquals(results.reportCreator(), "report creator");
        assertEquals(results.reportOwner(), "report owner");
    }

    @Test
    void should_return_report_list_entry_from_mappingtablemodels() throws DatabaseReadException {

        when(mappingTableDao.fetchReportList()).thenReturn(createMappingTableModel());
        when(mapper.map(Mockito.any(), Mockito.any())).thenReturn(createMappedTableModel());

        List<ReportsGet200ResponseReportListInner> results = classUnderTest.fetchReportListEntries();
        assertEquals(results.size(), 1);
        assertEquals(results.get(0).getId(), 1);
        assertEquals(results.get(0).getDescription(), "description");
        assertEquals(results.get(0).getReportName(), "report name");
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
    void should_return_report_read_response() {
        when(mappingTableDao.fetchReportList()).thenReturn(createMappingTableModel());
        when(mapper.map(Mockito.any(), Mockito.any())).thenReturn(createMappedTableModel());

        ReportsGet200ResponseReportListInner results = classUnderTest.getDetailsForSpecificReport(1);

        assertEquals(results.getId(), 1);
        assertEquals(results.getDescription(), "description");
        assertEquals(results.getReportName(), "report name");
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
        mappedReport.setReportName("report name");
        mappedReport.setDescription("description");

        return mappedReport;
    }
}