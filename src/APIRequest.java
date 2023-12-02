import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIRequest {

    public static String getCurrentRates() {
        try {
            // Replace this URL with the actual API endpoint

            final String API_KEY = EnviromentFileParser.readFirstLine("keys.env");

            String fromCurrency = "EUR";
            String toCurrency = "USD";

            String amount = "100";
            String apiUrl = "https://openexchangerates.org/api/latest.json?app_id=" + API_KEY;



            // Create URL object
            URL url = new URL(apiUrl);

            // Open a connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method (GET, POST, etc.)
            connection.setRequestMethod("GET");

            // Optional: Set request headers, including your API key if required
            // connection.setRequestProperty("Authorization", "Bearer your_api_key_here");


            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                // Handle the response data as needed
                //System.out.println("API Response: " + response.toString());
                String exchangeRates = response.toString();
                connection.disconnect();
                return exchangeRates;

            } else {
                System.out.println("API Request failed. Response Code: " + responseCode);
                connection.disconnect();
                return "";
            }



            // Close the connection

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
