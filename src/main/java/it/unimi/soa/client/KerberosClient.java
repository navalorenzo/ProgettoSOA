package it.unimi.soa.client;


import com.google.gson.Gson;
import it.unimi.soa.message.auth.MessageAuthRequest;
import it.unimi.soa.message.auth.MessageAuthToken;
import it.unimi.soa.message.ticket.MessageTicketRequest;
import it.unimi.soa.message.ticket.UserTicket;
import it.unimi.soa.utilities.CipherModule;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Scanner;

public class KerberosClient {
    public static void main(String[] args) {
        // ask for authentication
        Scanner reader = new Scanner(System.in);
        System.out.print("Username: ");
        String username = reader.nextLine();
        System.out.print("Password: ");
        String password = reader.nextLine();

        // init client
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8090");

        // request auth
        MessageAuthToken messageAuthToken = requestAuth(target, username);
        // extract the ticket
        MessageTicketRequest messageTicketRequest = new Gson().fromJson(messageAuthToken.authTicket, MessageTicketRequest.class);
        // decrypt ticket in order to send it to the ticket-granting server
        try {
            byte[] grantingServerTicket = CipherModule.decrypt(password.toCharArray(), messageTicketRequest.encryptedTicket);
            System.out.println(new String(grantingServerTicket));

        } catch (Exception e) {
            System.err.println("Could not decrypt the ticket");
            e.printStackTrace();
        }

    }

    private static MessageAuthToken requestAuth(WebTarget target, String username) {
        target = target.path("auth");
        String argument = new Gson().toJson(new MessageAuthRequest(username));

        Response response = target.request().post(Entity.entity(argument, MediaType.APPLICATION_JSON));
        if(response.getStatus() != 200) {
            System.err.println("The request failed: " + response.getStatus());
        }

        return new Gson().fromJson(response.readEntity(String.class), MessageAuthToken.class);
    }
}
