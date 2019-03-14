package bartoszgorka;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import sun.misc.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class ResourceServer {
    private static String filePath = "/PROJECT";

    /**
     * @param args Arguments from user
     * @throws Exception Potential error - we will see stacktrace
     */
    public static void main(String[] args) throws Exception {
        // Read file path from args
        if (args.length > 0) {
            filePath = args[0];
        }

        // Set port
        int port = 8000;

        // Server will use all possible IP addresses
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Contexts - paths
        server.createContext("/", new ResourceServer.RootHandlerContent());

        // Show info and start server
        System.out.println("Starting server on port: " + port);
        server.start();
    }

    private static Resource getContent(String path) {
        StringBuilder builder = new StringBuilder(htmlHeader());

        // Check file is directory
        File file = new File(filePath + path);

        // Verify path traversal
        boolean correctPath = false;
        try {
            String canonical = file.getCanonicalPath();
            System.out.println(canonical);
            correctPath = canonical.startsWith(filePath);
        } catch (IOException ignored) {
        }

        // Return 403 error
        if (!correctPath) {
            return new Resource(403, "Forbidden".getBytes(), null);
        }

        // MIME type
        String mimeType = null;
        try {
            mimeType = Files.probeContentType(file.toPath());
        } catch (IOException ignored) {
        }

        if (file.isDirectory() && file.canRead()) {
            // Get list of files in directory
            builder.append("<ul>");
            String prefix = path.endsWith("/") ? "" : "/";

            // Append to html file list of files
            for (File f : file.listFiles()) {
                builder.append("<li><a href=\"")
                        .append(path)
                        .append(prefix)
                        .append(f.getName())
                        .append("\">")
                        .append(f.getName())
                        .append("</a></li>");
            }

            // End list
            builder.append("</ul>");

            // Append footer and prepare response
            builder.append(htmlFooter());
            return new Resource(200, builder.toString().getBytes(), mimeType);
        } else if (file.isFile() && file.canRead()) {
            try {
                FileInputStream stream = new FileInputStream(file.getAbsolutePath());
                // TODO read files as binary content
                return new Resource(200, IOUtils.readFully(stream, -1, false), mimeType);
            } catch (Exception ignored) {
            }
        }

        return new Resource(404, "Not found".getBytes(), mimeType);
    }

    private static String htmlHeader() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Exercise 3</title>\n" +
                "</head>" +
                "<body>";
    }

    private static String htmlFooter() {
        return "</body>\n" +
                "</html>";
    }

    /**
     * Root handler
     */
    static class RootHandlerContent implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            Resource resource = getContent(exchange.getRequestURI().toString());
            int statusCode = resource.getStatusCode();
            byte[] response = resource.getMessage();

            // When found mime type (not null) - return it
            if (resource.getMimeType() != null) {
                System.out.println(resource.getMimeType());
                exchange.getResponseHeaders().set("Content-Type", resource.getMimeType());
            }

            // Return response
            exchange.sendResponseHeaders(statusCode, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }
}
