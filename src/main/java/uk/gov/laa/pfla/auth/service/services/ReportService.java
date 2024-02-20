package uk.gov.laa.pfla.auth.service.services;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.pfla.auth.service.dao.ReportViewsDao;
import uk.gov.laa.pfla.auth.service.responses.ReportResponse;
import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ReportService {

    public static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final ReportViewsDao reportViewsDao;

    private final MappingTableService mappingTableService;

//    private final Map<Integer, Class<? extends ReportModel>> reportModelMapping; //This is only necessary if we need to create pojo objects of reports (i.e. each report might need a specific pojo object in order to transform/create a file from the data. Currently, we are using streams)


    @Autowired
    public ReportService(ReportViewsDao reportViewsDao, MappingTableService mappingTableService) {
        this.reportViewsDao = reportViewsDao;
        this.mappingTableService = mappingTableService;


//        this.reportModelMapping = new HashMap<>();
//        reportModelMapping.put(1, VCisToCcmsInvoiceSummaryModel.class);
//        reportModelMapping.put(2, VBankMonth.class);
    }


    /**
     * Obtains a resultlist of data from the MOJFIN reports database, and converts it into a CSV data stream
     * @param sqlQuery - the sql query necessary to request a specific report from the MOJFIN database
     * @return a byteArrayOutputStream - a stream of CSV data
     * @throws IOException - if conversion of the DB data to a CSV stream fails
     */
    public ByteArrayOutputStream createCsvStream(String sqlQuery) throws IOException {


        // Create CSV
        List<Map<String, Object>> resultList = null;
        try {
            resultList = reportViewsDao.callDataBase(sqlQuery);
        } catch (DataAccessException e) {
            log.error("Error reading from DB: " + e);
            // todo - throw custom DB exception type
        }
        // Generate CSV content in-memory
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (Writer writer = new OutputStreamWriter(byteArrayOutputStream);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            // Extract headers from the first map and write them to the CSV
            Map<String, Object> firstRow = resultList.get(0);
            for (String header : firstRow.keySet()) {
                csvPrinter.print(header);
            }
            csvPrinter.println();

            // Iterate through the list of maps and write data to the CSV
            for (Map<String, Object> row : resultList) {
                for (String header : firstRow.keySet()) {
                    csvPrinter.print(row.get(header));
                }
                csvPrinter.println();
            }
        }


        String csvStreamString = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
        String singleLineContent = csvStreamString.replace("\n", "|"); //If we don't filter out newline chars then kibana will print each line as a separate log message
        log.info("CSV byte-stream data converted to a string: " + singleLineContent);

        return byteArrayOutputStream;
    }


    /**
     * Create a Response entity with a CSV data stream inside the body, for use by the controller's '/csv' endpoint
     * @param requestedId - the ID of the requested report
     * @return a ResponseEntity of type 'StreamingResponseBody', containing a stream of CSV data
     */
    public ResponseEntity<StreamingResponseBody> createCSVResponse(int requestedId) throws IOException {

        //Querying the mapping table, to obtain metadata about the report
        ReportListResponse reportListResponse = getMappingTableMetadata(requestedId);


        //Get CSV data stream
        ByteArrayOutputStream csvDataOutputStream;
        try {
            csvDataOutputStream = createCsvStream(reportListResponse.getSqlQuery());
        } catch (IOException e) {
            log.error("Error creating CSV data stream: " + e);
            throw new IOException("Error creating CSV data stream: " + e);
        }

        //Create response
        StreamingResponseBody responseBody = outputStream -> {
            try {
                csvDataOutputStream.writeTo(outputStream);
                outputStream.flush();
            } catch (IOException e) {
                log.error("Error writing csv stream data to a response body " + e);
                throw new IOException("Error writing csv stream data to a response body " + e);            }
        };

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + reportListResponse.getReportName() + ".csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }


    /**
     * Create a json response to be used by the /report API endpoint. Once a caching system is in place, this response will serve as confirmation that a csv file has been created, and when.
     *
     * @param id - id of the requested report
     * @return reportResponse containing json data about the requested report
     * @throws IndexOutOfBoundsException
     * @throws IOException
     */
    public ReportResponse createReportResponse(int id ) throws IndexOutOfBoundsException {

        //Querying the mapping table, to obtain metadata about the report
        ReportListResponse reportListResponse = getMappingTableMetadata(id);

        ReportResponse reportResponse = new ReportResponse();
        reportResponse.setId(reportListResponse.getId());
        reportResponse.setReportName(reportListResponse.getReportName());
//        reportResponse.setReportSharepointUrl(reportListResponse.getBaseUrl()); // only needed if/when integrating with sharepoint
//        reportResponse.setCreationTime(LocalDateTime.now()); // it only makes sense to set the time once caching/content management system is in place
        reportResponse.setReportDownloadUrl("https://laa-pay-for-la-dev.apps.live.cloud-platform.service.justice.gov.uk/" + "csv/" + id);


        log.debug("Report response object: {}", reportResponse);

        return reportResponse;

    }

    /**
     * Create a ReportListResponse with report metadata such as reportname, obtained from the CSV to SQL mapping table
     * @param id - the id of the requested report
     * @return a ReportListResponse from the CSV - SQL mapping table
     */
    private ReportListResponse getMappingTableMetadata(int id) {
        ReportListResponse reportListResponse;
        if(id < 1000 && id > 0){
            reportListResponse = mappingTableService.getDetailsForSpecificReport(id);
        }else{ throw new IndexOutOfBoundsException("Report ID needs to be a number between 0 and 1000");}
        return reportListResponse;
    }



}