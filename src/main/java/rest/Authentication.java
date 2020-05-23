package main.java.rest;

import com.google.gson.Gson;
import main.java.message.MessageAuthToken;
import main.java.message.MessageLogin;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("auth")
public class Authentication {

    @Path("login")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response status(@Context HttpServletRequest requestContext, MessageLogin messageLogin) {
        System.out.println(requestContext.getRemoteHost() + " - " + requestContext.getRemoteUser() + " - " + requestContext.getRemoteAddr());
        String token = computeToken();
        return Response.ok(new Gson().toJson(new MessageAuthToken(token))).build();
    }


    // utils
    private String computeToken() {
        return null;
    }
}
