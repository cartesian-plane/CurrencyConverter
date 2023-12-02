import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class EnviromentFileParser {
    public static String readFirstLine(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String firstLine = reader.readLine();
            return (firstLine != null && !firstLine.trim().isEmpty()) ? firstLine.trim() : null;
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
            return null;
        }
    }

    public static void main(String[] args) {
        String filePath = "keys.env";

        // Read and print the first line from the .env file
        String firstLine = readFirstLine(filePath);

    }
}
