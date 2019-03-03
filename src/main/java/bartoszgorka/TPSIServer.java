package bartoszgorka;

import com.cedarsoftware.util.io.JsonWriter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class TPSIServer {
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
        server.createContext("/", new RootHandlerStaticFile());
        server.createContext("/echo", new EchoHandler());
        server.createContext("/redirect", new RedirectHandler());

        // Show info and start server
        System.out.println("Starting server on port: " + port);
        server.start();
    }

    /**
     * Root handler with static index.html file in response
     */
    static class RootHandlerStaticFile implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            // Read index.html file
            byte[] response = Files.readAllBytes(Paths.get("index.html"));

            // Set content type as HTML file with encoding UTF-8
            exchange.getResponseHeaders().set("Content-Type", "text/html;charset=utf-8");

            // Send response headers - status code 200 (OK) and correct length
            exchange.sendResponseHeaders(200, response.length);

            // "Write" - send response to client
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    /**
     * 6A - Echo handler. Request headers as response body
     */
    static class EchoHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder builder = new StringBuilder("{");
            String prefix = "";

            // Get request headers and prepare response
            Headers requestHeaders = exchange.getRequestHeaders();
            for (Map.Entry<String, List<String>> header : requestHeaders.entrySet()) {
                builder.append(prefix);
                prefix = ",";

                // Append structure KEY : VALUE
                String key = JsonWriter.objectToJson(header.getKey());
                builder.append(key);
                builder.append(":");

                String value = JsonWriter.objectToJson(header.getValue().toArray());
                builder.append(value);
            }
            // Append closing } char
            builder.append("}");

            // Pretty format JSON
            byte[] jsonResponse = JsonWriter.formatJson(builder.toString()).getBytes();

            // Set content type as JSON
            exchange.getResponseHeaders().set("Content-Type", "application/json");

            // Send response headers - status code 200 (OK) and correct length
            exchange.sendResponseHeaders(200, jsonResponse.length);

            // "Write" - send response to client
            OutputStream os = exchange.getResponseBody();
            os.write(jsonResponse);
            os.close();
        }
    }

    /**
     * 6B - Redirect to index
     * Codes:
     * 301 - Moved permanently - use disk cache (only first request sent)
     * 302 - Temporary - request sent but used `Location` redirect
     * 303 - See other
     */
    static class RedirectHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            int REDIRECT_CODE = 302;

            // Log request
            Logger logger = Logger.getLogger("RedirectLogger");
            logger.info(exchange.getRequestMethod() + " " + exchange.getRequestURI().toString());

            // Set content type as JSON
            exchange.getResponseHeaders().set("Content-Type", "application/json");

            // Set redirect URL
            exchange.getResponseHeaders().set("Location", "/");

            // Send response headers - status code
            byte[] response = {};
            exchange.sendResponseHeaders(REDIRECT_CODE, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }
}