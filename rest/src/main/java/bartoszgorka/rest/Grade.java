package bartoszgorka.rest;

import bartoszgorka.models.DB;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/students/{index}/grades/{ID}")
public class Grade {

    @GET
    public Response getGrade(@PathParam("index") int index, @PathParam("ID") int gradeID) {
        bartoszgorka.models.Grade g = DB.getGrades(index).stream().filter(grade -> grade.getID() == gradeID).findFirst().orElse(null);
        if (g != null) {
            return Response.ok(g).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response updateGrade(@PathParam("index") int index, @PathParam("ID") int gradeID, bartoszgorka.models.Grade rawGradeBody) {
        bartoszgorka.models.Grade g = DB.updateGrade(index, gradeID, rawGradeBody);
        if (g != null) {
            return Response.ok(g).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    public Response removeGrade(@PathParam("index") int index, @PathParam("ID") int gradeID) {
        boolean success = DB.removeGrade(index, gradeID);

        if (success) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
