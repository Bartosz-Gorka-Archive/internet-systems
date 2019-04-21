package bartoszgorka.rest;

import bartoszgorka.Server;
import bartoszgorka.models.Grade;
import bartoszgorka.models.Student;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/students/{index}/grades/{ID}")
public class GradeREST {
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @PermitAll
    public Response getGrade(@PathParam("index") int index, @PathParam("ID") int gradeID) throws NotFoundException {
        Student student = Server.getDatabase().getStudentByID(index);
        Grade grade = Server.getDatabase().getGradeByID(student, gradeID);
        return Response.ok(grade).build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @RolesAllowed({"admin", "supervisor"})
    public Response updateGrade(@PathParam("index") int index, @PathParam("ID") int gradeID, Grade body) throws NotFoundException {
        Student student = Server.getDatabase().getStudentByID(index);
        Grade grade = Server.getDatabase().getGradeByID(student, gradeID);
        Grade updatedGrade = Server.getDatabase().updateGrade(student, grade, body);
        return Response.ok(updatedGrade).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @RolesAllowed({"supervisor", "admin"})
    public Response removeGrade(@PathParam("index") int index, @PathParam("ID") int gradeID) throws NotFoundException {
        Student student = Server.getDatabase().getStudentByID(index);
        Grade grade = Server.getDatabase().getGradeByID(student, gradeID);
        Server.getDatabase().removeGrade(student, grade);
        return Response.noContent().build();
    }
}
