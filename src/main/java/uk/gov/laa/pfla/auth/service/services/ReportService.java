package uk.gov.laa.pfla.auth.service.services;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.pfla.auth.service.dao.ReportViewsDao;
import uk.gov.laa.pfla.auth.service.exceptions.CsvStreamException;
import uk.gov.laa.pfla.auth.service.exceptions.DatabaseReadException;
import uk.gov.laa.pfla.auth.service.exceptions.ReportIdNotFoundException;
import uk.gov.laa.pfla.auth.service.responses.ReportResponse;
import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;

import java.io.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ReportService {

    public static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final ReportViewsDao reportViewsDao;

    private final MappingTableService mappingTableService;


    @Autowired
    public ReportService(ReportViewsDao reportViewsDao, MappingTableService mappingTableService) {
        this.reportViewsDao = reportViewsDao;
        this.mappingTableService = mappingTableService;

    }


    /**
     * Obtains a resultlist of data from the MOJFIN reports database, and converts it into a CSV data stream
     *
     * @param sqlQuery - the sql query necessary to request a specific report from the MOJFIN database
     * @return a byteArrayOutputStream - a stream of CSV data
     * @throws IOException - if conversion of the DB data to a CSV stream fails
     */
    public ByteArrayOutputStream createCsvStream(String sqlQuery) throws IOException, DatabaseReadException {


        // Get report data from DB
        List<Map<String, Object>> resultList = reportViewsDao.callDataBase(sqlQuery);

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


//        String csvStreamString = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
//        String singleLineContent = csvStreamString.replace("\n", "|"); //For debugging in DEV. If we don't filter out newline chars then kibana will print each line as a separate log message
        log.info("Returning byteArrayOutputStream of CSV data, from createCsvStream method");

        return byteArrayOutputStream;
    }


    /**
     * Create a Response entity with a CSV data stream inside the body, for use by the controller's '/csv' endpoint
     *
     * @param requestedId - the ID of the requested report
     * @return a ResponseEntity of type 'StreamingResponseBody', containing a stream of CSV data
     */
    public ResponseEntity<StreamingResponseBody> createCSVResponse(int requestedId) throws ReportIdNotFoundException, DatabaseReadException, IndexOutOfBoundsException, CsvStreamException {


        //Querying the mapping table, to obtain metadata about the report
        ReportListResponse reportListResponse = mappingTableService.getDetailsForSpecificReport(requestedId);


        //Get CSV data stream
        ByteArrayOutputStream csvDataOutputStream;
        try {
            log.debug("Creating CSV stream with id: " + reportListResponse.getId());
            csvDataOutputStream = createCsvStream(reportListResponse.getSqlQuery());
        } catch (IOException e) {
            throw new CsvStreamException("Error creating CSV data stream: " + e);
        }

        //Create response
        StreamingResponseBody responseBody = outputStream -> {
            try {
                csvDataOutputStream.writeTo(outputStream);
                outputStream.flush();
            } catch (IOException e) {
                throw new CsvStreamException("Error writing csv stream data to a response body " + e);
            }
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
     * @throws IndexOutOfBoundsException - From the getDetailsForSpecificReport() method call, if the requested index is under 0 or over 100
     * @throws ReportIdNotFoundException - From the getDetailsForSpecificReport() method call, if the requested index is not found
     * @throws DatabaseReadException     - From the createReportListResponseList() method call inside getDetailsForSpecificReport()
     */
    public ReportResponse createReportResponse(int id) throws IndexOutOfBoundsException {

        //Querying the mapping table, to obtain metadata about the report
        ReportListResponse reportListResponse = mappingTableService.getDetailsForSpecificReport(id);

        ReportResponse reportResponse = new ReportResponse();
        reportResponse.setId(reportListResponse.getId());
        reportResponse.setReportName(reportListResponse.getReportName());
//        reportResponse.setReportSharepointUrl(reportListResponse.getBaseUrl()); // only needed if/when integrating with sharepoint
//        reportResponse.setCreationTime(LocalDateTime.now()); // it only makes sense to set the time once caching/content management system is in place
        reportResponse.setReportDownloadUrl("https://laa-pay-for-la-dev.apps.live.cloud-platform.service.justice.gov.uk/" + "csv/" + id);


        log.info("Returning report response object");

        return reportResponse;

    }


}