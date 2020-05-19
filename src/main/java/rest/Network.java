package rest;

import javax.ws.rs.*;

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
