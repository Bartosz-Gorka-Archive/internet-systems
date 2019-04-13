package bartoszgorka.rest;

import bartoszgorka.models.DB;
import bartoszgorka.models.Grade;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Set;

@Path("/students/{index}/grades")
public class Grades {
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @PermitAll
    public Set<Grade> getAllGradesForStudent(@PathParam("index") int index) {
        return DB.getGrades(index);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @RolesAllowed({"supervisor", "admin"})
    public Response registerNewGrade(@PathParam("index") int index, Grade rawGradeBody, @Context UriInfo uriInfo) throws NotFoundException, BadRequestException {
        Grade newGrade = DB.registerNewGrade(index, rawGradeBody);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Integer.toString(newGrade.getID()));
        return Response.created(builder.build()).entity(newGrade).build();
    }
}