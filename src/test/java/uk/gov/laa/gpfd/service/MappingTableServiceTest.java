package uk.gov.laa.pfla.auth.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.pfla.auth.service.dao.MappingTableDao;
import uk.gov.laa.pfla.auth.service.exceptions.DatabaseReadException;
import uk.gov.laa.pfla.auth.service.exceptions.ReportIdNotFoundException;
import uk.gov.laa.pfla.auth.service.models.MappingTableModel;
import uk.gov.laa.pfla.auth.service.responses.ReportListEntry;
import uk.gov.laa.pfla.auth.service.services.MappingTableService;

import java.io.IOException;
import java.util.ArrayList;
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

    @Test
    void should_return_report_list_entry_from_mappingtablemodels() throws DatabaseReadException {
       when(mappingTableDao.fetchReportList()).thenReturn(createMappingTableModels());
        List<ReportListEntry> results = classUnderTest.fetchReportListEntries();
        assertEquals(results.size(), 2);
        assertEquals(results.get(0).getId(), 1);
        assertEquals(results.get(0).getDescription(), "description");
        assertEquals(results.get(0).getReportName(), "reportName");
        assertEquals(results.get(0).getSqlQuery(), "select * from table1");
        assertEquals(results.get(0).getBaseUrl(), "http://nothing");
        assertEquals(results.get(0).getCsvName(), "csv name");
        assertEquals(results.get(0).getExcelReport(), "excel report");
        assertEquals(results.get(0).getExcelSheetNum(), 1);
        assertEquals(results.get(0).getOwnerEmail(), "owner@a.com");
        assertEquals(results.get(0).getReportCreator(), "report creator");
        assertEquals(results.get(0).getReportOwner(), "report owner");
        assertEquals(results.get(1).getId(), 2);
        assertEquals(results.get(1).getDescription(), "description 2");
        assertEquals(results.get(1).getReportName(), "report name 2");
        assertEquals(results.get(1).getSqlQuery(), "select * from table2");
        assertEquals(results.get(1).getBaseUrl(), "http://nothing2");
        assertEquals(results.get(1).getCsvName(), "csv name 2");
        assertEquals(results.get(1).getExcelReport(), "excel report 2");
        assertEquals(results.get(1).getExcelSheetNum(), 1);
        assertEquals(results.get(1).getOwnerEmail(), "owner2@a.com");
        assertEquals(results.get(1).getReportCreator(), "report creator 2");
        assertEquals(results.get(1).getReportOwner(), "report owner 2");
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
        when(mappingTableDao.fetchReportList()).thenReturn(createMappingTableModels());

        assertThrows(ReportIdNotFoundException.class, () -> classUnderTest.getDetailsForSpecificReport(3));
    }

    @Test
    void should_return_database_read_exception_when_database_read_fails() throws DatabaseReadException {
        when(mappingTableDao.fetchReportList()).thenThrow(DatabaseReadException.class);

        assertThrows(DatabaseReadException.class, () -> classUnderTest.getDetailsForSpecificReport(1));
    }

    @Test
    void should_return_report_read_response() throws DatabaseReadException {
        when(mappingTableDao.fetchReportList()).thenReturn(createMappingTableModels());

        ReportListEntry results = classUnderTest.getDetailsForSpecificReport(1);

        assertEquals(results.getId(), 1);
        assertEquals(results.getDescription(), "description");
        assertEquals(results.getReportName(), "reportName");
        assertEquals(results.getSqlQuery(), "select * from table1");
        assertEquals(results.getBaseUrl(), "http://nothing");
        assertEquals(results.getCsvName(), "csv name");
        assertEquals(results.getExcelReport(), "excel report");
        assertEquals(results.getExcelSheetNum(), 1);
        assertEquals(results.getOwnerEmail(), "owner@a.com");
        assertEquals(results.getReportCreator(), "report creator");
        assertEquals(results.getReportOwner(), "report owner");

    }

    List<MappingTableModel> createMappingTableModels() {
        return List.of(
                MappingTableModel.builder()
                        .id(1)
                        .csvName("csv name")
                        .description("description")
                        .excelReport("excel report")
                        .reportName("reportName")
                        .reportOwner("report owner")
                        .sqlQuery("select * from table1")
                        .baseUrl("http://nothing")
                        .excelSheetNum(1)
                        .reportCreator("report creator")
                        .ownerEmail("owner@a.com")
                        .build(),
                MappingTableModel.builder()
                        .id(2)
                        .csvName("csv name 2")
                        .description("description 2")
                        .excelReport("excel report 2")
                        .reportName("report name 2")
                        .reportOwner("report owner 2")
                        .sqlQuery("select * from table2")
                        .baseUrl("http://nothing2")
                        .excelSheetNum(1)
                        .reportCreator("report creator 2")
                        .ownerEmail("owner2@a.com")
                        .build()
       );
    }
}
