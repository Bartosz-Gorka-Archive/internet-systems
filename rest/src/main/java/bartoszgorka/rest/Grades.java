package bartoszgorka.rest;

import bartoszgorka.models.DB;
import bartoszgorka.models.Grade;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Set;

@Path("/students/{index}/grades")
public class Grades {
    @GET
    public Set<Grade> getAllGradesForStudent(@PathParam("index") int index) {
        return DB.getGrades(index);
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response registerNewGrade(@PathParam("index") int index, Grade rawGradeBody, @Context UriInfo uriInfo) {
        Grade newGrade = DB.registerNewGrade(index, rawGradeBody);

        if (newGrade != null) {
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Integer.toString(newGrade.getID()));
            return Response.created(builder.build()).entity(newGrade).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}