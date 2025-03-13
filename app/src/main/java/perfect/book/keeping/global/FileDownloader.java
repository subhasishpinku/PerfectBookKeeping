package perfect.book.keeping.global;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloader {
    public static void downloadFile(String fileURL, String saveFilePath) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        // Check if the connection was successful (HTTP 200 OK)
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            int fileLength = connection.getContentLength();

            // Create a buffered input stream to read the data from the connection
            InputStream input = new BufferedInputStream(url.openStream());

            // Create the output stream to save the downloaded file
            FileOutputStream output = new FileOutputStream(saveFilePath);

            byte[] data = new byte[1024];
            int bytesRead;
            long totalBytesRead = 0;

            // Read the data from the input stream and write it to the output stream
            while ((bytesRead = input.read(data)) != -1) {
                output.write(data, 0, bytesRead);
                totalBytesRead += bytesRead;

                // You can update a progress bar here using the 'totalBytesRead' and 'fileLength' variables
            }

            // Close the streams
            output.flush();
            output.close();
            input.close();
        }

        // Disconnect the connection
        connection.disconnect();
    }
}
