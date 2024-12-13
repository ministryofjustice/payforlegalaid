package uk.gov.laa.gpfd.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import uk.gov.laa.gpfd.dao.ReportViewsDao;
import uk.gov.laa.gpfd.exception.CsvStreamException;
import uk.gov.laa.gpfd.exception.DatabaseReadException;
import uk.gov.laa.gpfd.exception.ReportIdNotFoundException;
import uk.gov.laa.gpfd.model.GetReportById200Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportViewsDao reportViewsDao;
    private final MappingTableService mappingTableService;

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
        var reportListResponse = mappingTableService.getDetailsForSpecificMapping(requestedId);

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
    public GetReportById200Response createReportResponse(int id) throws IndexOutOfBoundsException {
        var reportListResponse = mappingTableService.getDetailsForSpecificReport(id);

        var reportResponse = new GetReportById200Response() {{
            setId(reportListResponse.getId());
            setReportName(reportListResponse.getReportName());
            setReportDownloadUrl(URI.create("https://laa-pay-for-la-dev.apps.live.cloud-platform.service.justice.gov.uk/csv" + "/" + id));
        }};

        log.info("Returning report response object");

        return reportResponse;
    }

}