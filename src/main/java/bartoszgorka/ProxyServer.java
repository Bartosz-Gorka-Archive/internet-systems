package bartoszgorka;

import com.opencsv.CSVReader;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import sun.misc.IOUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public class ProxyServer {
    /**
     * Black List path
     * This should be raw text file with one blocked domain in line
     */
    private final static String filtersFilePath = "black_list.txt";
    /**
     * Statistics collection
     */
    private static HashSet<Statistic> statistics = new HashSet<>();

    /**
     * @param args Arguments from user
     * @throws Exception Potential error - we will see stacktrace
     */
    public static void main(String[] args) throws Exception {
        // Set port
        int port = 8000;

        // Server will use all possible IP addresses
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Read file
        ArrayList<String> blackList = readFilters();
        setStatistics();

        // Contexts - paths
        server.createContext("/", new ProxyServer.RootHandlerContent(blackList));

        // Add hook - catch terminating to enable store already collected statistics
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());

        // Show info and start server
        System.out.println("Starting server on port: " + port);
        server.start();
    }

    /**
     * Read already collected statistics from file
     * Prevent override statistics
     */
    private static void setStatistics() {
        File file = new File("statistics.csv");
        try {
            FileReader fileReader = new FileReader(file);
            CSVReader reader = new CSVReader(fileReader);
            String[] nextRecord;
            // Skip header
            reader.readNext();

            while ((nextRecord = reader.readNext()) != null) {
                addStatistics(new Statistic(nextRecord[0], Integer.parseInt(nextRecord[2]), Integer.parseInt(nextRecord[3]), Integer.parseInt(nextRecord[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return Statistics collection
     */
    public static HashSet<Statistic> getStatistics() {
        return statistics;
    }

    /**
     * Add single statistic record to collection
     *
     * @param statistics Request / Response statistic
     */
    public static void addStatistics(Statistic statistics) {
        ProxyServer.statistics.add(statistics);
    }

    /**
     * Read file with rules
     *
     * @return Black list of domains
     * @throws Exception When not found file - throw error
     */
    private static ArrayList<String> readFilters() throws Exception {
        ArrayList<String> response = new ArrayList<>();

        // Read file line by line
        Scanner scanner = new Scanner(new File(filtersFilePath));
        while (scanner.hasNext()) {
            response.add(scanner.nextLine().toLowerCase());
        }

        return response;
    }

    /**
     * Root handler
     */
    static class RootHandlerContent implements HttpHandler {
        private static ArrayList<String> blackList;

        public RootHandlerContent(ArrayList<String> list) {
            blackList = (ArrayList<String>) list.clone();
        }

        public void handle(HttpExchange exchange) throws IOException {
            try {
                // We can log each request to Proxy
                Logger logger = Logger.getLogger("RootHandlerLogger");
                logger.info(exchange.getRequestMethod() + " " + exchange.getRequestURI().toString());

                // Build connection between Proxy and Server
                URL url = exchange.getRequestURI().toURL();

                // Check URL not blocked
                for (String s : blackList) {
                    if ((url.getAuthority() + url.getPath()).toLowerCase().contains(s)) {
                        throw new BlockedURLException();
                    }
                }

                // Open connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Disable follow redirects
                connection.setInstanceFollowRedirects(false);
                HttpURLConnection.setFollowRedirects(false);

                // Set method and headers like in origin request
                connection.setRequestMethod(exchange.getRequestMethod());
                for (Map.Entry<String, List<String>> header : exchange.getRequestHeaders().entrySet()) {
                    connection.addRequestProperty(header.getKey(), String.join(", ", header.getValue()));
                }

                int transferedBytes = 0;
                // Add body from origin request
                String method = exchange.getRequestMethod().toLowerCase();
                if (method.equals("post") || method.equals("put") || method.equals("patch")) {
                    connection.setDoOutput(true);
                    OutputStream outputStream = connection.getOutputStream();
                    byte[] bytes = IOUtils.readFully(exchange.getRequestBody(), -1, false);
                    transferedBytes = bytes.length;
                    outputStream.write(bytes);
                }

                // Add statistics
                addStatistics(new Statistic(url.getAuthority(), transferedBytes, 0, 1));

                // Make request and build response
                int code = connection.getResponseCode();
                byte[] response = {};
                try {
                    response = IOUtils.readFully(connection.getInputStream(), -1, false);
                } catch (Exception e) {
                    if (connection.getErrorStream() != null) {
                        response = IOUtils.readFully(connection.getErrorStream(), -1, false);
                    }
                }

                // Add statistics
                addStatistics(new Statistic(url.getAuthority(), 0, response.length, 1));

                // Add headers
                for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
                    if (header.getKey() != null && !header.getKey().toLowerCase().equals("transfer-encoding")) {
                        exchange.getResponseHeaders().add(header.getKey(), String.join(", ", header.getValue()));
                    }
                }

                // Return response
                exchange.sendResponseHeaders(code, response.length);
                OutputStream os = exchange.getResponseBody();
                if (response.length > 0) {
                    os.write(response);
                }
                os.close();
            } catch (BlockedURLException e) {
                // Send 403 - Forbidden
                byte[] response = "No way!".getBytes();
                exchange.sendResponseHeaders(403, response.length);
                OutputStream os = exchange.getResponseBody();
                os.write(response);
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
