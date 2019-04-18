package bartoszgorka;

import bartoszgorka.models.DB;
import bartoszgorka.rest.*;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class Server {
    public static void main(String[] args) {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(8000).build();
        DB.getInstance();
        DB.setBaseUri(baseUri);
        ResourceConfig config = new ResourceConfig(
                Students.class,
                Student.class,
                Courses.class,
                Course.class,
                Grades.class,
                Grade.class,
                ExceptionMapper.class
        );
        config.register(AuthenticationFilter.class);

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
    }
}
