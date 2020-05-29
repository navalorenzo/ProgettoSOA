package it.unimi.soa;

import it.unimi.soa.rest.as.AuthenticationServlet;
import it.unimi.soa.rest.registration.RegistrationServlet;
import it.unimi.soa.rest.service.Service;
import it.unimi.soa.rest.service.ServiceBankServlet;
import it.unimi.soa.rest.service.ServiceHelloServlet;
import it.unimi.soa.rest.tgs.TicketGrantingServlet;
import it.unimi.soa.utilities.SharedPassword;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

import java.util.LinkedHashMap;
import java.util.Map;

public class JettyServer {
    private static Server server;

    private static final int PORT = 8090;
    private static final String KEYS_PATH = "src/resources";

    public static void main(String[] args) throws Exception {

        // Setup handlers
        LinkedHashMap<Class, String> servlets = new LinkedHashMap<>();
        servlets.put(RegistrationServlet.class, "/registration");
        servlets.put(AuthenticationServlet.class, "/auth");
        servlets.put(TicketGrantingServlet.class, "/ticket");
        servlets.put(ServiceHelloServlet.class, "/service/hello");
        servlets.put(ServiceBankServlet.class, "/service/bank");

        server = new Server();
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        for (Map.Entry<Class, String> entry : servlets.entrySet())
            handler.addServletWithMapping(entry.getKey(), entry.getValue());


        ServerConnector connector = new ServerConnector(server);
        connector.setPort(PORT);
        server.setConnectors(new Connector[]{connector});

        // Init all services and passwords
        SharedPassword.getInstance().initRegistrationPublicAndPrivateKey(KEYS_PATH + "/private.der", KEYS_PATH + "/public.der");
        SharedPassword.getInstance().registerAstgskey("passworddelserverauth");
        SharedPassword.getInstance().registerService(Service.HELLO.toString(), "passworddihello");
        SharedPassword.getInstance().registerService(Service.BANK.toString(), "passworddibank");

        server.start();
    }
}