package bartoszgorka.rest;

import bartoszgorka.models.DB;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/courses/{ID}")
public class Course {

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCourse(@PathParam("ID") int courseID) {
        bartoszgorka.models.Course c = DB.getCourses().stream().filter(course -> course.getID() == courseID).findFirst().orElse(null);
        if (c != null) {
            return Response.ok(c).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateCourse(@PathParam("ID") int courseID, bartoszgorka.models.Course rawCourseBody) {
        bartoszgorka.models.Course course = DB.updateCourse(courseID, rawCourseBody);
        if (course != null) {
            return Response.ok(course).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeCourse(@PathParam("ID") int courseID) {
        boolean success = DB.removeCourse(courseID);

        if (success) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
