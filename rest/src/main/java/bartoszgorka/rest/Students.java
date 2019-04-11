package bartoszgorka.rest;

import bartoszgorka.models.DB;
import bartoszgorka.models.Student;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Set;

@Path("/students")
public class Students {
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Set<Student> getAllStudents() {
        return DB.getStudents();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response registerNewStudent(Student rawStudentBody, @Context UriInfo uriInfo) throws BadRequestException {
        Student student = DB.addNewStudent(rawStudentBody);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Integer.toString(student.getIndex()));
        return Response.created(builder.build()).entity(student).build();
    }
}
