package bartoszgorka.rest;

import bartoszgorka.models.Course;
import bartoszgorka.models.DB;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.*;
import java.util.Set;

@Path("/courses")
public class Courses {
    @GET
    public Set<Course> getAllCourses() {
        return DB.getCourses();
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response registerNewCourse(Course rawCourseBody, @Context UriInfo uriInfo) {
        Course newCourse = DB.registerNewCourse(rawCourseBody);

        if (newCourse != null) {
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Integer.toString(newCourse.getID()));
            return Response.created(builder.build()).entity(newCourse).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}