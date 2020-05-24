package it.unimi.soa;

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
        handler.addServletWithMapping(BlockingServlet.class, "/status");
        handler.addServletWithMapping(AnotherServlet.class, "/anotherstatus/ciao");


        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8090);
        server.setConnectors(new Connector[]{connector});
        server.start();

    }
}