package bartoszgorka;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

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
}