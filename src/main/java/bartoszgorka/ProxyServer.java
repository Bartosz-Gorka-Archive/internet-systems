package bartoszgorka;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import sun.misc.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ProxyServer {
    /**
     * @param args Arguments from user
     * @throws Exception Potential error - we will see stacktrace
     */
    public static void main(String[] args) throws Exception {
        // Set port
        int port = 8000;

        // Server will use all possible IP addresses
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Contexts - paths
        server.createContext("/", new ProxyServer.RootHandlerContent());

        // Show info and start server
        System.out.println("Starting server on port: " + port);
        server.start();
    }

    /**
     * Root handler
     */
    static class RootHandlerContent implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // We can log each request to Proxy
                Logger logger = Logger.getLogger("RootHandlerLogger");
                logger.info(exchange.getRequestMethod() + " " + exchange.getRequestURI().toString());

                // Build connection between Proxy and Server
                URL url = exchange.getRequestURI().toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Disable follow redirects
                connection.setInstanceFollowRedirects(false);
                HttpURLConnection.setFollowRedirects(false);

                // Set method and headers like in origin request
                connection.setRequestMethod(exchange.getRequestMethod());
                for (Map.Entry<String, List<String>> header : exchange.getRequestHeaders().entrySet()) {
                    connection.addRequestProperty(header.getKey(), String.join(", ", header.getValue()));
                }

                // Add body from origin request
                String method = exchange.getRequestMethod().toLowerCase();
                if (method.equals("post") || method.equals("put") || method.equals("patch")) {
                    connection.setDoOutput(true);
                    OutputStream outputStream = connection.getOutputStream();
                    byte[] bytes = IOUtils.readFully(exchange.getRequestBody(), -1, false);
                    outputStream.write(bytes);
                }

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
