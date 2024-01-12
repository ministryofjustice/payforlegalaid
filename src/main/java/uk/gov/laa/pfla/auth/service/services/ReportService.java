package uk.gov.laa.pfla.auth.service.services;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import uk.gov.laa.pfla.auth.service.dao.ReportViewsDao;
import uk.gov.laa.pfla.auth.service.models.report_view_models.ReportModel;
import uk.gov.laa.pfla.auth.service.models.report_view_models.VBankMonth;
import uk.gov.laa.pfla.auth.service.models.report_view_models.VCisToCcmsInvoiceSummaryModel;
import uk.gov.laa.pfla.auth.service.responses.ReportResponse;
import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ReportService {

    public static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final ReportViewsDao reportViewsDao;

    private final MappingTableService mappingTableService;

    private final Map<Integer, Class<? extends ReportModel>> reportModelMapping;


    public ReportService(ReportViewsDao reportViewsDao, MappingTableService mappingTableService) {
        this.reportViewsDao = reportViewsDao;
        this.mappingTableService = mappingTableService;

        this.reportModelMapping = new HashMap<>();
        reportModelMapping.put(1, VCisToCcmsInvoiceSummaryModel.class);
        reportModelMapping.put(2, VBankMonth.class);
    }

    public ReportResponse createReportResponse(int id) throws IndexOutOfBoundsException {

        //Querying the mapping table, to obtain metadata about the report
        ReportListResponse reportListResponse;

        if(id < 1000 && id > 0){
            reportListResponse = mappingTableService.getDetailsForSpecificReport(id);
        }else{ throw new IndexOutOfBoundsException("Report ID needs to be a number between 0 and 1000");}

        Class<? extends ReportModel> classOne = reportModelMapping.get(id);
        //Fetch fetchReportViewObjectList data from MOJFIN database
//        List<ReportModel> reportViewObjectList = fetchReportViewObjectList(classOne, reportListResponse.getSqlQuery());


        // Create csv here
        List<Map<String, Object>> resultList = reportViewsDao.callDataBase(reportListResponse.getSqlQuery());
        convertToCSV(resultList);

        ReportResponse reportResponse = new ReportResponse();
        reportResponse.setId(reportListResponse.getId());
        reportResponse.setReportName(reportListResponse.getReportName());
        reportResponse.setReportUrl(reportListResponse.getBaseUrl());
        reportResponse.setCreationTime(LocalDateTime.now());


        log.debug("Report response object: {}", reportResponse);

        return reportResponse;

    }

    public List<ReportModel>  fetchReportViewObjectList(Class<? extends ReportModel> clazz, String sqlQuery) {
        //Use the id from the customer's request to define the report model we need to use (when we later query the database)

        //Fetching report items from database report views (using the SQL query string from the mapping table)
        List<ReportModel> reportViewObjectList = reportViewsDao.fetchReportViewObjectList(sqlQuery, clazz);

        log.debug("Object table list size: {}", reportViewObjectList.size()); // Checking if the list is unexpectedly empty

        return reportViewObjectList;
    }

    public void convertToCSV(List<Map<String, Object>> resultList) {


//        StringWriter sw = new StringWriter();
        FileWriter out = null;
        try {
            out = new FileWriter("book_new.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        CSVFormat csvFormat = CSVFormat.ORACLE.builder()
//                .setHeader(HEADERS)
                .build();

        try {
//            final CSVPrinter printer = CSVFormat.ORACLE.withHeader(resultList).print(out);
            final CSVPrinter printer = new CSVPrinter(out, csvFormat);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


//        CSVFormat csvFormat = CSVFormat.ORACLE.builder()
//                .setHeader(HEADERS)
//                .build();




//        return Stream.of(list)
//                .map(this::escapeSpecialCharacters)
//                .collect(Collectors.joining(","));
    }



}