package bartoszgorka.rest;

import bartoszgorka.Server;
import bartoszgorka.models.Course;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/courses")
public class CoursesREST {
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Course> getCourses(@QueryParam("name") String name, @QueryParam("supervisor") String supervisor) {
        return Server.getDatabase().getCourses(name, supervisor);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @RolesAllowed({"supervisor", "admin"})
    public Response registerNewCourse(Course body, @Context UriInfo uriInfo) throws BadRequestException {
        Course newCourse = Server.getDatabase().registerNewCourse(body);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Integer.toString(newCourse.getID()));
        return Response.created(builder.build()).entity(newCourse).build();
    }
}