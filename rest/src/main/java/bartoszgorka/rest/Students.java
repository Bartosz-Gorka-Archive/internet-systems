package bartoszgorka.rest;

import bartoszgorka.models.Course;
import bartoszgorka.models.DB;
import bartoszgorka.models.Student;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.*;
import java.util.Set;

@Path("/students")
public class Students {
    @GET
    public Set<Student> getAllStudents() {
        return DB.getStudents();
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response registerNewStudent(Student rawStudentBody, @Context UriInfo uriInfo) {
        Student student = DB.addNewStudent(rawStudentBody);

        if (student != null) {
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Integer.toString(student.getIndex()));
            return Response.created(builder.build()).entity(student).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}
