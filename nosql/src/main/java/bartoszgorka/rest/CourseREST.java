package bartoszgorka.rest;

import bartoszgorka.Server;
import bartoszgorka.models.Course;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/courses/{ID}")
public class CourseREST {
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @PermitAll
    public Response getCourse(@PathParam("ID") int courseID) throws NotFoundException {
        Course course = Server.getDatabase().getCourseByID(courseID);
        return Response.ok(course).build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @RolesAllowed({"admin", "supervisor"})
    public Response updateCourse(@PathParam("ID") int courseID, Course body) throws NotFoundException {
        Course course = Server.getDatabase().getCourseByID(courseID);
        Course updatedCourse = Server.getDatabase().updateCourse(course, body);
        return Response.ok(updatedCourse).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @RolesAllowed("admin")
    public Response removeCourse(@PathParam("ID") int courseID) throws NotFoundException {
        Course course = Server.getDatabase().getCourseByID(courseID);
        Server.getDatabase().removeCourse(course);
        return Response.noContent().build();
    }
}
