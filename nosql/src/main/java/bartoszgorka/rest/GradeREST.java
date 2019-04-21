package bartoszgorka.rest;

import bartoszgorka.Server;

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
        bartoszgorka.models.Grade g = Server.getDatabase().getGrades(index).stream().filter(grade -> grade.getID() == gradeID).findFirst().orElse(null);
        if (g != null) {
            g.clearLinks();
            return Response.ok(g).build();
        }

        throw new NotFoundException();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @RolesAllowed({"admin", "supervisor"})
    public Response updateGrade(@PathParam("index") int index, @PathParam("ID") int gradeID, bartoszgorka.models.Grade rawGradeBody) throws NotFoundException {
        bartoszgorka.models.Grade g = Server.getDatabase().updateGrade(index, gradeID, rawGradeBody);
        g.clearLinks();
        return Response.ok(g).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @RolesAllowed({"supervisor", "admin"})
    public Response removeGrade(@PathParam("index") int index, @PathParam("ID") int gradeID) throws NotFoundException {
        Server.getDatabase().removeGrade(index, gradeID);
        return Response.noContent().build();
    }
}
