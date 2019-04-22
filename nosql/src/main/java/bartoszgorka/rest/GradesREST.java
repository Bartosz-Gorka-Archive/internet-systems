package bartoszgorka.rest;

import bartoszgorka.Server;
import bartoszgorka.models.Course;
import bartoszgorka.models.Grade;
import bartoszgorka.models.Student;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/students/{index}/grades")
public class GradesREST {
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @PermitAll
    public List<Grade> getGradesForStudent(
            @PathParam("index") int index,
            @QueryParam("course_id") int courseId,
            @QueryParam("grade") Grade.GradeValue value,
            @QueryParam("order") String order) {
        return Server.getDatabase().getGrades(index, courseId, value, order);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @RolesAllowed({"supervisor", "admin"})
    public Response registerNewGrade(@PathParam("index") int index, Grade body, @Context UriInfo uriInfo) throws NotFoundException, BadRequestException {
        Student student = Server.getDatabase().getStudentByID(index);
        Course course = Server.getDatabase().getCourseByID(body.getCourseID());
        Grade newGrade = Server.getDatabase().registerNewGrade(student, course, body);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Integer.toString(newGrade.getID()));
        return Response.created(builder.build()).entity(newGrade).build();
    }
}