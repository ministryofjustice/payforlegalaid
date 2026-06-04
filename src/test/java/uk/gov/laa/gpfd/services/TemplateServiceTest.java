package uk.gov.laa.gpfd.services;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.gpfd.data.ReportsTestDataFactory;
import uk.gov.laa.gpfd.exception.TemplateResourceException.ExcelTemplateCreationException;
import uk.gov.laa.gpfd.model.Report;
import uk.gov.laa.gpfd.model.excel.ExcelTemplate;
import uk.gov.laa.gpfd.services.excel.template.TemplateClient;
import uk.gov.laa.gpfd.services.excel.workbook.ReportWorkbook;
import uk.gov.laa.gpfd.services.excel.workbook.StyleManager;
import uk.gov.laa.gpfd.utils.WorkbookFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

    @Mock
    private TemplateClient repository;

    @Test
    void findTemplateById_ShouldLoadWorkbookFromRepositoryAndFactory() throws Exception {
        var template = ExcelTemplate.fromString("eee30b23-2c8d-4b4b-bb11-8cd67d07915c");
        var workbook = mock(Workbook.class);
        var factory = (WorkbookFactory) input -> workbook;

        when(repository.findTemplateById(template.getId())).thenReturn(new ByteArrayInputStream(new byte[0]));

        var service = new TemplateService.ExcelTemplateService.Builder()
                .repository(repository)
                .factory(factory)
                .build();

        assertSame(workbook, service.findTemplateById(template));
        verify(repository).findTemplateById(template.getId());
    }

    @Test
    void findTemplateById_ShouldWrapIOExceptionAsExcelTemplateCreationException() throws Exception {
        var template = ExcelTemplate.fromString("eee30b23-2c8d-4b4b-bb11-8cd67d07915c");
        var factory = (WorkbookFactory) input -> { throw new IOException("boom"); };

        when(repository.findTemplateById(template.getId())).thenReturn(new ByteArrayInputStream(new byte[0]));

        var service = new TemplateService.ExcelTemplateService.Builder()
                .repository(repository)
                .factory(factory)
                .build();

        var thrown = assertThrows(ExcelTemplateCreationException.class, () -> service.findTemplateById(template));
        assertEquals(IOException.class, thrown.getCause().getClass());
    }

    @Test
    void createEmpty_ShouldReturnReportWorkbook() {
        var styleManager = mock(StyleManager.class);
        var report = ReportsTestDataFactory.createTestReport();
        var factory = (WorkbookFactory) input -> new XSSFWorkbook();

        var service = new TemplateService.ExcelTemplateService.Builder()
                .repository(repository)
                .factory(factory)
                .withStyleManager(styleManager)
                .build();

        var workbook = service.createEmpty(report);

        assertInstanceOf(ReportWorkbook.class, workbook);
    }

    @Test
    void build_WithStreamEnabled_ShouldReturnStreamingFactoryFromTemplate() throws Exception {
        var workbookBytes = new ByteArrayOutputStream();
        try (var xssf = new XSSFWorkbook()) {
            xssf.createSheet("Test");
            xssf.write(workbookBytes);
        }

        var templateClient = (TemplateClient) id -> new ByteArrayInputStream(workbookBytes.toByteArray());
        var service = new TemplateService.ExcelTemplateService.Builder()
                .repository(templateClient)
                .factory(WorkbookFactory::newWorkbook)
                .withStream(10)
                .build();

        var workbook = service.streamingFactory().create(new ByteArrayInputStream(workbookBytes.toByteArray()));

        assertInstanceOf(SXSSFWorkbook.class, workbook);
    }
}
