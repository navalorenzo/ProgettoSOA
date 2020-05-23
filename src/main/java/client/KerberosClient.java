package main.java.client;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import main.java.message.MessageAuthToken;
import main.java.message.MessageLogin;

import java.util.Scanner;


public class KerberosClient {
    private static String SERVER_URL = "http://localhost";
    private static int SERVER_PORT = 1234;

    private static Client client;

    public static void main(String[] args) {
        // init jersey client
        client = Client.create();

        // ask for authentication
        Scanner reader = new Scanner(System.in);
        System.out.print("Username: ");
        String username = reader.nextLine();
        System.out.print("Password: ");
        String password = reader.nextLine();

        // request auth
        System.out.println(requestAuth(username).authTicket);

    }

    private static MessageAuthToken requestAuth(String username) {
        String path = "/auth/login";
        String argument = new Gson().toJson(new MessageLogin(username));
        WebResource resource = client.resource(SERVER_URL + ":" + SERVER_PORT + path);
        return new Gson().fromJson(resource.type("application/json").post(ClientResponse.class, argument).getEntity(String.class), MessageAuthToken.class);
    }
}
