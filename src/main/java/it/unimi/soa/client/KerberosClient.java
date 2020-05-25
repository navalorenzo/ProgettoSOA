package it.unimi.soa.client;


import com.google.gson.Gson;
import it.unimi.soa.message.auth.MessageAuthRequest;
import it.unimi.soa.message.auth.MessageAuthToken;
import it.unimi.soa.message.ticket.EncryptedTicket;
import it.unimi.soa.message.ticket.MessageTicketRequest;
import it.unimi.soa.message.ticket.MessageServiceTicket;
import it.unimi.soa.message.ticket.UserTicket;
import it.unimi.soa.service.Service;
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
        System.out.print("Service: ");
        String service = reader.nextLine();

        // init client
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8090");

        // request auth
        MessageAuthToken messageAuthToken = requestAuth(target, new MessageAuthRequest(username));
        // extract the ticket
        EncryptedTicket encryptedTicket = new Gson().fromJson(messageAuthToken.authTicket, EncryptedTicket.class);

        // decrypt ticket in order to send it to the ticket-granting server
        UserTicket userTicket = null;
        try {
            byte[] grantingServerTicket = CipherModule.decrypt(password.toCharArray(), encryptedTicket.encryptedTicket);
            System.out.println(new String(grantingServerTicket));

            userTicket = new Gson().fromJson(new String(grantingServerTicket), UserTicket.class);
        } catch (Exception e) {
            System.err.println("Could not decrypt the ticket");
            System.exit(1);
        }

        // prepare the token for the ticket-granting server
        if(userTicket != null) {
            MessageServiceTicket messageServiceTicket = requestTicket(target, new MessageTicketRequest(username, userTicket.authenticator, Service.valueOf(service), userTicket.encryptedTicket));

            // TODO: supporto per più server
            // send the request to the service server
            System.out.println(requestService(target, messageServiceTicket));
        }

    }

    private static MessageAuthToken requestAuth(WebTarget target, MessageAuthRequest messageAuthRequest) {
        target = target.path("auth");
        String argument = new Gson().toJson(messageAuthRequest);

        Response response = target.request().post(Entity.entity(argument, MediaType.APPLICATION_JSON));
        if(response.getStatus() != 200) {
            System.err.println("The request failed: " + response.getStatus());
            System.exit(1);
        }

        return new Gson().fromJson(response.readEntity(String.class), MessageAuthToken.class);
    }

    private static MessageServiceTicket requestTicket(WebTarget target, MessageTicketRequest messageTicketRequest) {
        target = target.path("ticket");
        String argument = new Gson().toJson(messageTicketRequest);

        Response response = target.request().post(Entity.entity(argument, MediaType.APPLICATION_JSON));
        if(response.getStatus() != 200) {
            System.err.println("The request failed: " + response.getStatus());
            System.exit(1);
        }

        return new Gson().fromJson(response.readEntity(String.class), MessageServiceTicket.class);
    }

    private static String requestService(WebTarget target, MessageServiceTicket messageServiceTicket) {
        target = target.path("service/hello");
        String argument = new Gson().toJson(messageServiceTicket);

        Response response = target.request().post(Entity.entity(argument, MediaType.APPLICATION_JSON));
        if(response.getStatus() != 200) {
            System.err.println("The request failed: " + response.getStatus());
            System.exit(1);
        }

        return response.readEntity(String.class);
    }
}
