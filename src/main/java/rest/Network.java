package main.java.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("network")
public class Network {

    @Path("status")
    @GET
    @Produces("text/plain")
    public String status() {
        // check if the server is online: debugging purpose
        return "online";
    }

}
