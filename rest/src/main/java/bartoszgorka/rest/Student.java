package bartoszgorka.rest;

import bartoszgorka.models.DB;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/students/{index}")
public class Student {

    @GET
    public Response getStudentByIndex(@PathParam("index") String index) {
        int studentIndex = Integer.parseInt(index);
        bartoszgorka.models.Student s = DB.getStudents().stream().filter(student -> student.getIndex() == studentIndex).findFirst().orElse(null);
        if (s != null) {
            return Response.ok(s).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response updateStudentRecord(@PathParam("index") int index, bartoszgorka.models.Student rawStudentBody) {
        bartoszgorka.models.Student student = DB.updateStudent(index, rawStudentBody);
        if (student != null) {
            return Response.ok(student).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    public Response removeStudent(@PathParam("index") int index) {
        boolean success = DB.removeStudent(index);

        if (success) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
