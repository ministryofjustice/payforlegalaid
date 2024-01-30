package uk.gov.laa.pfla.auth.service.services;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.laa.pfla.auth.service.dao.ReportViewsDao;
import uk.gov.laa.pfla.auth.service.models.report_view_models.ReportModel;
import uk.gov.laa.pfla.auth.service.models.report_view_models.VBankMonth;
import uk.gov.laa.pfla.auth.service.models.report_view_models.VCisToCcmsInvoiceSummaryModel;
import uk.gov.laa.pfla.auth.service.responses.ReportResponse;
import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ReportService {

    public static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final ReportViewsDao reportViewsDao;

    private final MappingTableService mappingTableService;

    private final Map<Integer, Class<? extends ReportModel>> reportModelMapping;

    private final RestTemplate restTemplate;

    private final UserService userService;

    private final SharePointService sharePointService;


    @Autowired
    public ReportService(ReportViewsDao reportViewsDao, MappingTableService mappingTableService, RestTemplate restTemplate, UserService userService, SharePointService sharePointService) {
        this.reportViewsDao = reportViewsDao;
        this.mappingTableService = mappingTableService;
        this.restTemplate = restTemplate;
        this.userService = userService;
        this.sharePointService = sharePointService;

        this.reportModelMapping = new HashMap<>();
        reportModelMapping.put(1, VCisToCcmsInvoiceSummaryModel.class);
        reportModelMapping.put(2, VBankMonth.class);
    }

    /**
     * Generates a CSV formatted byte-stream of data, which is held in memory and sent to sharepoint via a http api call
     * @param rawData
     * @throws IOException
     */
//    public void generateAndUploadCsvToSharePoint(List<Map<String, Object>> rawData) throws IOException {
//
//        // Generate CSV content in-memory
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//        try (Writer writer = new OutputStreamWriter(byteArrayOutputStream);
//             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
//            // Extract headers from the first map and write them to the CSV
//            Map<String, Object> firstRow = rawData.get(0);
//            for (String header : firstRow.keySet()) {
//                csvPrinter.print(header);
//            }
//            csvPrinter.println();
//
//            // Iterate through the list of maps and write data to the CSV
//            for (Map<String, Object> row : rawData) {
//                for (String header : firstRow.keySet()) {
//                    csvPrinter.print(row.get(header));
//                }
//                csvPrinter.println();
//            }
//        }
//
//
//        // Convert CSV content to InputStream
//        InputStream inputStream = new ByteArrayInputStream((byteArrayOutputStream.toByteArray()));
//
//
//
//
//
//
//
//        // Configure SharePoint API URL, headers, and authentication
//        String sharePointApiUrl = "https://placeholder-sharepoint-site/api/upload"; //todo - insert correct api URL
//
//        // Upload CSV to SharePoint using HTTP client todo - this is just placeholder code for now, the actual api call still needs to be formulated
//        try {
//            restTemplate.postForLocation(sharePointApiUrl, inputStream);
//        } catch (RestClientException e) {
//            log.error("Error when sending a post HTTP message to sharepoint API - RestClientException: " + e);
//        }
//
//        String csvStreamString = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
//        String singleLineContent = csvStreamString.replace("\n", "|"); //If we don't filter out newline chars then kibana will print each line as a separate log message
//        log.info("CSV byte-stream data converted to a string: " + singleLineContent);
//
//        // Clean up in-memory resources
//        inputStream.close();
//        byteArrayOutputStream.close();
//    }


    public void generateAndUploadCsvToSharePoint(List<Map<String, Object>> rawData) throws IOException {
        String siteUrl = "https://mojodevl.sharepoint.com/sites/FinanceSysReference-DEV";
        String folderPath = "Documents/General/Get Payments & Financial Data Reports/Generated Reports/CCMS invoice analysis/CIS to CCMS Import Analysis";
        String fileName = "testfile111.csv"; // name of file to be uploaded
        sharePointService.uploadCsv(rawData, siteUrl, folderPath, fileName);
    }


//    public void sendRequestToSharepoint(
//            @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient graphClient
//    ) throws UserServiceException {
//
//
//        GraphAuthenticationProvider  graphAuthenticationProvider = new GraphAuthenticationProvider(graphClient);
//
//        String accessToken = graphAuthenticationProvider.getAuthorizationTokenAsync(https://www.mango.com).join();
//
//    }


    public ReportResponse createReportResponse(int id) throws IndexOutOfBoundsException, IOException {

        //Querying the mapping table, to obtain metadata about the report
        ReportListResponse reportListResponse;

        if(id < 1000 && id > 0){
            reportListResponse = mappingTableService.getDetailsForSpecificReport(id);
        }else{ throw new IndexOutOfBoundsException("Report ID needs to be a number between 0 and 1000");}

//        Class<? extends ReportModel> requestedReportType = reportModelMapping.get(id);

        //Fetch fetchReportViewObjectList data from MOJFIN database
//        List<ReportModel> reportViewObjectList = fetchReportViewObjectList(classOne, reportListResponse.getSqlQuery());


        // Create CSV
        List<Map<String, Object>> resultList = reportViewsDao.callDataBase(reportListResponse.getSqlQuery());

        // create a physical csv file
//        String createdReportName = null;
//        if(CollectionUtils.isNotEmpty(resultList)){
//            createdReportName = convertToCSVandWriteToFile(resultList, requestedReportType );
//        }

        // Generate in-memory csv data stream and upload to sharepoint
        if(CollectionUtils.isNotEmpty(resultList)){
            generateAndUploadCsvToSharePoint(resultList);
        }


        // Delete CSV
        // deleteLocalFile(createdReportName);

        ReportResponse reportResponse = new ReportResponse();
        reportResponse.setId(reportListResponse.getId());
        reportResponse.setReportName(reportListResponse.getReportName());
        reportResponse.setReportUrl(reportListResponse.getBaseUrl());
        reportResponse.setCreationTime(LocalDateTime.now());


        log.debug("Report response object: {}", reportResponse);

        return reportResponse;

    }



//    public static boolean deleteLocalFile(String createdReportName) {
//        log.info("Deleting file: " + createdReportName);
//        File file = new File(createdReportName);
//        if(file.delete()){
//            log.info("file deleted successfully");
//            return true;
//        }else{
//            log.error("failed to delete the file: " + createdReportName);
//            return false;
//        }
//    }


//    public List<ReportModel>  fetchReportViewObjectList(Class<? extends ReportModel> clazz, String sqlQuery) {
//        //Use the id from the customer's request to define the report model we need to use (when we later query the database)
//
//        //Fetching report items from database report views (using the SQL query string from the mapping table)
//        List<ReportModel> reportViewObjectList = reportViewsDao.fetchReportViewObjectList(sqlQuery, clazz);
//
//        log.debug("Object table list size: {}", reportViewObjectList.size()); // Checking if the list is unexpectedly empty
//
//        return reportViewObjectList;
//    }

//    public String convertToCSVandWriteToFile(List<Map<String, Object>> allRows, Class<? extends ReportModel> requestedReportType) throws ArrayIndexOutOfBoundsException, IOException {
//
//        UUID uuid = UUID.randomUUID();
//
////        StringWriter sw = new StringWriter();
//        String pathToRemove = "uk.gov.laa.pfla.auth.service.models.report_view_models.";
//        String requestedClassName = requestedReportType.getName().replace(pathToRemove, "");
//        String fileName = "/app/csv-files/" + requestedClassName + "-" + uuid + ".csv";
//        FileWriter out;
//        try {
//            out = new FileWriter(fileName);
//        } catch (IOException e) {
//            throw new IOException("error creating file: " + e);
//        }
//
//        // Reading the first line of the DB resultset and parsing this to determine the headers for the csv
//        String[] headers;
//        try {
//            headers = allRows.get(0).keySet().toArray(new String[]{});
//        } catch (ArrayIndexOutOfBoundsException e) {
//            throw new ArrayIndexOutOfBoundsException("Error reading headers from resultlist data, when constructing csv: " + e);
//        }
//        CSVFormat csvFormat = CSVFormat.DEFAULT
//                .builder()
//                .setHeader(headers)
//                .build();
//        // The CSVPrinter will automatically be closed after the try statement has completed (a'try-with-resources' statement)
//        try(CSVPrinter printer = new CSVPrinter(out, csvFormat)) {
//            for (Map<String, Object> fieldNamesAndValues : allRows) {
//                Object[] row = fieldNamesAndValues.values().toArray();
//                printer.printRecord(row);
//            }
//        } catch (IOException e) {
//            throw new IOException("error writing to file: " + e);
//        }
//
//
//        return fileName;
//    }



}