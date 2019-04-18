package bartoszgorka.rest;

import bartoszgorka.models.DB;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("courses/{ID}")
public class Course {

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @PermitAll
    public Response getCourse(@PathParam("ID") int courseID) throws NotFoundException {
        bartoszgorka.models.Course c = DB.getCourses().stream().filter(course -> course.getID() == courseID).findFirst().orElse(null);
        if (c != null) {
            return Response.ok(c).build();
        }

        throw new NotFoundException();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @RolesAllowed({"admin", "supervisor"})
    public Response updateCourse(@PathParam("ID") int courseID, bartoszgorka.models.Course rawCourseBody) throws NotFoundException {
        bartoszgorka.models.Course course = DB.updateCourse(courseID, rawCourseBody);
        return Response.ok(course).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @RolesAllowed("admin")
    public Response removeCourse(@PathParam("ID") int courseID) throws NotFoundException {
        DB.removeCourse(courseID);
        return Response.noContent().build();
    }
}
