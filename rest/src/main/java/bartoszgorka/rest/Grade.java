package bartoszgorka.rest;

import bartoszgorka.models.DB;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("students/{index}/grades/{ID}")
public class Grade {

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @PermitAll
    public Response getGrade(@PathParam("index") int index, @PathParam("ID") int gradeID) throws NotFoundException {
        bartoszgorka.models.Grade g = DB.getGrades(index).stream().filter(grade -> grade.getID() == gradeID).findFirst().orElse(null);
        if (g != null) {
            return Response.ok(g).build();
        }

        throw new NotFoundException();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @RolesAllowed({"admin", "supervisor"})
    public Response updateGrade(@PathParam("index") int index, @PathParam("ID") int gradeID, bartoszgorka.models.Grade rawGradeBody) throws NotFoundException {
        bartoszgorka.models.Grade g = DB.updateGrade(index, gradeID, rawGradeBody);
        return Response.ok(g).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @RolesAllowed({"supervisor", "admin"})
    public Response removeGrade(@PathParam("index") int index, @PathParam("ID") int gradeID) throws NotFoundException {
        DB.removeGrade(index, gradeID);
        return Response.noContent().build();
    }
}
