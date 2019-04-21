package bartoszgorka.rest;

import bartoszgorka.Server;
import bartoszgorka.models.Student;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Set;

@Path("/students")
public class StudentsREST {
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @PermitAll
    public Set<Student> getAllStudents() {
        for (Student s : Server.getDatabase().getStudents()) {
            s.clearLinks();
        }
        return Server.getDatabase().getStudents();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @RolesAllowed("admin")
    public Response registerNewStudent(Student rawStudentBody, @Context UriInfo uriInfo) throws BadRequestException {
        Student student = Server.getDatabase().addNewStudent(rawStudentBody);
        student.clearLinks();

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Integer.toString(student.getIndex()));
        return Response.created(builder.build()).entity(student).build();
    }
}
