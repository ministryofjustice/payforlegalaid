package uk.gov.laa.gpfd.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JWTAuthIT {

    //TODO WiP!
    public static void main(String[] args) throws IOException {
        String tenantId = "your-tenant-id";
        String username = "your-username";
        String password = "your-password";
        String clientId = "your-client-id";
        String clientSecret = "your-client-secret";

        String url = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token";

        String data = "grant_type=password&"
                + "client_id=" + clientId + "&"
                + "client_secret=" + clientSecret + "&"
                + "scope=https://graph.microsoft.com/.default&"
                + "username=" + username + "&"
                + "password=" + password;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        try (
                OutputStream os = connection.getOutputStream()) {
            byte[] input = data.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

       int responseCode = connection.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();


        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println("Response: " + response.toString());

        connection.disconnect();

    }
}
