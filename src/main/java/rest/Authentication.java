package rest;

import message.MessageLogin;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("auth")
public class Authentication {

    @Path("a")
    @GET
    public Response ciao() {
        System.out.println("iiii");
        return Response.ok("ciao").build();
    }

    @Path("login")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response status(MessageLogin messageLogin) {
        String token = computeToken();
        return Response.ok("ciao").build();
    }


    // utils
    private String computeToken() {
        return null;
    }
}
