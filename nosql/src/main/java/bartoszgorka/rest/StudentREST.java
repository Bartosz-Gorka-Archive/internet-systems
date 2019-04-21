package bartoszgorka.rest;

import bartoszgorka.Server;
import bartoszgorka.models.Student;

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
    public Response getStudentByIndex(@PathParam("index") int index) throws NotFoundException {
        Student student = Server.getDatabase().getStudentByID(index);
        return Response.ok(student).build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @RolesAllowed("admin")
    public Response updateStudentRecord(@PathParam("index") int index, Student body) throws NotFoundException {
        Student student = Server.getDatabase().getStudentByID(index);
        Student updatedStudent = Server.getDatabase().updateStudent(student, body);
        return Response.ok(updatedStudent).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @RolesAllowed("admin")
    public Response removeStudent(@PathParam("index") int index) throws NotFoundException {
        Student student = Server.getDatabase().getStudentByID(index);
        Server.getDatabase().removeStudent(student);
        return Response.noContent().build();
    }
}
