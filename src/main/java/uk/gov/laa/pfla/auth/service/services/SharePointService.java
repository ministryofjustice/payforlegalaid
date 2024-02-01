package uk.gov.laa.pfla.auth.service.services;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.laa.pfla.auth.service.graph.GraphAuthenticationProvider;


@Service
public class SharePointService {

    public static final Logger log = LoggerFactory.getLogger(SharePointService.class);

    private final OAuth2AuthorizedClientService clientService;
    private final RestTemplate restTemplate;

    public SharePointService(OAuth2AuthorizedClientService clientService, RestTemplate restTemplate) {
        this.clientService = clientService;
        this.restTemplate = restTemplate;
    }

    public void uploadCsv(List<Map<String, Object>> rawData, String siteUrl, String folderPath, String fileName, OAuth2AuthorizedClient graphClient) throws IOException {

        // Generate CSV content in-memory
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (Writer writer = new OutputStreamWriter(byteArrayOutputStream);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            // Extract headers from the first map and write them to the CSV
            Map<String, Object> firstRow = rawData.get(0);
            for (String header : firstRow.keySet()) {
                csvPrinter.print(header);
            }
            csvPrinter.println();

            // Iterate through the list of maps and write data to the CSV
            for (Map<String, Object> row : rawData) {
                for (String header : firstRow.keySet()) {
                    csvPrinter.print(row.get(header));
                }
                csvPrinter.println();
            }
        }


        // Convert CSV content to InputStream
        InputStream inputStream = new ByteArrayInputStream((byteArrayOutputStream.toByteArray()));



        ///////



//        // Retrieve the name of the currently authenticated user
//        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
//        // Load the OAuth2AuthorizedClient for the authenticated user
//        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient("gpfd-azure-dev", currentUser);
        // Passing in the client originally obtained from the "@RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient graphClient" method parameter in the getReport method in the controller.
        GraphAuthenticationProvider graphAuthenticationProvider = new GraphAuthenticationProvider(graphClient);

        // Construct the URL to SharePoint's file upload endpoint
        String sharePointApiUrl = String.format(
                "%s/_api/web/GetFolderByServerRelativeUrl('%s')/Files/add(url='%s', overwrite=true)",
                siteUrl, folderPath, fileName
        );  //todo - insert correct api URL
        URL sharePointFormattedUrl = new URL(sharePointApiUrl);



        ////
        try {

            // URL of the SharePoint API

            // Set the authorization token
            String accessToken = "your_access_token_here";

            String tokenValue = graphClient.getAccessToken().getTokenValue();


            // Create headers and set 'Authorization' and 'Content-Type'
            HttpHeaders headers = new HttpHeaders();
//            headers.setBearerAuth(graphAuthenticationProvider.getAuthorizationTokenAsync(sharePointFormattedUrl).join());
            headers.setBearerAuth(tokenValue);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            // Create a new HttpEntity with the headers. You can also pass a body if needed.
            HttpEntity<String> entity = new HttpEntity<>("your_request_body", headers);

            // Make the POST request and capture the response location URI
            URI location = restTemplate.postForLocation(sharePointApiUrl, entity);

            // Output the location if you need to check it
            if (location != null) {
                log.info("Location: " + location);
            } else {
                log.error("SharePoint API error: No location returned from API call");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            restTemplate.postForLocation(sharePointApiUrl, inputStream);
//        } catch (RestClientException e) {
//            log.error("Error when sending a post HTTP message to sharepoint API - RestClientException: " + e);
//        }

        String csvStreamString = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
        String singleLineContent = csvStreamString.replace("\n", "|"); //If we don't filter out newline chars then kibana will print each line as a separate log message
        log.info("CSV byte-stream data converted to a string: " + singleLineContent);

        // Clean up in-memory resources
        inputStream.close();
        byteArrayOutputStream.close();
    }
}