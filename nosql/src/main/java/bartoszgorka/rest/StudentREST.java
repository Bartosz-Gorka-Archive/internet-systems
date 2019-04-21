package bartoszgorka.rest;

import bartoszgorka.Server;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/students/{index}")
public class StudentREST {

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @PermitAll
    public Response getStudentByIndex(@PathParam("index") String index) throws NotFoundException {
        int studentIndex = Integer.parseInt(index);
        bartoszgorka.models.Student s = Server.getDatabase().getStudents().stream().filter(student -> student.getIndex() == studentIndex).findFirst().orElse(null);
        if (s != null) {
            s.clearLinks();
            return Response.ok(s).build();
        }

        throw new NotFoundException();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @RolesAllowed("admin")
    public Response updateStudentRecord(@PathParam("index") int index, bartoszgorka.models.Student rawStudentBody) throws NotFoundException {
        bartoszgorka.models.Student student = Server.getDatabase().updateStudent(index, rawStudentBody);
        student.clearLinks();
        return Response.ok(student).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @RolesAllowed("admin")
    public Response removeStudent(@PathParam("index") int index) throws NotFoundException {
        Server.getDatabase().removeStudent(index);
        return Response.noContent().build();
    }
}
