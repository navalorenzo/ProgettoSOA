package it.unimi.soa;

import it.unimi.soa.rest.as.AuthenticationServlet;
import it.unimi.soa.rest.as.UserDB;
import it.unimi.soa.otp.client.QRCode;
import it.unimi.soa.otp.server.GeneratorsDb;
import it.unimi.soa.rest.registration.RegistrationServlet;
import it.unimi.soa.rest.service.Service;
import it.unimi.soa.rest.service.ServiceHelloServlet;
import it.unimi.soa.rest.tgs.TicketGrantingServlet;
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
        handler.addServletWithMapping(RegistrationServlet.class, "/registration");
        handler.addServletWithMapping(AuthenticationServlet.class, "/auth");
        handler.addServletWithMapping(TicketGrantingServlet.class, "/ticket");
        handler.addServletWithMapping(ServiceHelloServlet.class, "/service/hello");

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8090);
        server.setConnectors(new Connector[]{connector});

        // Init all services and passwords
        SharedPassword.getInstance().initRegistrationPublicAndPrivateKey("/home/lorenzo/Uni/ProgettoSOA/src/Resources/private.der", "/home/lorenzo/Uni/ProgettoSOA/src/Resources/public.der");
        SharedPassword.getInstance().registerAstgskey("passworddelserverauth");
        SharedPassword.getInstance().registerService(Service.HELLO.toString(), "passworddihello");

        server.start();
    }
}