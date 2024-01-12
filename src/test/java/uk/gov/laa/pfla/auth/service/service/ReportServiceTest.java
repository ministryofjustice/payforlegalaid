package uk.gov.laa.pfla.auth.service.service;

import org.apache.commons.csv.CSVFormat;
import org.json.*;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.laa.pfla.auth.service.dao.ReportViewsDao;
import uk.gov.laa.pfla.auth.service.models.report_view_models.ReportModel;
import uk.gov.laa.pfla.auth.service.models.report_view_models.VBankMonth;
import uk.gov.laa.pfla.auth.service.services.MappingTableService;
import uk.gov.laa.pfla.auth.service.services.ReportService;

import java.io.*;
import java.util.*;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    ReportViewsDao reportViewsDAO;
    @Mock
    MappingTableService mappingTableService;

    @InjectMocks
    ReportService reportService;

    Map<String, Object> authorBookMap;
    List<Map<String, Object>> authorBookMapList = new ArrayList<>();
    @BeforeEach
    void init() {
        JSONObject json1;

//        LOG.info("startup");
        try {

            json1 = new JSONObject("{\"name\":\"John\", \"age\":30, \"car\":null}");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        authorBookMap = Collections.unmodifiableMap(new LinkedHashMap<String, Object>() {


            {
                put("Dan Simmons", json1);
                put("Douglas Adams", json1);
            }
        });

        authorBookMapList.add(authorBookMap);

    }




    public static final String[] HEADERS = { "author", "title" };

    enum BookHeaders{
        author, title
    }

    public static final String EXPECTED_FILESTREAM = "author,title\r\n" + "Dan Simmons,Hyperion\r\n" + "Douglas Adams,The Hitchhiker's Guide to the Galaxy";

    @Test
    public void fetchReportViewObjectList_ReturnsCorrectReportModelType(){

        // Arrange
        String sqlQuery = "SELECT * FROM ANY_REPORT.V_BANK_MONTH";

        VBankMonth expectedReportViewObject = VBankMonth.builder()
                .source("CCMS")
                .invSource("CCMS")
                .subSource("Applied Receipts")
                .paymentDate("05-jul-23")
                .paymentMonth("31-jul-23")
                .settlementType("Applied Receipts")
                .scheme("Civil")
                .subScheme("Civil Representation")
                .detailDesc("Costs Interest")
                .catCode("N/A")
                .apArMovement("N")
                .total(-15)
                .build();

        final List<ReportModel> expectedReportViewObjectList = new ArrayList<>();
        expectedReportViewObjectList.add(expectedReportViewObject);

        // Simulate report model and service behavior
        when(reportViewsDAO.fetchReportViewObjectList(sqlQuery,VBankMonth.class)).thenReturn(expectedReportViewObjectList);


        // Act
        List<ReportModel> actualReportViewObjectList = reportService.fetchReportViewObjectList(VBankMonth.class, sqlQuery);

        // Assert
        verify(reportViewsDAO, times(1)).fetchReportViewObjectList(sqlQuery,VBankMonth.class);
        assertEquals(1, actualReportViewObjectList.size());
        assertEquals(expectedReportViewObject, actualReportViewObjectList.get(0));
        assertThat(actualReportViewObjectList.get(0), instanceOf(VBankMonth.class));

    }


    @Test
    void givenAuthorBookMap_whenWrittenToStream_thenOutputStreamAsExpected() throws IOException {
        StringWriter sw = new StringWriter();
        FileWriter out = new FileWriter("book_new.csv");


        CSVFormat csvFormat = CSVFormat.ORACLE.builder()
                .setHeader(HEADERS)
                .build();

        reportService.convertToCSV(authorBookMapList);
//        try (CSVPrinter printer = new CSVPrinter(out, csvFormat)) {
//            AUTHOR_BOOK_MAP.forEach((author, title) -> {
//                try {
//                    printer.printRecord(author, title);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
//
//        }


//        assertEquals(EXPECTED_FILESTREAM, out.toString().trim());
    }


}