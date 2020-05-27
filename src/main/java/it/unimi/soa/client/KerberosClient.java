package it.unimi.soa.client;


import com.google.gson.Gson;
import it.unimi.soa.message.as.MessageAuthRequest;
import it.unimi.soa.message.as.MessageAuthResponse;
import it.unimi.soa.message.service.MessageServiceRequest;
import it.unimi.soa.message.service.MessageServiceResponse;
import it.unimi.soa.message.tgs.MessageTGSResponse;
import it.unimi.soa.message.tgs.MessageTGSRequest;
import it.unimi.soa.ticket.AuthenticatorServerTicket;
import it.unimi.soa.utilities.CipherModule;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Scanner;

public class KerberosClient {
    public static void main(String[] args) {
        // Ask for authentication
        Scanner reader = new Scanner(System.in);
        System.out.print("Username: ");
        String username = reader.nextLine();
        System.out.print("Password: ");
        String password = reader.nextLine();
        System.out.print("OTP: ");
        String otp = reader.nextLine();
        System.out.print("Service: ");
        String service = reader.nextLine();

        // Init client
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8090");

        // Request auth
        MessageAuthResponse messageAuthResponse = requestAuth(target, new MessageAuthRequest(username));

        // Extract the ticket and the session key
        byte[] tgsEncryptedTicket = messageAuthResponse.getTgsEncryptedTicket();
        byte[] clientTgsEncryptedSessionKey = messageAuthResponse.getClientTgsEncryptedSessionKey();

        // Decrypt the client-tgs session key in order to encrypt the communication with the tgs
        byte[] clientTgsSessionKey = new byte[0];
        try {
            clientTgsSessionKey = CipherModule.decrypt(password.toCharArray(), clientTgsEncryptedSessionKey);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not decrypt the ticket!");
            System.exit(1);
        }

        // send server-ticket request to the ticket-granting server

        MessageTGSRequest messageTGSRequest = null;
        try {
            messageTGSRequest = new MessageTGSRequest().createJSONToken(username, otp, service, new String(clientTgsSessionKey), tgsEncryptedTicket);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not encrypt the ticket!");
            System.exit(1);
        }

        MessageTGSResponse messageTGSResponse = requestTicket(target, messageTGSRequest);

        // send the service request
        byte[] serviceEncryptedTicket = messageTGSResponse.getServiceEncryptedTicket();
        byte[] clientServerEncryptedSessionKey = messageTGSResponse.getClientServerEncryptedSessionKey();

        // Decrypt the client-tgs session key in order to encrypt the communication with the tgs
        byte[] clientServerSessionKey = new byte[0];
        try {
            clientServerSessionKey = CipherModule.decrypt(new String(clientTgsSessionKey).toCharArray(), clientServerEncryptedSessionKey);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not decrypt the ticket!");
            System.exit(1);
        }

        // Create the message for the service server
        MessageServiceRequest messageServiceRequest = null;
        try {
            messageServiceRequest = new MessageServiceRequest().createJSONToken(username, new String(clientServerSessionKey), serviceEncryptedTicket);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not encrypt the ticket!");
            System.exit(1);
        }

        MessageServiceResponse messageServiceResponse = requestService(target, messageServiceRequest);

        // check service response
        String receivedTimestamp = null;
        String previousTimestamp = null;
        try {
            receivedTimestamp = new String(CipherModule.decrypt(new String(clientServerSessionKey).toCharArray(), messageServiceResponse.getTimestampEncryptedChallenge()));
            previousTimestamp = String.valueOf(new Gson().fromJson(new String(CipherModule.decrypt(new String(clientServerSessionKey).toCharArray(), messageServiceRequest.getClientServerEncryptedSessionKey())), AuthenticatorServerTicket.class).getTimestamp());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not decrypt the ticket!");
            System.exit(1);
        }

        System.out.println("Authenticated: " + receivedTimestamp.equals(previousTimestamp));

        // TODO: supporto per più server
        // send the request to the service server
        //System.out.println(requestService(target, ));

    }

    private static MessageAuthResponse requestAuth(WebTarget target, MessageAuthRequest messageAuthRequest) {
        target = target.path("auth");
        String argument = new Gson().toJson(messageAuthRequest);

        Response response = target.request().post(Entity.entity(argument, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            System.err.println("The request failed: " + response.getStatus());
            System.exit(1);
        }

        return new Gson().fromJson(response.readEntity(String.class), MessageAuthResponse.class);
    }

    private static MessageTGSResponse requestTicket(WebTarget target, MessageTGSRequest messageTGSRequest) {
        target = target.path("ticket");
        String argument = new Gson().toJson(messageTGSRequest);

        Response response = target.request().post(Entity.entity(argument, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            System.err.println("The request failed: " + response.getStatus());
            System.exit(1);
        }

        return new Gson().fromJson(response.readEntity(String.class), MessageTGSResponse.class);
    }

    private static MessageServiceResponse requestService(WebTarget target, MessageServiceRequest messageServiceRequest) {
        target = target.path("service/hello");
        String argument = new Gson().toJson(messageServiceRequest);

        Response response = target.request().post(Entity.entity(argument, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200) {
            System.err.println("The request failed: " + response.getStatus());
            System.exit(1);
        }

        return new Gson().fromJson(response.readEntity(String.class), MessageServiceResponse.class);
    }
}
