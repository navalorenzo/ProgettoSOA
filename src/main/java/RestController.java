
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/hello")
public class RestController {

    private HttpServletRequest Reqq;

    @GET
    @Path("/home")
    @Produces(MediaType.TEXT_PLAIN)
    public Response read(@Context HttpHeaders httpheaders, @Context HttpServletRequest req) {
        Reqq = req;
        return Response.ok("ccc").build();
    }
}
