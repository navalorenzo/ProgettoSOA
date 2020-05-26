package it.unimi.soa;

import it.unimi.soa.authentication.AuthenticationServlet;
import it.unimi.soa.authentication.UserDB;
import it.unimi.soa.service.Service;
import it.unimi.soa.service.ServiceHelloServlet;
import it.unimi.soa.ticket.TicketGrantingServlet;
import it.unimi.soa.utilities.SharedPassword;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

public class JettyServer {
    private static Server server;

    public static void main(String[] args) throws Exception {
        server = new Server();
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        handler.addServletWithMapping(AuthenticationServlet.class, "/auth");
        handler.addServletWithMapping(TicketGrantingServlet.class, "/ticket");
        handler.addServletWithMapping(ServiceHelloServlet.class, "/service/hello");

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8090);
        server.setConnectors(new Connector[]{connector});

        // Init all services and passwords
        SharedPassword.getInstance().registerAstgskey("passworddelserverauth");
        SharedPassword.getInstance().registerService(Service.HELLO.toString(), "passworddihello");

        // TODO register users
        UserDB.getInstance().register("user", "key");

        server.start();


    }
}

// https://www.baeldung.com/jetty-embedded