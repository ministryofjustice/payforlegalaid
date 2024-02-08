package uk.gov.laa.pfla.auth.service.services;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.models.DriveItemUploadableProperties;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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

    public void uploadCsvManual(List<Map<String, Object>> rawData, String siteUrl, String folderPath, String fileName, OAuth2AuthorizedClient oAuth2Client) throws IOException {

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
        GraphAuthenticationProvider graphAuthenticationProvider = new GraphAuthenticationProvider(oAuth2Client);

        // Construct the URL to SharePoint's file upload endpoint
        String sharePointApiUrl = String.format(
                "%s/_api/web/GetFolderByServerRelativeUrl('%s')/Files/add(url='%s', overwrite=true)",
                siteUrl, folderPath, fileName
        );  //todo - insert correct api URL
        URL sharePointFormattedUrl = new URL(sharePointApiUrl);


        ////


        // URL of the SharePoint API
        String token = oAuth2Client.getAccessToken().getTokenValue();


        // Create headers and set 'Authorization' and 'Content-Type'
        HttpHeaders headers = new HttpHeaders();
//            headers.setBearerAuth(graphAuthenticationProvider.getAuthorizationTokenAsync(sharePointFormattedUrl).join());
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

//            // Create a new HttpEntity with the headers. You can also pass a body if needed.
//            HttpEntity<InputStream> entity = new HttpEntity<>(inputStream, headers);


        byte[] byteArray = new byte[0];
        try {
            byteArray = StreamUtils.copyToByteArray(inputStream);
        } catch (IOException e) {
            log.error("IOException, byte array was not able to be created from input stream: " + e);
        }

        ByteArrayResource resource = new ByteArrayResource(byteArray) {
            @Override
            public String getFilename() {
                return fileName; // To provide a filename in the content-disposition header
            }
        };

        HttpEntity<ByteArrayResource> requestEntity = new HttpEntity<>(resource, headers);

        // Make the POST request and capture the response location URI
        URI location = restTemplate.postForLocation(sharePointApiUrl, requestEntity);

        // Output the location if you need to check it
        if (location != null) {
            log.info("Location: " + location);
        } else {
            log.error("SharePoint API error: No location returned from API call");
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

    //trying an upload with microsoft graph:
    public void uploadCsv(List<Map<String, Object>> rawData, String siteUrl, String folderPath, String fileName, OAuth2AuthorizedClient oAuth2Client) throws IOException {

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


        // Construct the URL to SharePoint's file upload endpoint
        String sharePointApiUrl = String.format(
                "%s/_api/web/GetFolderByServerRelativeUrl('%s')/Files/add(url='%s', overwrite=true)",
                siteUrl, folderPath, fileName
        );  //todo - insert correct api URL
        URL sharePointFormattedUrl = new URL(sharePointApiUrl);


        ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId("YOUR_CLIENT_ID")
                .clientSecret("YOUR_CLIENT_SECRET")
                .tenantId("YOUR_TENANT_ID")
                .build();

        IAuthenticationProvider authProvider = new TokenCredentialAuthProvider(Arrays.asList("https://graph.microsoft.com/.default"), clientSecretCredential);
        String relativePath = "/sites/FinanceSysReference-DEV/drive/root:/General/test-upload-file.csv";

        GraphServiceClient<Request> graphClient = GraphServiceClient.builder().authenticationProvider(authProvider).buildClient();
        try {

            // Create a new DriveItem with the uploaded file's content
            DriveItemUploadableProperties driveItemUploadProps = new DriveItemUploadableProperties();
            driveItemUploadProps.name = "filename.csv";


//   ------------------


//            // Variables for site, drive (optional), and item path
//            String siteHostname = "mojodevl.sharepoint.com";
//            String sitePath = "/sites/FinanceSysReference-DEV";
////            String driveId = "b!driveId"; // Optional, if not provided, default document library is used
//            String itemPath = "Documents/General/Get Payments & Financial Data Reports/Generated Reports/CCMS invoice analysis/CIS to CCMS Import Analysis/testfile111.csv"; // Path where the file will be uploaded
//
//            // Create an upload sessionx
//            UploadSession uploadSession;
//            uploadSession = graphClient.sites(siteHostname).places(sitePath).drive().root().itemWithPath(itemPath).createUploadSession(new DriveItemUploadableProperties()).buildRequest().post();


            // Specify the size of the inputStream (important for large uploads)
            long streamSize = 3000; //todo - this is a guess, a MB is equal to about 500 pages of text


            // Upload small files (less than 4MB) directly
            if (streamSize < 4 * 1024 * 1024) {
                byte[] buffer = new byte[(int) streamSize];
                inputStream.read(buffer);
                graphClient.me().drive().items(relativePath).content().buildRequest().put(buffer);
            } else {
//                // For larger files, create an upload session
//                UploadSession uploadSession = graphClient.me().drive().items(driveItemId).createUploadSession(new DriveItemUploadableProperties()).buildRequest().post();
//
//                // Create an upload provider
//                ChunkedUploadProvider<DriveItem> uploadProvider = new ChunkedUploadProvider<>(uploadSession, graphClient, inputStream, streamSize, DriveItem.class);
//
//                // Configuring chunk size (optional, defaults are available)
//                int chunkSize = 1024 * 1024; // 1MB
//                ChunkedUploadResult<DriveItem> uploadResult = uploadProvider.upload(chunkSize).get();
//
//                if (uploadResult.uploadCompleted()) {
//                    // Handle the uploaded drive item
//                    DriveItem uploadedItem = uploadResult.getItemResponse();
//                } else {
//                    // Handle the failure
//                }
            }


//            DriveItem uploadedFile = graphClient
//                    .customRequest(relativePath, DriveItem.class)
//                    .buildRequest()
//                    .put();
//
//            System.out.println("Uploaded file ID: " + uploadedFile.id);
        } catch (ClientException e) {
            log.error("SharePoint graph API error, ClientException: " + e);
        }



        String csvStreamString = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
        String singleLineContent = csvStreamString.replace("\n", "|"); //If we don't filter out newline chars then kibana will print each line as a separate log message
        log.info("CSV byte-stream data converted to a string: " + singleLineContent);

        // Clean up in-memory resources
        inputStream.close();
        byteArrayOutputStream.close();
    }


}