package main.java;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;

public class StartServer {

    private static final String HOST = "localhost";
    private static final int PORT = 1234;

    public static void main(String[] args) {
        try {
            // init the server
            HttpServer server = HttpServerFactory.create("http://"+HOST+":"+PORT+"/");
            server.start();

            // log the server status
            System.out.println("Server running!");
            System.out.println("Server started on: http://"+HOST+":"+PORT);

            // safe shutdown
            System.out.println("Hit return to stop...");
            System.in.read();
            System.out.println("Stopping server");
            server.stop(0);
            System.out.println("Server stopped");
            System.exit(0);
        } catch(IOException ioe) {
            System.err.println("Server cannot be started: initialization error");
            ioe.printStackTrace();
        }
    }
}
