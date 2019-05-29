package bartoszgorka;

import bartoszgorka.rest.*;
import bartoszgorka.storage.DB;
import bartoszgorka.storage.MongoDB;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class Server {
    public static void main(String[] args) {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(8000).build();
        ResourceConfig config = new ResourceConfig(
                StudentsREST.class,
                StudentREST.class,
                CoursesREST.class,
                CourseREST.class,
                GradesREST.class,
                GradeREST.class,
//                AuthenticationFilter.class,
                DeclarativeLinkingFeature.class
        );
        // If you want show logs - add ExceptionMapper.class
        config.register(CORSHeaders.class);
        config.register(ExceptionMapper.class);

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
    }

    public static DB getDatabase() {
        return MongoDB.getInstance();
    }
}