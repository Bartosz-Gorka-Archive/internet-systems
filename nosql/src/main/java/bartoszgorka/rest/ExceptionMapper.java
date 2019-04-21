package bartoszgorka.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Throwable> {
    public Response toResponse(Throwable ex) {
        ex.printStackTrace();

        return Response.status(500)
                .entity(ex.toString())
                .type(MediaType.TEXT_PLAIN).build();
    }
}