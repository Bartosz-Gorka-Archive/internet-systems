package bartoszgorka;

import bartoszgorka.rest.*;
import bartoszgorka.storage.DB;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class Server {
    public static void main(String[] args) {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(8000).build();
        DB.getInstance();
        ResourceConfig config = new ResourceConfig(
                StudentsREST.class,
                StudentREST.class,
                CoursesREST.class,
                CourseREST.class,
                GradesREST.class,
                GradeREST.class,
                AuthenticationFilter.class,
                DeclarativeLinkingFeature.class,
                ExceptionMapper.class
        );

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
    }
}